package com.wxy.define;

/**
 * 命令类
 * Created by wuxy on 2017/7/31.
 */

public interface CommandType {

     /* 系统命令*/
    byte COM_SYSTEM_RESPOND = 1;    //回应   

     /* 验证模块命令 */
    byte COM_CHECK_LOGIN = 10;  //登录 
    byte COM_CHECK_LOGOUT = 11; //登出

    /*聊天模块命令*/
    byte COM_CHAT_SEND = 20;    //发送聊天信息

    /*连接模块命令*/
    byte COM_CONNECT_GET = 30;//获取
    byte COM_CONNECT_SET = 31;//设置
    byte COM_CONNECT = 32;    //连接
    byte COM_CONNECT_ATTACH = 33;    //连接
    byte COM_CONNECT_DETACH = 34;    //分离
}
