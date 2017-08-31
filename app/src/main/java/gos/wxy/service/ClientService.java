package gos.wxy.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.wxy.LoginActivity;
import gos.wxy.R;
import gos.wxy.base.EventMsg;
import gos.wxy.base.Net;
import gos.wxy.define.PrintInfo;
import gos.wxy.enums.EnumEventMode;
import gos.wxy.tool.DataPackage;
import gos.wxy.tool.Event;
import gos.wxy.tool.JsonParse;

import static  gos.wxy.define.CommandType.*;
import static  gos.wxy.enums.EnumEventMode.*;
import static  gos.wxy.tool.BroadcastManager.*;

import static gos.wxy.define.CommandType.COM_CONNECT;
import static gos.wxy.define.CommandType.COM_CONNECT_ATTACH;
import static gos.wxy.define.CommandType.COM_CONNECT_DETACH;
import static gos.wxy.enums.EnumEventMode.IN;

public class ClientService extends Service {
    final String TAG = this.getClass().getSimpleName();
    final static String HANDLER_KEY = "handler key";
    TcpNet tcpNet = new TcpNet();

    boolean recvFlag = true;
    Handler taskHandler ;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventMsg msg = getBroadcastMsg(intent);
            Log.i(TAG,"command:"+msg.getCommand());

            if(null != taskHandler){
                Message message = new Message();

                Bundle bundle = new Bundle();
                bundle.putSerializable(HANDLER_KEY,msg);
                message.setData(bundle);

                taskHandler.sendMessage(message);
            }

        }
    };

    public ClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"创建服务");

        super.onCreate();

       // Event.register(this);
        initTaskHandler();
        registerReceiver(broadcastReceiver,FILTER_SERVICE);
        sendServiceStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        //Event.unregister(this);

        unregisterReceiver(broadcastReceiver);

    }

    //Service被启动时调用
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 使用前台服务，让Service稍微没那么容易被系统杀死,Builder-构造器
        Notification.Builder builder = new Notification.Builder(this);
        Intent intent1 = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setTicker("启动后台运行");
        builder.setContentTitle("Chatting");
        builder.setContentText("正在运行...");
        builder.setWhen(System.currentTimeMillis());
        // 使用startForeground 将service放到前台状态,这样在低内存时被kill的几率会低一些
        startForeground(1, builder.getNotification());

        return super.onStartCommand(intent1, flags, startId);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void recvEvent(EventMsg msg){
        if(msg.getEventMode() == EnumEventMode.OUT)
        {
            parseEventMsg(msg);
        }
    }

    private void parseEventMsg(EventMsg msg){
        Log.i(TAG,"getCommand:"+msg.getCommand());

        switch (msg.getCommand()){
            case COM_CONNECT:
                connect(JsonParse.net(msg.getData()));
                break;
            case COM_SYSTEM_SERVICE_STOP:
                stopService();
                break;
            default:
                send(msg);
                break;
        }
    }

    private void initTaskHandler(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                taskHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        EventMsg eventMsg = (EventMsg)msg.getData().getSerializable(HANDLER_KEY);
                        parseEventMsg(eventMsg);
                    }
                };
                Looper.loop();
            }
        }).start();
    }

    /**
     * 接收服务器
     */
    private void recv(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (recvFlag){
                        try {
                            DataPackage pak = tcpNet.recv();
                            Log.i(TAG,pak.getData());
                            eventSend(new EventMsg(pak.getCommand(),pak.getData(),IN));

                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }

                }finally {

                    tcpNet.close();
                }

            }
        }).start();
    }

    /**
     * 停止线程运行
     */
    private void stopRun(){
        recvFlag = false;
        System.out.println(PrintInfo.INFO_EXIT_THREAD );

    }

    private void connect(Net net){
        if(tcpNet.connect(net)){
            recv();

            Log.i(TAG,"connect success");
            eventSend(new EventMsg(COM_CONNECT_ATTACH,IN));
        }else {
            Log.i(TAG,"connect failed");

            eventSend(new EventMsg(COM_CONNECT_DETACH,IN));
        }
    }

    /**
     * 发送给服务器
     * @param msg
     */
    private void send(EventMsg msg){
        DataPackage dataPackage = new DataPackage(msg.getCommand(),msg.getData());

        try {
            tcpNet.send(dataPackage.toByte());
        } catch (Exception e) {
            e.printStackTrace();
            eventSend(new EventMsg(COM_CONNECT_DETACH,IN));
        }
    }

    private void eventSend(EventMsg msg){
        //Event.send(msg);
        sendBroadcast(getIntentActivity(msg));
    }

    private void sendServiceStart(){
        Log.i(TAG,"sendServiceStart");

        eventSend(new EventMsg(COM_SYSTEM_SERVICE_START,IN));
    }

    private  void stopService(){
        stopRun();
        stopSelf();
        Log.i(TAG,"停止服务");
    }


}
