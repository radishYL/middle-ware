package com.yl.middleware.rabbitmq.advance;

import com.rabbitmq.client.*;
import com.yl.middleware.rabbitmq.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author Alex
 * @since 2019/6/14 16:45
 */
public class Producer {

    public static void main(String[] args) throws Exception{

        // 获取连接
        Connection connection = ConnectionFactory.getConnection();

        // 创建channel
        Channel channel = connection.createChannel();

        // 指定消息投递模式:消息确认模式
        channel.confirmSelect();
        // 添加确认监听,用于监听消息是否送达到exchange
        channel.addConfirmListener(new ConfirmListener() {
            // 确认成功 参数:1-唯一消息标签
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.err.println("消息ACK:" + deliveryTag);
            }

            // 确认失败
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.err.println("ConfirmListener NO ACK:" + deliveryTag);
            }
        });

        /**
         * 不可达消息监听,用于监听消息是否送达队列,比如路由键找不到队列等
         * 注意:
         *      1.exchange不存在会报错
         */
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("ReturnListener Return err:" + replyCode);
            }
        });

        String exchangeName = "dev_direct_exchange";
        String routingKey = "confirm.save";

        Scanner scanner = new Scanner(System.in);

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
            .deliveryMode(2)//1-不持久化消息 2-持久化消息
            .contentEncoding("UTF-8")// 编码类型
            .expiration("15000")// 过期时间-10s
            .messageId("10010")
            .build();

        String line = null;
        while (!(line = scanner.nextLine()).equals("exit")){
            /**
             * 当使用ReturnListener的时候,mandatory必须设置为true
             * 不然Broker端会自动删除不可达的消息
             */
            channel.basicPublish(exchangeName, routingKey,true, properties, line.getBytes());
        }
        connection.close();

    }

}
