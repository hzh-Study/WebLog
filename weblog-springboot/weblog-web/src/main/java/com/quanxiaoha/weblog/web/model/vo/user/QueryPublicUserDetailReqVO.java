package com.quanxiaoha.weblog.web.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryPublicUserDetailReqVO {
    @NotBlank(message = "用户名不能为空")
    private String username;
}
