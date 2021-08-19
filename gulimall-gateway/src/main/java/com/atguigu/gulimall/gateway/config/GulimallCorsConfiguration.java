package com.atguigu.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

/**
 * @Author Dali
 * @Date 2021/8/19 21:26
 * @Version 1.0
 * @Description: 解决跨域问题
 */
@Configuration // gateway
public class GulimallCorsConfiguration {
    @Bean  // 添加过滤器
    public CorsWebFilter corsWebFilter() {
        // 基于url跨域，选择reactive包下的
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 跨域配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //1、配置跨域问题
        corsConfiguration.addAllowedHeader("*");    // 允许跨域的头
        corsConfiguration.addAllowedMethod("*");    // 允许跨域的请求方式
        corsConfiguration.addAllowedOrigin("*");    // 允许跨域的请求来源
        corsConfiguration.setAllowCredentials(true);    // 是否允许携带cookie跨域

        source.registerCorsConfiguration("/**", corsConfiguration); // 任意url都要进行跨域配置
        return new CorsWebFilter(source);
    }
}
