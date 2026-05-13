package com.quanxiaoha.weblog.common.enums;

import com.quanxiaoha.weblog.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-18 8:14
 * @description: 响应枚举
 **/
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中"),
    PARAM_ERROR("10001", "参数错误"),

    // ----------- 业务异常状态码 -----------
    UNAUTHORIZED("10002", "无访问权限，请先登录！"),
    FORBIDDEN("10003", "演示账号仅支持查询操作！"),
    NO_TOKEN_OR_TOKEN_INVALID("10004", "Token 丢失或 Token 不可用！"),
    LOGIN_FAIL("10005", "登录失败"),
    USERNAME_OR_PWD_ERROR("10006", "用户名或密码错误"),
    UPLOAD_FILE_ERROR("10007", "文件上传失败"),
    DUPLICATE_TAG_ERROR("10008", "提交的部分标签已被创建"),
    DUPLICATE_CATEGORY_ERROR("10009", "该分类已被创建"),
    TOKEN_EXPIRED("10010", "Token 已过期"),
    USERNAME_ALREADY_EXISTS("10011", "用户名已存在"),
    USER_NOT_FOUND("10012", "用户不存在"),
    ARTICLE_NOT_FOUND("10013", "文章不存在"),
    ARTICLE_ACCESS_DENIED("10014", "无权访问该文章"),
    PASSWORD_ERROR("10015", "旧密码错误"),

    /**
     * code: 20001 <br/>
     * info: 访客IP归属地查询失败
     */
    AGENT_REGION_SEARCH_ERROR("20001", "访客IP归属地查询失败"),

    // ----------- AI 功能异常状态码 -----------
    AI_QUOTA_EXCEEDED("30001", "今日AI文章生成次数已用完"),
    AI_INTERVAL("30002", "AI功能使用间隔不足，请稍后再试"),
    AI_TOKEN_EXCEEDED("30003", "今日AI Token额度不足"),
    AI_ERROR("30004", "AI服务调用失败，请稍后重试"),

    // ----------- 文章版本异常状态码 -----------
    VERSION_NOT_FOUND("40002", "版本不存在"),
    VERSION_ROLLBACK_FAILED("40003", "版本回滚失败"),

    // ----------- 文章互动异常状态码 -----------
    LIKE_DUPLICATE("50001", "已点赞，不可重复"),
    FAVORITE_DUPLICATE("50002", "已收藏，不可重复"),
    ARTICLE_NOT_PUBLIC("50003", "该文章未公开，无法互动"),
    ;

    private String errorCode;
    private String errorMessage;

}
