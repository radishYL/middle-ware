package com.yl.middleware.rabbitmq.combat.component.converter;

/**
 * @author Alex
 * @since 2019/6/17 16:52
 */
public class MsgInfo {

    private String queue;

    private String body;

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
