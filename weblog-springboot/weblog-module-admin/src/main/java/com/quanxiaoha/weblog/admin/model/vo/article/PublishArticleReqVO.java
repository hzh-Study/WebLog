package com.quanxiaoha.weblog.admin.model.vo.article;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishArticleReqVO {

    @NotBlank(message = "文章标题不能为空")
    @Length(min = 1, max = 100, message = "文章标题字数需大于 1 小于 100")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String titleImage;

    private String description;

    @NotNull(message = "文章分类不能为空")
    private Long categoryId;

    @NotEmpty(message = "文章标签不能为空")
    private List<Long> tagIds;

    @NotBlank(message = "文章可见性不能为空")
    private String visibility;
}
