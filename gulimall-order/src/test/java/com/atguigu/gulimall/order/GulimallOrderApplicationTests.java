package com.atguigu.gulimall.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;
    /**
     * 1、测试RabbitMQ之如何通过代码的方式创建Exchange、Queue、Bingding绑定关系
     *      方式一：使用RabbitAutoConfiguration自动配置中的AmqpAdmin进行创建
     * 2、测试如何收发消息
     */
    @Test
    public void createExchange() {

    }

}
