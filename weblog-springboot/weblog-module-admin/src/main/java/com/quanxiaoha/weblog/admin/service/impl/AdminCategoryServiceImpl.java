package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quanxiaoha.weblog.admin.dao.AdminCategoryDao;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.mapper.CategoryMapper;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.admin.model.vo.category.AddCategoryReqVO;
import com.quanxiaoha.weblog.admin.model.vo.category.DeleteCategoryReqVO;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.model.vo.category.QueryCategoryPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.category.QueryCategoryPageListRspVO;
import com.quanxiaoha.weblog.admin.service.AdminCategoryService;
import com.quanxiaoha.weblog.common.PageResponse;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class AdminCategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryDO> implements AdminCategoryService {

    @Autowired
    private AdminCategoryDao categoryDao;
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
    public Response addCategory(AddCategoryReqVO addCategoryReqVO) {
        String name = addCategoryReqVO.getName();
        UserDO currentUser = getCurrentUser();

        QueryWrapper<CategoryDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CategoryDO::getUserId, currentUser.getId()).eq(CategoryDO::getName, name).eq(CategoryDO::getIsDeleted, 0);
        long count = count(wrapper);
        if (count > 0) {
            return Response.fail("该分类已存在");
        }

        CategoryDO categoryDO = CategoryDO.builder()
                .userId(currentUser.getId())
                .parentId(0L)
                .name(name)
                .code(name.toLowerCase().replaceAll("[^a-z0-9]+", "-"))
                .level(1)
                .sort(0)
                .isSystem(false)
                .status(1)
                .isDeleted(false)
                .build();
        save(categoryDO);
        return Response.success();
    }

    @Override
    public PageResponse queryCategoryPageList(QueryCategoryPageListReqVO queryCategoryPageListReqVO) {
        Long current = queryCategoryPageListReqVO.getCurrent();
        Long size = queryCategoryPageListReqVO.getSize();
        Page<CategoryDO> page = new Page<>(current, size);
        QueryWrapper<CategoryDO> wrapper = new QueryWrapper<>();

        String categoryName = queryCategoryPageListReqVO.getCategoryName();
        Date startDate = queryCategoryPageListReqVO.getStartDate();
        Date endDate = queryCategoryPageListReqVO.getEndDate();
        boolean isLibrary = Boolean.TRUE.equals(queryCategoryPageListReqVO.getLibrary());
        wrapper.lambda()
                .eq(isLibrary, CategoryDO::getIsSystem, true)
                .eq(!isLibrary, CategoryDO::getUserId, getCurrentUser().getId())
                .eq(CategoryDO::getIsDeleted, 0)
                .like(Objects.nonNull(categoryName), CategoryDO::getName, categoryName)
                .ge(Objects.nonNull(startDate), CategoryDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), CategoryDO::getCreateTime, endDate)
                .orderByDesc(CategoryDO::getCreateTime);

        Page<CategoryDO> categoryDOPage = page(page, wrapper);

        List<CategoryDO> categoryDOS = categoryDOPage.getRecords();

        List<QueryCategoryPageListRspVO> queryCategoryPageListRspVOS = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            queryCategoryPageListRspVOS = categoryDOS.stream()
                    .map(p -> QueryCategoryPageListRspVO.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .createTime(p.getCreateTime())
                            .build())
                    .collect(Collectors.toList());

        }

        return PageResponse.success(categoryDOPage, queryCategoryPageListRspVOS);
    }

    @Override
    public Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO) {
        Long categoryId = deleteCategoryReqVO.getCategoryId();
        CategoryDO exist = getById(categoryId);
        if (exist == null || Boolean.TRUE.equals(exist.getIsDeleted())
                || !getCurrentUser().getId().equals(exist.getUserId())) {
            return Response.fail("无权操作该分类");
        }
        removeById(categoryId);
        return Response.success();
    }

    @Override
    public Response queryCategorySelectList() {
        List<CategoryDO> categoryDOList = categoryDao.selectAllCategory(getCurrentUser().getId());
        List<QuerySelectListRspVO> list = null;
        if (!CollectionUtils.isEmpty(categoryDOList)) {
            list = categoryDOList.stream()
                    .map(p -> QuerySelectListRspVO.builder()
                            .label(p.getName())
                            .value(p.getId())
                            .build())
                    .collect(Collectors.toList());
        }
        return Response.success(list);
    }
}
