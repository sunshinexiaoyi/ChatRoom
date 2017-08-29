package gos.wxy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.wxy.base.EventMsg;
import gos.wxy.base.Net;
import gos.wxy.enums.EnumEventMode;
import gos.wxy.service.ClientService;
import gos.wxy.tool.Event;

import static gos.wxy.define.CommandType.*;


public class ConnectActivity extends AppCompatActivity {
    final String TAG = this.getClass().getSimpleName();

    Net net = new Net("192.168.100.101",17728); //默认服务器地址

    /**
     * 事件接收
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recvEvent(EventMsg msg){
        if(msg.getEventMode() == EnumEventMode.IN){
            switch (msg.getCommand()){
                case COM_CONNECT_ATTACH:
                    startLoginActivity();
                    break;
                case COM_CONNECT_DETACH:
                    Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Event.register(this);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Event.unregister(this);
    }

    void init(){
        startNetService();
        sendConnect();
    }

    void startNetService(){
        Log.i(TAG,"startNetService");
        Intent intent = new Intent(this, ClientService.class);
        startService(intent);
    }

    void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    void sendConnect(){
        Log.i(TAG,"sendConnect");
        Event.sendSticky(new EventMsg(COM_CONNECT, JSON.toJSONString(net),EnumEventMode.OUT));
    }



}
