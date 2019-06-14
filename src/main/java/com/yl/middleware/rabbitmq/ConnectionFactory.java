package com.yl.middleware.rabbitmq;

/**
 * @author Alex
 * @since 2019/6/13 14:22
 */
public class ConnectionFactory {
    private static com.rabbitmq.client.ConnectionFactory connectionFactory;
    static {
        connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");// ip
        connectionFactory.setPort(5672);// port
        connectionFactory.setVirtualHost("/dev");// 虚拟地址
        connectionFactory.setConnectionTimeout(10*1000);// 连接超时时间
    }

    public static com.rabbitmq.client.Connection getConnection()throws Exception{
        return connectionFactory.newConnection();
    }

}
