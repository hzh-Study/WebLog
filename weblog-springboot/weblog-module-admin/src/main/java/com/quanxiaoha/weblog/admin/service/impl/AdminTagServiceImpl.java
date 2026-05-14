package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quanxiaoha.weblog.admin.dao.AdminTagDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.model.vo.tag.SearchTagReqVO;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.admin.model.vo.tag.AddTagReqVO;
import com.quanxiaoha.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.quanxiaoha.weblog.admin.model.vo.tag.QueryTagPageListReqVO;
import com.quanxiaoha.weblog.admin.service.AdminTagService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.model.vo.QuerySelectListRspVO;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class AdminTagServiceImpl extends ServiceImpl<TagMapper, TagDO> implements AdminTagService {

    @Autowired
    private AdminTagDao tagDao;
    @Autowired
    private AdminUserDao userDao;

    private UserDO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserDO userDO = userDao.selectByUsername(username);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        return userDO;
    }

    @Override
    public Response addTags(AddTagReqVO addTagReqVO) {
        List<String> tagNames = addTagReqVO.getTags();
        if (CollectionUtils.isEmpty(tagNames)) {
            return Response.fail("标签名称不能为空");
        }
        UserDO currentUser = getCurrentUser();
        int addedCount = 0;

        for (String tagName : tagNames) {
            QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(TagDO::getUserId, currentUser.getId()).eq(TagDO::getName, tagName).eq(TagDO::getIsDeleted, 0);
            long count = count(wrapper);
            if (count > 0) {
                continue;
            }

            TagDO tagDO = TagDO.builder()
                    .userId(currentUser.getId())
                    .name(tagName)
                    .code(tagName.toLowerCase().replaceAll("[^a-z0-9]+", "-"))
                    .sort(0)
                    .isSystem(false)
                    .status(1)
                    .isDeleted(false)
                    .build();
            save(tagDO);
            addedCount++;
        }
        if (addedCount == 0) {
            return Response.fail("所选标签已全部存在");
        }
        return Response.success();
    }

    @Override
    public Response queryTagPageList(QueryTagPageListReqVO queryTagPageListReqVO) {
        Long current = queryTagPageListReqVO.getCurrent();
        Long size = queryTagPageListReqVO.getSize();
        Date startDate = queryTagPageListReqVO.getStartDate();
        Date endDate = queryTagPageListReqVO.getEndDate();
        String tagName = queryTagPageListReqVO.getTagName();
        boolean isLibrary = Boolean.TRUE.equals(queryTagPageListReqVO.getLibrary());

        Page<TagDO> tagDOPage;
        if (isLibrary) {
            tagDOPage = tagDao.queryTagLibraryPageList(current, size, startDate, endDate, tagName);
        } else {
            Long uid = getCurrentUser().getId();
            tagDOPage = tagDao.queryTagPageList(current, size, startDate, endDate, tagName, uid);
        }

        return Response.success(tagDOPage);
    }

    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        Long tagId = deleteTagReqVO.getTagId();
        TagDO exist = getById(tagId);
        if (exist == null || Boolean.TRUE.equals(exist.getIsDeleted())
                || !getCurrentUser().getId().equals(exist.getUserId())) {
            return Response.fail("无权操作该标签");
        }
        removeById(tagId);
        return Response.success();
    }

    @Override
    public Response searchTags(SearchTagReqVO searchTagReqVO) {
        String key = searchTagReqVO.getKey();
        List<TagDO> tagDOS = tagDao.searchTags(key, getCurrentUser().getId());
        List<QuerySelectListRspVO> selectListRspVOS = null;

        if (!CollectionUtils.isEmpty(tagDOS)) {
            selectListRspVOS = tagDOS.stream()
                    .map(p -> QuerySelectListRspVO.builder().label(p.getName()).value(p.getId()).build())
                    .collect(Collectors.toList());
        }
        return Response.success(selectListRspVOS);
    }

    @Override
    public Response queryTagSelectList() {
        List<TagDO> tagDOS = tagDao.selectAllByUserId(getCurrentUser().getId());
        List<QuerySelectListRspVO> list = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            list = tagDOS.stream()
                    .map(p -> QuerySelectListRspVO.builder()
                            .label(p.getName())
                            .value(p.getId())
                            .build())
                    .collect(Collectors.toList());
        }
        return Response.success(list);
    }
}
