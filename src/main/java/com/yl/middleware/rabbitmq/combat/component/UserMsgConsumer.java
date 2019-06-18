package com.yl.middleware.rabbitmq.combat.component;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * @author Alex
 * @since 2019/6/18 12:24
 */
@Component
public class UserMsgConsumer //implements ChannelAwareMessageListener
{

    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "user_reg_queue",durable = "true",autoDelete = "false"
            ,arguments = {@Argument(name = "x-dead-letter-exchange",value = "x-dead-letter-exchange")}),
        exchange = @Exchange(value = "user_exchange",durable = "true",autoDelete = "false",type = "topic"),
        key = "user.reg.#"
    ))
    //@Override
    public void onMessage(Message message, Channel channel) throws Exception {

        System.err.println("UserMsgConsumer consume msg:" + message);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);


    }
}

