package com.atguigu.gulimall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author Dali
 * @Date 2022/4/23 17:34
 * @Version 1.0
 * @Description
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage() {
        return "login";
    }

    /**
     * http://auth.gulimall.com/reg.html
     *
     * @return
     */
    @GetMapping("/reg.html")
    public String regPage() {
        return "reg";
    }
}
