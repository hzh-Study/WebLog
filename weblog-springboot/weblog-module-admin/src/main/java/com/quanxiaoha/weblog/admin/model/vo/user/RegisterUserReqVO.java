package com.quanxiaoha.weblog.admin.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserReqVO {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 20, message = "用户名长度需在 3 到 20 个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 32, message = "密码长度需在 6 到 32 个字符之间")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Length(min = 1, max = 20, message = "昵称长度需在 1 到 20 个字符之间")
    private String nickname;
}
