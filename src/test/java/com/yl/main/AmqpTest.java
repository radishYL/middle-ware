package com.yl.main;

import com.yl.main.Application;
import com.yl.middleware.rabbitmq.combat.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Alex
 * @since 2019/6/17 10:58
 */

/**
 * spring单元测试注意问题
 *  1.若测试类与Spring启动类包名相同,则不需要指定classes = {Application.class},否则需要显示指定启动类或配置类的地址
 *  2.classes属性可以为配置类或启动类
 */
@SpringBootTest(classes = {RabbitmqConfig.class})
//@SpringBootTest
@RunWith(SpringRunner.class)
public class AmqpTest {


    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void demo_1() throws Exception{

        // 定义交换机 durable是否持久化(Broker重启恢复) autoDelete当所有绑定队列都不再使用时,是否删除交换机
        rabbitAdmin.declareExchange(new DirectExchange("amqp.spring.direct.exchange", false, true, null));
        // 定义队列
        rabbitAdmin.declareQueue(new Queue("amqp.spring.queue", false, false, true,null));
        // 定义绑定
        rabbitAdmin.declareBinding(new Binding("amqp.spring.queue", Binding.DestinationType.QUEUE,
            "amqp.spring.direct.exchange", "directKey", null));

        // 链式编程方式 版本不同导致失败
//        rabbitAdmin.declareBinding(BindingBuilder
//            .bind(new Queue("amqp.spring.builder.queue", false, false, true))
//            .to(new TopicExchange("amqp.spring.topic.exchange", false, true))
//            .with("spring.#"));

        Thread.currentThread().sleep(15*1000);
    }

}
