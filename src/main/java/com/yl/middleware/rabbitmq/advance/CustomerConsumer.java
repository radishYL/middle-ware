package com.yl.middleware.rabbitmq.advance;

import com.rabbitmq.client.*;
import com.yl.middleware.rabbitmq.ConnectionFactory;

import java.io.IOException;

/**
 * @author Alex
 * @since 2019/6/14 17:53
 * 自定义消费端
 */
public class CustomerConsumer extends DefaultConsumer {


    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public CustomerConsumer(Channel channel) {
        super(channel);
    }

    /**
     * 真正处理消息回调的函数
     * @param consumerTag
     * @param envelope
     * @param properties
     * @param body
     * @throws IOException
     */
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        System.err.println("收到消息");
        System.err.println("consumerTag:" + consumerTag);
        System.err.println("envelope:" + envelope);
        System.err.println("properties:" + properties);
        System.err.println("body:" + new String(body));
        System.err.println("----------------------");
    }

    public static void main(String[] args) throws Exception{

        // 获取连接
        Connection connection = ConnectionFactory.getConnection();

        // 创建channel
        Channel channel = connection.createChannel();

        String exchangeName = "dev_direct_exchange";
        String routingKey = "confirm.save";
        String exchangeType = "direct";

        /**
         * 以下声明代码可以放在Producer端,也可以放在Consumer端
         * 但是路由键的绑定只能在一端设置,否则会重复
         */
        // 定义exchange
        channel.exchangeDeclare(exchangeName, exchangeType, true);
        // 定义queue
        AMQP.Queue.DeclareOk queue = channel.queueDeclare("confirm_save_queue", true, false, false, null);
        // 绑定
        channel.queueBind(queue.getQueue(), exchangeName, routingKey);
        // 消费,使用自定义消费者进行消息消费,更优雅
        channel.basicConsume(queue.getQueue(), false, new CustomerConsumer(channel));

    }
}

