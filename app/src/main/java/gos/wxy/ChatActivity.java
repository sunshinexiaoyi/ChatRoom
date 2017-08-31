package gos.wxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import gos.wxy.adapter.MsgAdapter;
import gos.wxy.base.ChatItem;
import gos.wxy.base.EventMsg;
import gos.wxy.base.Message;
import gos.wxy.enums.EnumEventMode;
import gos.wxy.tool.Event;
import gos.wxy.tool.JsonParse;

import static gos.wxy.define.CommandType.COM_CHAT_SEND;
import static gos.wxy.define.CommandType.COM_CHECK_LOGOUT;
import static gos.wxy.enums.EnumChatType.OTHER;
import static gos.wxy.enums.EnumChatType.SELF;
import static gos.wxy.tool.BroadcastManager.FILTER_ACTIVITY;
import static gos.wxy.tool.BroadcastManager.getBroadcastMsg;
import static gos.wxy.tool.BroadcastManager.getIntentService;


public class ChatActivity extends AppCompatActivity implements OnClickListener{
    final String TAG = this.getClass().getSimpleName();

    private TextView back;
    private ImageView imgAccount;
    private ImageView emoticon;
    private ImageView emoticonImag;
    private EditText inputText;
    private Button sendMessage;
    private ListView listView;

    private MsgAdapter msgAdapter;
    private List<ChatItem> msgList = new ArrayList<ChatItem>();
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventMsg msg = getBroadcastMsg(intent);
            parseEventMsg(msg);
        }
    };

    /**
     * 事件接收
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recvEvent(EventMsg msg){
        if(msg.getEventMode() == EnumEventMode.IN){
            parseEventMsg(msg);
        }
    }

    private void parseEventMsg(EventMsg msg){
        switch (msg.getCommand()){
            case COM_CHAT_SEND:     //聊天信息
                Message message = JsonParse.message(msg.getData());
                updateChatView(new ChatItem(message.getMessage(),OTHER));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 沉浸式状态栏
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //requestWindowFeature(Window.FEATURE_NO_TITLE); // 去除标题栏，必须放在R前
        setContentView(R.layout.activity_chat);

        Event.register(this);
        //Event.register(this);
        registerReceiver(broadcastReceiver,FILTER_ACTIVITY);

        initMsg(); // 初始写几条数据，用于测试
        initView();
    }

    private void initMsg() {
        ChatItem msg1 = new ChatItem("Hello guy", OTHER);
        msgList.add(msg1);
        ChatItem msg2 = new ChatItem("Hello,Who is that?", SELF);
        msgList.add(msg2);
        ChatItem msg3 = new ChatItem("this is Sum.", OTHER);
        msgList.add(msg3);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
        sendLogout();
        //Event.unregister(this);
        unregisterReceiver(broadcastReceiver);
    }

    private void initView(){
        back = (TextView) findViewById(R.id.back);
        imgAccount = (ImageView) findViewById(R.id.imgAccount);
        emoticon = (ImageView) findViewById(R.id.emoticon);
        emoticonImag = (ImageView) findViewById(R.id.emoticonImag);
        inputText = (EditText) findViewById(R.id.inputText);
        sendMessage = (Button) findViewById(R.id.sendMessage);
        listView = (ListView) findViewById(R.id.chatDialog);

        back.setOnClickListener(this);
        imgAccount.setOnClickListener(this);
        // EditText获得焦点，同时弹出小键盘
        inputText.requestFocus();
        emoticon.setOnClickListener(this);
        sendMessage.setOnClickListener(this);

        msgAdapter = new MsgAdapter(this, R.layout.chatting_item, msgList);
        listView.setAdapter(msgAdapter);

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputText.getText().toString().length() > 0) {
                    sendMessage.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.imgAccount:
                Intent intent = getIntent();
                String name = intent.getStringExtra("NAME");
                Toast.makeText(this, "我是" + name, Toast.LENGTH_SHORT).show();
                break;
            case R.id.emoticon:
                Toast.makeText(this, "小情绪", Toast.LENGTH_SHORT).show();

                AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                animation.setDuration(5000);
                animation.setRepeatMode(AlphaAnimation.REVERSE);
                animation.setRepeatCount(AlphaAnimation.INFINITE);
                emoticonImag.setAnimation(animation);
                break;
            case R.id.sendMessage:
                String content = inputText.getText().toString();
                if(! "".equals(content)) {
                    Message message = new Message(content);
                    sendMessage(message);//发送聊天信息给服务器

                    // 清空输入框中的内容
                    inputText.setText("");
                }
                break;
        }
    }


    /**
     * 更新聊天界面
     * @param chatItem
     */
    private void updateChatView(ChatItem chatItem){
        //ChatItem chatItem = new ChatItem(message.getMessage(),OTHER);
        msgList.add(chatItem);

        // 当有新消息时，刷新ListView中的显示
        msgAdapter.notifyDataSetChanged();
        // 将ListView定位到最后一行
        listView.setSelection(msgList.size());
    }

    /**
     * 发送聊天信息
     * @param message
     */
    private void sendMessage(Message message){
        updateChatView(new ChatItem(message.getMessage(),SELF));
        Log.i(TAG,"sendMessage:"+message.getMessage());

        eventSend(new EventMsg(COM_CHAT_SEND, JSON.toJSONString(message),EnumEventMode.OUT));
    }

    private void eventSend(EventMsg msg){
        //Event.send(msg);
        sendBroadcast(getIntentService(msg));
    }


    private void sendLogout(){
        Log.i(TAG,"COM_CHECK_LOGOUT");
        eventSend(new EventMsg(COM_CHECK_LOGOUT,EnumEventMode.OUT));

    }
}
