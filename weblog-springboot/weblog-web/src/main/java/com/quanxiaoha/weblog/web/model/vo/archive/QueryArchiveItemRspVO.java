package com.quanxiaoha.weblog.web.model.vo.archive;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.quanxiaoha.weblog.web.model.vo.user.ArticleAuthorRspVO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryArchiveItemRspVO {
    private Long id;
    private String titleImage;
    private String title;
    private String createMonth;
    private String createTime;
    private ArticleAuthorRspVO author;
}
