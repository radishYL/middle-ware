package com.yl.middleware.rabbitmq.combat.component;

import  org.springframework.messaging.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author Alex
 * @since 2019/6/18 11:02
 */
@Component
public class RabbitSender {

    @Autowired
    public RabbitTemplate rabbitTemplate;

    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.err.println("data:" + correlationData + ",ack:" + ack + ",cause:" + cause);
        }
    };

    final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback(){
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("message:" + message);
            System.err.println("replyText:" +replyText + ",exchange:" + exchange + ",routingKey:" + routingKey);
        }
    };

    public void send(String exchangeName,String routingKey,Object message, Map<String,Object> properties){
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        Message<Object> msg = MessageBuilder.createMessage(message, new MessageHeaders(properties));
        // 真实情况下CorrelationData的id必须为全局唯一的业务Id
        rabbitTemplate.convertAndSend(exchangeName, routingKey, msg,new CorrelationData(UUID.randomUUID().toString()));
    }
}
