package com.yl.middleware.rabbitmq.combat.config;

import com.yl.middleware.rabbitmq.combat.component.converter.CustomerMessageConvert;
import com.yl.middleware.rabbitmq.combat.component.CustomerMsgHandler;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Alex
 * @since 2019/6/17 10:44
 */
@Configuration
@ComponentScan(value = {"com.yl.middleware.rabbitmq"})
@EnableRabbit
public class RabbitmqConfig {


    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("127.0.0.1:5672");
        //connectionFactory.setUsername("guest");
        //connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/dev");
        connectionFactory.setConnectionTimeout(10*1000);
        // 开启签收监听
        connectionFactory.setPublisherConfirms(true);
        // 开启可达监听
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory(){
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory());
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        containerFactory.setReceiveTimeout(10*1000L);
        containerFactory.setConcurrentConsumers(1);
        containerFactory.setMaxConcurrentConsumers(1);
        containerFactory.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + "yu.alex";
            }
        });
        return containerFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);// 自动启动
        return rabbitAdmin;
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 如果要进行消息可达(return)监听,必须设置为true,不然Broker会直接删除不可达消息
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    /**
     * 定义exchange、queue、bind
     */

    /**
     * 定义死信队列
     * @return
     */
    @Bean
    public FanoutExchange deadLetterExchange(){
        return new FanoutExchange("dead_letter_exchange", true, false);
    }
    @Bean
    public Queue deadLetterQueue(){
        return new Queue("dead_letter_queue", true, false, false);
    }
    @Bean
    public Binding deadLetterBind(){
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

    @Bean
    public Exchange exchange_user(){
        return new TopicExchange("user_exchange", true, false, null);
    }
    @Bean
    public Queue user_queue(){
        Map<String,Object> arguments = new HashMap<>();
        // 设置死信队列
        arguments.put("x-dead-letter-exchange", deadLetterExchange().getName());
        //arguments.put("x-dead-letter-routing-key", "deadKey");
        return new Queue("user_buy_queue", true, false, false, arguments);
    }
    @Bean
    public Binding user__bind(){
        return new Binding(user_queue().getName(), Binding.DestinationType.QUEUE, exchange_user().getName(), "user.#",null );
    }

    //@Bean
    public SimpleMessageListenerContainer messageContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(deadLetterQueue(),user_queue());// 设置监控队列
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(2);
        container.setDefaultRequeueRejected(false);// 是否重回队列
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);// 自动签收消息
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });
        // 设置消息监听器
//        container.setMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                String body = new String(message.getBody());
//                System.err.println("spring amqp从队列:" + message.getMessageProperties().getConsumerQueue() + "收到消息:" + body);
//                Thread.currentThread().sleep(3*1000);
//            }
//        });

        /**
         * 采用适配器模式监听消息<一>:所有queue统一处理
         *  注意事项:
         *      1.delegate中的方法可以自己重新定义(默认方法名handleMessage),如果不适用默认方法名,需显示指定
         *      2.方法的参数是byte[],如果更改需要自定义converter转换
         */
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new CustomerMsgHandler());
//        adapter.setDefaultListenerMethod("handMsg4Args");// 默认为handleMessage,通过反射调用
//        adapter.setMessageConverter(new CustomerMessageConvert());
//        container.setMessageListener(adapter);

        /**
         * 采用适配器模式监听消息<二>:不同队列使用不同方法处理
         */
        MessageListenerAdapter adapter = new MessageListenerAdapter(new CustomerMsgHandler());
        Map<String,String> queueOrTagToMethodName = new HashMap<>();
        queueOrTagToMethodName.put(deadLetterQueue().getName(), "deadLetterHand");
        queueOrTagToMethodName.put(user_queue().getName(), "userHand");
        adapter.setMessageConverter(new CustomerMessageConvert());
        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
        container.setMessageListener(adapter);

        /**
         * 关于消息转换器种类很多,后续用到再去查
         */

        return container;
    }

}
