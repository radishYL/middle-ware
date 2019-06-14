package com.yl.middleware.rabbitmq.introduction;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yl.middleware.rabbitmq.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Alex
 * @since 2019/6/13 14:14
 */
public class Producer {

    public static void demo_1(String[] args) throws Exception{

        Connection connection = ConnectionFactory.getConnection();

        // 创建channel
        Channel channel = connection.createChannel();

        // 发送数据
        Scanner scanner = new Scanner(System.in);
        String line = null;
        while (!(line = scanner.nextLine()).equals("exit")){
            /**
             * 当exchange为空的时候,默认使用当前虚拟地址的default exchange
             * 当使用default exchange的时候,routingKey必须完全匹配队列名,不然会丢弃消息
              */
            channel.basicPublish("", "demo", null, line.getBytes());
        }
       connection.close();
    }

    public static void demo_2(String[] args) throws Exception{
        Connection connection = ConnectionFactory.getConnection();

        // 创建channel
        Channel channel = connection.createChannel();

        String exchangeName = "dev_demo";
        String routingKey = "dev.direct";
        // 发送数据
        Scanner scanner = new Scanner(System.in);
        String line = null;
        while (!(line = scanner.nextLine()).equals("exit")){
            /**
             * 发送到指定的Exchange上,根据routingKey找到与这个exchange绑定的队列
             * 交换机与队列之间通过路由键进行绑定(多对多的关系)
             * 如果在指定exchange下未找到与之使用指定routingKey绑定的队列,则消息会被丢弃
             */
            channel.basicPublish(exchangeName, routingKey, null, line.getBytes());
        }
        connection.close();
    }

    public static void demo_3(String[] args) throws Exception{
        Connection connection = ConnectionFactory.getConnection();

        // 创建channel
        Channel channel = connection.createChannel();

        String exchangeName = "dev_fanout_exchange";
        // 发送数据
        Scanner scanner = new Scanner(System.in);
        String line = null;
        while (!(line = scanner.nextLine()).equals("exit")){
            /**
             * 发送到指定的Exchange上,会将消息发送到所有与该交换机绑定的队列上,不处理路由键
             * 可不设置路由键
             */
            channel.basicPublish(exchangeName, "", null, line.getBytes());
        }
        connection.close();
    }

    public static void main(String[] args) throws Exception{
        Connection connection = ConnectionFactory.getConnection();

        // 创建channel
        Channel channel = connection.createChannel();

        String exchangeName = "dev_fanout_exchange";
        // 发送数据
        Map<String,Object> headers = new HashMap<>();
        headers.put("user", "miss");
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
            .deliveryMode(2)//1-不持久化消息 2-持久化消息
            .contentEncoding("UTF-8")// 编码类型
            .expiration("15000")// 过期时间-10s
            .headers(headers)
            .build();
        Scanner scanner = new Scanner(System.in);
        String line = null;
        while (!(line = scanner.nextLine()).equals("exit")){
            /**
             * 发送到指定的Exchange上,会将消息发送到所有与该交换机绑定的队列上,不处理路由键
             * 可不设置路由键
             */
            channel.basicPublish(exchangeName, "", properties, line.getBytes());
        }
        connection.close();
    }
}
