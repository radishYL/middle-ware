package com.yl.middleware.rabbitmq.advance;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.yl.middleware.rabbitmq.ConnectionFactory;

/**
 * @author Alex
 * @since 2019/6/14 16:46
 */
public class Consumer {

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
        // 创建消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 消费
        channel.basicConsume(queue.getQueue(), false, consumer);
        while (true){

            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            System.err.println("收到消息:" + new String(delivery.getBody()));


        }


    }

}
