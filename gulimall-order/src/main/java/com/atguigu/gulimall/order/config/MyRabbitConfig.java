package com.atguigu.gulimall.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dali
 * @Date 2022/6/3 15:40
 * @Version 1.0
 * @Description: RabbitMQ消息格式转换组件
 */
@Configuration
public class MyRabbitConfig {

    /**
     * RabbitMQ消息格式转换组件
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
