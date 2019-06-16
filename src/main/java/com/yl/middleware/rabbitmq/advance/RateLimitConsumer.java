package com.yl.middleware.rabbitmq.advance;

import com.rabbitmq.client.*;
import com.yl.middleware.rabbitmq.ConnectionFactory;

import java.io.IOException;

/**
 * @author yu.alex
 * @date 2019/6/16 11:00
 * @des Rabbitmq消费端限流
 */
public class RateLimitConsumer extends DefaultConsumer {


    public RateLimitConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        System.err.println("收到消息");
        System.err.println("consumerTag:" + consumerTag);
        System.err.println("envelope:" + envelope);
        System.err.println("properties:" + properties);
        System.err.println("body:" + new String(body));
        System.err.println("----------------------");

        this.getChannel().basicAck(envelope.getDeliveryTag(), false);

        //收到NACK表示消息签收失败,参数:消息表示、是否批量、是否重回队列(即将消息重新发送回队列,重回的消息放在队列的最尾端)
        //this.getChannel().basicNack(envelope.getDeliveryTag(), false, true);
    }

    public static void main(String[] args) throws Exception{

        Connection connection = ConnectionFactory.getConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "dev_direct_exchange";
        String routingKey = "confirm.save";
        String exchangeType = "direct";
        String queueName = "confirm.rateLimit";

        channel.exchangeDeclare(exchangeName,exchangeType,true,false,null);
        AMQP.Queue.DeclareOk queue = channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName,exchangeName,routingKey);

        /**
         * 限流设置
         *  1.设置autoAck为false,消费消息之后手动确认
         *  2.设置消息接收规则 参数:拉取消息大小(0表示不限制)、拉取消息个数、是否应用到channel级别,false表示应用到消费者级别
         */
        channel.basicQos(0,1,false);
        channel.basicConsume(queueName,false,new RateLimitConsumer(channel));

    }

}
