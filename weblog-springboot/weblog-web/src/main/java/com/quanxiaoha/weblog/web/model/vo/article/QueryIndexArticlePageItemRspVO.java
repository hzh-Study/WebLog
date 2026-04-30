package com.quanxiaoha.weblog.web.model.vo.article;


import com.quanxiaoha.weblog.web.model.vo.category.QueryCategoryListItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.tag.QueryTagListItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.ArticleAuthorRspVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryIndexArticlePageItemRspVO {
    private Long id;
    private String titleImage;
    private String title;
    private String createTime;
    private String description;
    private String searchSnippet;
    private QueryCategoryListItemRspVO category;
    private List<QueryTagListItemRspVO> tags;
    private ArticleAuthorRspVO author;
}
