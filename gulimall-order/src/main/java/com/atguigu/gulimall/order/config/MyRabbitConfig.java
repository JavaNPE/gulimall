package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author Dali
 * @Date 2022/6/3 15:40
 * @Version 1.0
 * @Description: RabbitMQ消息格式转换组件
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * RabbitMQ消息格式转换组件
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * 案例1： 服务器收到消息就回调，也就是p-b：confirmCallback阶段
     *      步骤一： 开启发送端确认 spring.rabbitmq.publisher-confirms=true
     *      步骤二： 设置确认回调
     * 案例2： 消息正确抵达队列进行回调，也就是所谓的e-q：returnCallback阶段
     *      步骤一： 配置spring.rabbitmq.publisher-returns=true, spring.rabbitmq.template.mandatory=true
     *      步骤二： 设置确认回调ReturnCallback
     *  案例3、消费端确认(保证每个消息被正确消费，此时才可以broker删除这个消息)。
     */
    @PostConstruct // MyRabbitConfig对象创建完成之后，才执行这个方法
    public void initRabbitTemplate() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *  只要消息抵达Broker代理服务器那么ack返回的就是true
             * @param correlationData 当前消息的唯一关联数据（这个消息的唯一id）
             * @param ack 消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm...correlationData[" + correlationData + "]==>ack[" + ack + "]==>cause[" + cause + "]");
            }
        });

        // 设置消息抵达队列的确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             *  只要消息没有投递给指定的队列，就触发这个失败回调
             *
             * @param message   消息投递失败的详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange  当时这个消息发给哪个交换机
             * @param routingKey    当时这个消息用的哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
                                        String routingKey) {
                System.out.println("Fail Message[" + message + "]==>replyCode[" + replyCode + "]==>replyText[" + replyText +
                        "]==>exchange[" + exchange + "]==>routingKey[" + routingKey + "]");
            }
        });
    }
}
