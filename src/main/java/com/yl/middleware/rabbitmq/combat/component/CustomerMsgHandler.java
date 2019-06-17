package com.yl.middleware.rabbitmq.combat.component;

import com.yl.middleware.rabbitmq.combat.component.converter.MsgInfo;

/**
 * @author Alex
 * @since 2019/6/17 16:13
 */
public class CustomerMsgHandler {

    public void handMsg4Byte(byte[] msgBody){
        System.err.println("字节数组接收消息:" + new String(msgBody));
    }

    public void handMsg4Str(String msgbody){
        System.err.println("字符串接收消息:" + msgbody);
    }

    public void handMsg4Args(MsgInfo msgInfo){
        System.err.println("字符串接收消息:" + msgInfo.getBody() + " from queue:" + msgInfo.getQueue());
    }

    public void deadLetterHand(MsgInfo msgInfo){
        System.err.println("deadLetterHand 字符串接收消息:" + msgInfo.getBody() + " from queue:" + msgInfo.getQueue());
    }

    public void userHand(MsgInfo msgInfo){
        System.err.println("userHand 字符串接收消息:" + msgInfo.getBody() + " from queue:" + msgInfo.getQueue());
    }
}
