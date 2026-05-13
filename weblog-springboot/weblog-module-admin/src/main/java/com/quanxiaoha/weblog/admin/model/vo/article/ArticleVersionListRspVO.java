package com.quanxiaoha.weblog.admin.model.vo.article;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVersionListRspVO {
    private List<VersionItemVO> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VersionItemVO {
        private Long versionId;
        private Integer versionNum;
        private String title;
        private String changeSummary;
        private String createTime;
    }
}