package com.quanxiaoha.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryPublicUserDetailRspVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String bio;
    private String website;
    private String githubHome;
    private String giteeHome;
    private String csdnHome;
    private String zhihuHome;
    private Long publicArticleCount;
}
