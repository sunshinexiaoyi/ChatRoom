package com.wxy.base;

/**
 * 聊天信息类
 * Created by wuxy on 2017/7/31.
 */

public class Message {
    private String message;

    public Message(){}
    public Message(String message){
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
