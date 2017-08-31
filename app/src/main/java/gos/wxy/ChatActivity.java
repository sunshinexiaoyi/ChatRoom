package gos.wxy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
import static gos.wxy.enums.EnumChatType.OTHER;
import static gos.wxy.enums.EnumChatType.SELF;

public class ChatActivity extends AppCompatActivity implements OnClickListener{

    private TextView back;
    private ImageView imgAccount;
    private ImageView emoticon;
    private EditText inputText;
    private Button sendMessage;
    private ListView listView;

    private MsgAdapter msgAdapter;
    private List<ChatItem> msgList = new ArrayList<ChatItem>();

    /**
     * 事件接收
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recvEvent(EventMsg msg){
        if(msg.getEventMode() == EnumEventMode.IN){
            switch (msg.getCommand()){
                case COM_CHAT_SEND:     //聊天信息
                    updateChatView(JsonParse.message(msg.getData()));

                    break;
            }
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
        super.onDestroy();
        Event.unregister(this);
    }

    private void initView(){
        back = (TextView) findViewById(R.id.back);
        imgAccount = (ImageView) findViewById(R.id.imgAccount);
        emoticon = (ImageView) findViewById(R.id.emoticon);
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

                break;
            case R.id.sendMessage:
                String content = inputText.getText().toString();
                if(! "".equals(content)) {
                    Message message = new Message(content);
                    sendMessage(message);//发送聊天信息给服务器

                    ChatItem chatItem = new ChatItem(message.getMessage(),SELF);
                    msgList.add(chatItem);

                    // 当有新消息时，刷新ListView中的显示
                    msgAdapter.notifyDataSetChanged();
                    // 将ListView定位到最后一行
                    listView.setSelection(msgList.size());
                    // 清空输入框中的内容
                    inputText.setText("");
                }
                break;
        }
    }


    /**
     * 更新聊天界面
     * @param message
     */
    private void updateChatView(Message message){
        ChatItem chatItem = new ChatItem(message.getMessage(),OTHER);
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
        Event.send(new EventMsg(COM_CHAT_SEND, JSON.toJSONString(message),EnumEventMode.OUT));
    }
}
