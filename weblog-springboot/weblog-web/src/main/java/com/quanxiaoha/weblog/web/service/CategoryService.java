package com.quanxiaoha.weblog.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;


public interface CategoryService extends IService<CategoryDO> {
    /**
     * 全站「公开文章」下按篇数降序的前 10 个分类，供首页侧栏等公共区域。
     */
    Response queryCategoryList();

    /**
     * 全站有公开文章关联的全部分类（按篇数降序），供分类总览页。
     */
    Response queryCategoryListAll();
}
