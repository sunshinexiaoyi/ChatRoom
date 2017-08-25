package gos.wxy.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import gos.wxy.tool.JsonParse;
import gos.wxy.base.LoginSetting;
import gos.wxy.base.User;

/**
 * 共享数据库
 * Created by wuxy on 2017/8/11.
 */

public class SharedDb {
    private final String USER_DB = "user_db";   //用户数据库文件
    private final String LOGIN_SETTING_DB = "login_setting_db"; //登陆配置数据库文件

    private final String USER_KEY = "user";
    private final String LOGIN_SETTING_KEY = "login_setting";


    private Context context;

    public SharedDb(Context context){
        this.context = context;
    }

    public User getUser(){
        SharedPreferences sp = context.getSharedPreferences(USER_DB,Context.MODE_PRIVATE);

        String jsonStr = sp.getString(USER_KEY,null);

        if(null != jsonStr){
            return JsonParse.user(jsonStr);
        }

        return null;
    }

    public void setUser(User user){
        SharedPreferences sp = context.getSharedPreferences(USER_DB,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String jsonStr = JSON.toJSONString(user);

        editor.putString(USER_KEY,jsonStr);
        editor.commit();
    }

    public LoginSetting getLoginSetting(){
        SharedPreferences sp = context.getSharedPreferences(LOGIN_SETTING_DB,Context.MODE_PRIVATE);

        String jsonStr = sp.getString(LOGIN_SETTING_KEY,null);

        if(null != jsonStr){
            return JsonParse.loginSetting(jsonStr);
        }

        return null;
    }

    public void setLoginSetting(LoginSetting loginSetting){
        SharedPreferences sp = context.getSharedPreferences(LOGIN_SETTING_DB,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String jsonStr = JSON.toJSONString(loginSetting);

        editor.putString(LOGIN_SETTING_KEY,jsonStr);
        editor.commit();
    }



}
