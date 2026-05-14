package com.quanxiaoha.weblog.admin.model.vo.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateTagReqVO {
    @NotEmpty(message = "标签ID列表不能为空")
    private List<Long> tagIds;
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
}
