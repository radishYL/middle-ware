package com.yl.test.spring;

import com.yl.middleware.rabbitmq.combat.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Scanner;

/**
 * @author Alex
 * @since 2019/6/17 14:03
 */
@SpringBootTest(classes = {RabbitmqConfig.class})
@RunWith(SpringRunner.class)
public class SpringAmqpTest {


    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private Exchange exchange_user;
    @Autowired
    private Exchange deadLetterExchange;

    @Test
    public void demo_1() throws Exception{

        int i = 0;
        while (i<10){
            MessageProperties messageProperties = new MessageProperties();
            Message message = new Message((i++ + ".msg").getBytes(), messageProperties);
            rabbitAdmin.getRabbitTemplate().convertAndSend(exchange_user.getName(), "user.buy", message,
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        message.getMessageProperties().setExpiration("10000");
                        return message;
                    }
                });
            rabbitAdmin.getRabbitTemplate().convertAndSend(deadLetterExchange.getName(), "", message,
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        message.getMessageProperties().setExpiration("10000");
                        return message;
                    }
                });
        }
        Thread.currentThread().sleep(60*10000);
    }

}
