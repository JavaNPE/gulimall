package com.atguigu.gulimall.member.exception;

/**
 * @Author Dali
 * @Date 2022/4/30 20:09
 * @Version 1.0
 * @Description
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号存在");
    }
}
