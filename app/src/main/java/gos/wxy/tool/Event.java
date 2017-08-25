package gos.wxy.tool;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import gos.wxy.base.EventMsg;

/**
 * Created by wuxy on 2017/8/24.
 */

public class Event {
    static public void register(Context context){
        EventBus.getDefault().register(context);
    }

    static public void unregister(Context context){
        EventBus.getDefault().unregister(context);
    }

    static public void send(EventMsg msg){
        EventBus.getDefault().post(msg);
    }

    static public void sendSticky(EventMsg msg){
        EventBus.getDefault().postSticky(msg);
    }

}
