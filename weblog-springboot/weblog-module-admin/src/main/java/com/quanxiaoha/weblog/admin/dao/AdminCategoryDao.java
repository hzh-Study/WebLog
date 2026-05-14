package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;

import java.util.Collection;
import java.util.List;

public interface AdminCategoryDao {
    List<CategoryDO> selectAllCategory(Long userId);

    Long selectTotalCount(Long userId);

    List<CategoryDO> selectBatchIds(Collection<Long> ids);
}
