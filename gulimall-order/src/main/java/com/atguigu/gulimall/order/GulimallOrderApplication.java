package com.atguigu.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 使用 RabbitMQ步骤
 * 1、引入spring- boot-starter-amqp的pox依赖（俗称场景启动器）RabbitAutoConfiguration就会自动生效
 * 2、给容器中自动配置了
 *      RabbitTemplate、AmqpAdmin、CachingConnectionFactory、 RabbitMessagingTemplatej
 *      所有的属性都是spring. rabbitmq
 *          @ConfigurationProperties(prefix = "spring. rabbi tmq")
 *          public class RabbitProperties
 *
 * 3、给配置文件中配置spring.rabbitmq 信息
 * 4、@EnableRabbit: @EnabLeXxxxx开启功能
 * 5、监听消息:使用@RabbitListener;必须有@EnableRabbit
 *  @RabbitListener:类+方法上(监听哪些队列)
 *  @RabbitHandler:标在方法上（重载区分不同的消息）
 */
@SpringBootApplication
@EnableRabbit
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
