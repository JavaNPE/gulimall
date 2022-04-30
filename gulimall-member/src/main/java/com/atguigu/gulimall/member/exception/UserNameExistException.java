package com.atguigu.gulimall.member.exception;

/**
 * @Author Dali
 * @Date 2022/4/30 20:08
 * @Version 1.0
 * @Description
 */
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名存在");
    }
}
