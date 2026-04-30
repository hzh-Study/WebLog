package com.quanxiaoha.weblog.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;


public interface TagService extends IService<TagDO> {
    /**
     * 全站按公开文章数降序的前 10 个标签（公共区域入口）。
     */
    Response queryTagList();

    /**
     * 有公开文章关联的全部标签，按篇数降序，供标签总览页。
     */
    Response queryTagListAll();
}
