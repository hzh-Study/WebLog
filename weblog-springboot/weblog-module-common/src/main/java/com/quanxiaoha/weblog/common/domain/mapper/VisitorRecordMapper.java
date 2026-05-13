package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.VisitorRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VisitorRecordMapper extends BaseMapper<VisitorRecordDO> {
}