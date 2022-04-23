package com.atguigu.gulimall.auth.controller.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author Dali
 * @Date 2022/4/23 18:22
 * @Version 1.0
 * @Description: 使用SpringMVC中的试图映射，就不需要在LoginController中写空方法进行页面跳转了
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    /**
     * 试图映射
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        /**
         *      @GetMapping("/login.html")
         *      public String loginPage() {
         *          return "login";
         *      }
         *
         *      http://auth.gulimall.com/reg.html
         *
         *      @GetMapping("/reg.html")
         *      public String regPage () {
         *      return "reg";
         *      }
         */
        // 此方法替换上面LoginController中的空方法
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
