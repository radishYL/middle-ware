package com.yl.main;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Alex
 * @since 2019/6/13 13:53
 */
@org.springframework.boot.autoconfigure.SpringBootApplication
@ComponentScan(value = {"com.yl.middleware.rabbitmq"})
public class Application {


    public static void main(String[] args) {

        SpringApplication.run(Application.class,args);

    }

}
