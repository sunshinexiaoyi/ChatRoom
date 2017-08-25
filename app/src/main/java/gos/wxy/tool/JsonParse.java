package gos.wxy.tool;

import com.alibaba.fastjson.JSON;

import gos.wxy.base.LoginSetting;
import gos.wxy.base.Message;
import gos.wxy.base.Net;
import gos.wxy.base.Respond;
import gos.wxy.base.User;

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

    public static LoginSetting loginSetting(String jsonData){
        return JSON.parseObject(jsonData,LoginSetting.class);

    } public static Net net(String jsonData){
        return JSON.parseObject(jsonData,Net.class);
    }
}
