package com.atguigu.gulimall.order;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessageTemplate() {
        // 发送消息，如果发送的消息是个对象，我们会使用序列化机制，将对象写出去。对象必须实现Serializable
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setCreateTime(new Date());
        reasonEntity.setName("绿妹");
        //String jsonString = JSONObject.toJSONString(reasonEntity);
        // 1、发送消息
        String msg = "Hello World!";
        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity);
        log.info("发送的消息内容为：【{}】", reasonEntity);
    }

    /**
     * 1、测试RabbitMQ之如何通过代码的方式创建Exchange[hello.java.exchange]、Queue、Bingding绑定关系
     * 方式一：使用RabbitAutoConfiguration自动配置中的AmqpAdmin进行创建
     * 2、测试如何收发消息
     */
    // public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
    @Test
    public void createExchange() {
        // 传个名字就可以了 后面那2个值都是默认设置了的
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功", "hello-java-exchange");
    }

    /**
     * 创建队列
     */
    @Test
    public void createQueue() {
        // public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue队列[{}]创建成功", "hello-java-queue");
    }

    /**
     * 创建交换机hello-java-exchange与队列hello-java-queue之间的绑定关系
     */
    @Test
    public void createBinding() {
        // import org.springframework.amqp.core.Binding;
        // public Binding(String destination【目的地】,
        // DestinationType destinationType【目的地类型】,
        // String exchange【交换机】,
        // String routingKey【路由键】,
        // Map<String, Object> arguments【自定义参数】)
        // 将exchange指定的交换机和destination目的地进行绑定，使用routingKey作为指定的路由键
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE, "hello-java-exchange",
                "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding绑定关系[{}]创建成功", "hello-java-binding");
    }
}
