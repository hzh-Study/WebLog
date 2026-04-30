package com.quanxiaoha.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleAuthorRspVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
}
