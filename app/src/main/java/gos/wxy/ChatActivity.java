package gos.wxy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.wxy.adapter.ChatItemAdapter;
import gos.wxy.base.ChatItem;
import gos.wxy.base.EventMsg;
import gos.wxy.base.Message;

import gos.wxy.enums.EnumChatType;
import gos.wxy.enums.EnumEventMode;
import gos.wxy.tool.Event;
import gos.wxy.tool.JsonParse;

import static gos.wxy.define.CommandType.COM_CHAT_SEND;
import static gos.wxy.define.CommandType.COM_SYSTEM_RESPOND;

public class ChatActivity extends AppCompatActivity {
    private final  String TAG = this.getClass().getSimpleName();

    Button chatSend;
    EditText chatInput ;
    ListView chatListView;
    ChatItemAdapter chatItemAdapter = new ChatItemAdapter(this);


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
        setContentView(R.layout.activity_chat);
        Event.register(this);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Event.unregister(this);
    }

    private void initView(){
        chatInput = (EditText)findViewById(R.id.chatInput);
        chatSend = (Button)findViewById(R.id.chatSend);
        chatListView = (ListView)findViewById(R.id.chatListView);
        chatListView.setAdapter(chatItemAdapter);

        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = chatInput.getText().toString();
                chatInput.setText("");

                Message message = new Message(sendMsg);
                sendMessage(message);

                //chatItemAdapter.addChatItem(new ChatItem(sendMsg, EnumChatType.SELF));
            }
        });


        chatInput.addTextChangedListener(new TextWatcher() {//当输入长度大于0时，按钮使能
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int inputLen = s.length();

                if(inputLen >0){
                    chatSend.setEnabled(true);
                }else {
                    chatSend.setEnabled(false);

                }
            }
        });
    }


    /**
     * 更新聊天界面
     * @param message
     */
    private void updateChatView(Message message){
        chatItemAdapter.addChatItem(new ChatItem(message.getMessage(), EnumChatType.SELF));

    }

    /**
     * 发送聊天信息
     * @param message
     */
    private void sendMessage(Message message){
        Event.send(new EventMsg(COM_CHAT_SEND, JSON.toJSONString(message),EnumEventMode.OUT));
    }
}
