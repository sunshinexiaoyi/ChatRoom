package com.wxy.tool;

import com.alibaba.fastjson.JSON;
import com.wxy.base.Message;
import com.wxy.base.Respond;
import com.wxy.base.User;

/**
 * Json 解析类
 * Created by wuxy on 2017/7/31.
 */

public class JsonParse {
    public static User user(String jsonData){
        System.out.println("jsonData:"+jsonData);
        return JSON.parseObject(jsonData,User.class);
    }

    public static Respond respond(String jsonData){
        return JSON.parseObject(jsonData,Respond.class);
    }

    public static Message message(String jsonData){
        return JSON.parseObject(jsonData,Message.class);
    }
}
