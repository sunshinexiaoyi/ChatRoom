package gos.wxy.tool;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import gos.wxy.base.EventMsg;

/**
 * Created by wuxy on 2017/8/30.
 */

public class BroadcastManager {
    private final static String  ACTION_ACTIVITY = "android.define.action.activity";
    private final static String  ACTION_SERVICE = "android.define.action.service";
    private final static String  BROADCAST_KEY = "key";

    public final static IntentFilter FILTER_ACTIVITY = new IntentFilter(ACTION_ACTIVITY);
    public final static IntentFilter FILTER_SERVICE = new IntentFilter(ACTION_SERVICE);

    public static Intent getIntentActivity(EventMsg msg){
        Intent intent = new Intent();
        intent.setAction(ACTION_ACTIVITY);

        Bundle bundle = new Bundle();
        bundle.putSerializable(BROADCAST_KEY,msg);

        intent.putExtras(bundle);

        return intent;
    }


    public static Intent getIntentService(EventMsg msg){
        Intent intent = new Intent();
        intent.setAction(ACTION_SERVICE);

        Bundle bundle = new Bundle();
        bundle.putSerializable(BROADCAST_KEY,msg);

        intent.putExtras(bundle);

        return intent;
    }

    public static EventMsg getBroadcastMsg(Intent intent){
        return (EventMsg) intent.getSerializableExtra(BROADCAST_KEY);
    }
}
