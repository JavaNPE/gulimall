package com.atguigu.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author Dali
 * @Date 2022/4/24 11:31
 * @Version 1.0
 * @Description：  接收注册页面用户提交的数据
 *      后端使用JSR303对前端页面传过来的数据进行数据校验；
 *      前端校验，后端不校验，通过postman绕过前端岂不是随便搞.
 */
@Data
public class UserRegistVo {
    // 使用JSR303对前端页面传过来的数据进行校验（原则上前后端都需要校验）
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 2, max = 8, message = "用户名必须是2-8位字符")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    private String password;

    @NotEmpty(message = "手机号不能为空")
    // 手机号正则表达式：百度搜最新的即可 ^1[3456789]\d{9}$
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String code;
}
