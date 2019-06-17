package com.yl.middleware.rabbitmq.combat.component.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author Alex
 * @since 2019/6/17 16:13
 */
public class CustomerMessageConvert implements MessageConverter {

    /**
     * 将java对象转为message
     */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(object.toString().getBytes(), messageProperties);
    }

    /**
     * 将message转成java对象
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        MsgInfo info = new MsgInfo();
        info.setQueue(message.getMessageProperties().getConsumerQueue());
        info.setBody(new String(message.getBody()));
        return info;
    }
}
