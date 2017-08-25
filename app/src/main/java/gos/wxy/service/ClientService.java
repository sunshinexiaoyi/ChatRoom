package gos.wxy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import gos.wxy.base.EventMsg;
import gos.wxy.base.Net;
import gos.wxy.enums.EnumEventMode;
import gos.wxy.tool.DataPackage;
import gos.wxy.tool.Event;
import gos.wxy.tool.JsonParse;

import static  gos.wxy.define.CommandType.*;
import static  gos.wxy.enums.EnumEventMode.*;

public class ClientService extends Service {
    final String TAG = this.getClass().getSimpleName();
    TcpNet tcpNet = new TcpNet();

    boolean recvFlag = true;

    public ClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onbind");

        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"onCreate");
        super.onCreate();
        Event.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Event.unregister(this);

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.BACKGROUND)
    public void recvEvent(EventMsg msg){
        Log.i(TAG,"command:"+msg.getCommand());
        if(msg.getEventMode() == EnumEventMode.OUT)
        {
            switch (msg.getCommand()){
                case COM_CONNECT:
                    connect(JsonParse.net(msg.getData()));
                    break;
                default:
                    send(msg);
                    break;
            }
        }
    }

    private void connect(Net net){
        if(tcpNet.connect(net)){
            recv();

            Log.i(TAG,"connect success");
            Event.send(new EventMsg(COM_CONNECT_ATTACH,IN));
        }else {
            Log.i(TAG,"connect failed");

            Event.send(new EventMsg(COM_CONNECT_DETACH,IN));
        }
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
                            Event.send(new EventMsg(pak.getCommand(),pak.getData(),IN));

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
     * 发送给服务器
     * @param msg
     */
    private void send(EventMsg msg){
        DataPackage dataPackage = new DataPackage(msg.getCommand(),msg.getData());

        try {
            tcpNet.send(dataPackage.toByte());
        } catch (Exception e) {
            e.printStackTrace();
            Event.send(new EventMsg(COM_CONNECT_DETACH,IN));
        }
    }
}
