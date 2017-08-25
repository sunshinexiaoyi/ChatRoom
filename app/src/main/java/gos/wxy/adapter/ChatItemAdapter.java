package gos.wxy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gos.wxy.base.ChatItem;
import gos.wxy.R;

/**
 * Created by wuxy on 2017/8/17.
 */

public class ChatItemAdapter extends BaseAdapter{
    private ArrayList<ChatItem> chatItems = new ArrayList<>();
    Context context;

    View convertViewSelf;

    View convertViewOther;


    public ChatItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return chatItems.size();
    }

    @Override
    public Object getItem(int position) {
        return chatItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holderSelf = null;
        ViewHolder holderOther = null;

        if(null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_self,parent,false);
            holderSelf = new ViewHolder();
            holderSelf.chatMsg = (TextView) convertView.findViewById(R.id.chatMsg);
            convertView.setTag(holderSelf);
        }else {
            holderSelf = (ViewHolder)convertView.getTag();
        }


        if(null == convertViewOther){
            convertViewOther = LayoutInflater.from(context).inflate(R.layout.item_chat_other,parent,false);
            holderOther = new ViewHolder();
            holderOther.chatMsg = (TextView) convertViewOther.findViewById(R.id.chatMsg);
            convertViewOther.setTag(holderOther);

        }else {
            holderOther = (ViewHolder)convertViewOther.getTag();
        }

        ChatItem chatItem = chatItems.get(position);
        Log.i("adapter","getMessage:"+chatItem.getMessage());

        switch (chatItem.getChatType()){
            case SELF:
                holderSelf.chatMsg.setText(chatItem.getMessage());
                return convertView;

            case OTHER:
                holderOther.chatMsg.setText(chatItem.getMessage());
                return convertViewOther;
            default:
                break;
        }

        return null;
    }

    public void addChatItem(ChatItem chatItem){
        chatItems.add(chatItem);
        Log.i("adapter","size:"+chatItems.size());
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView chatMsg;
    }


}
