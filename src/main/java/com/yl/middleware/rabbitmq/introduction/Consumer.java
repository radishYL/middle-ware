package com.yl.middleware.rabbitmq.introduction;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.yl.middleware.rabbitmq.ConnectionFactory;
import org.junit.jupiter.api.Test;

/**
 * @author Alex
 * @since 2019/6/13 14:15
 */
public class Consumer {

    public static void main(String[] args) throws Exception{
        Connection connection = ConnectionFactory.getConnection();

        // 创建channel
        Channel channel = connection.createChannel();

        // (声明)创建队列 队列名、是否持久化(重启服务器队列仍然存在)、是否独占(改队列只允许当前连接使用)、自动删除、拓展参数
        AMQP.Queue.DeclareOk queue = channel.queueDeclare("demo", true, false, false, null);

        // 创建消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);

        // 设置channel 队列名、是否主动签收消息、消费者
        channel.basicConsume(queue.getQueue(), true, consumer);

        while (true){

            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            System.err.println("consumer收到消息:" + new String(delivery.getBody()));

        }


    }


    @Test
    public void demo_1() throws Exception{

        Connection connection = ConnectionFactory.getConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "dev_direct_exchange";
        String exchangeType = "direct";
        String queueName = "dev-topic-queue";
        String routingKey = "dev.direct";

        // 声明一个交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, false,false, null);
        // 声明一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        // 建立exchange与queue的绑定关系
        channel.queueBind(queueName, exchangeName, routingKey);

        // 设置channel 队列名、是否主动签收消息、消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        while (true){

            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            System.err.println("consumer收到消息:" + new String(delivery.getBody()));

        }


    }

    @Test
    public void demo_2() throws Exception{

        Connection connection = ConnectionFactory.getConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "dev_topic_exchange";
        String exchangeType = "topic";
        String queueName = "dev-topic-queue";
        // 根据模糊匹配规则,routingKey可以匹配所有已dev.topic.开头的路由键
        String routingKey = "dev.topic.#";

        // 声明一个交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, false,false, null);
        // 声明一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        // 建立exchange与queue的绑定关系
        channel.queueBind(queueName, exchangeName, routingKey);

        // 设置channel 队列名、是否主动签收消息、消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        while (true){

            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            System.err.println("consumer收到消息:" + new String(delivery.getBody()));

        }

    }


    @Test
    public void demo_3() throws Exception{

        Connection connection = ConnectionFactory.getConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "dev_fanout_exchange";
        String exchangeType = "fanout";
        String queueName = "dev-fanout-queue";
        // 此处的路由键只是进行exchange和queue的绑定,所有发送到改交换机的消息会被转发到所有与该交换机绑定的队列中
        String routingKey = "dev.fanout";

        // 声明一个交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, false,false, null);
        // 声明一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        // 建立exchange与queue的绑定关系
        channel.queueBind(queueName, exchangeName, routingKey);

        // 设置channel 队列名、是否主动签收消息、消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        while (true){
            System.err.println("consumer开始拉取消息");

            // 此处是一个阻塞的过程,当队列无消息的时候,会阻塞
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            AMQP.BasicProperties properties = delivery.getProperties();
            System.err.println(properties.getContentEncoding());
            System.err.println(properties.getDeliveryMode());
            System.err.println(properties.getExpiration());
            System.err.println(properties.getHeaders());

            System.err.println("consumer收到消息:" + new String(delivery.getBody()));

        }

    }
}
