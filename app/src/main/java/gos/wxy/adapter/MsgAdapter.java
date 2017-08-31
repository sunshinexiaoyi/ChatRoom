package gos.wxy.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import gos.wxy.R;
import gos.wxy.base.ChatItem;

/**
 * Created by lp on 2017/8/28.
 * 创建ListView的适配器类，让它继承自ArrayAdapter，
 * 并将泛型指定为Msg类。新建类MsgAdapter
 */

public class MsgAdapter extends ArrayAdapter<ChatItem> {

    private int resourceId;
    private List<ChatItem> chatMsg;

    private class ViewHolder {
        LinearLayout leftLayout;
        RelativeLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
    }

    public MsgAdapter(Context context, int resourceId, List<ChatItem> chatMsg) {
        super(context, resourceId, chatMsg);
        this.resourceId = resourceId;
        this.chatMsg = chatMsg;
    }

    public int getCount() {
        if(chatMsg == null){
            return 0;
        } else {
            return chatMsg.size();
        }
    }

    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        ChatItem msg = getItem(position);

        ViewHolder viewHolder = new ViewHolder();
        if(view == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null); // R.layout.chatting_item
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (RelativeLayout) view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            view.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) view.getTag();
        }

        switch (msg.getChatType()) {
            case OTHER:
                // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftMsg.setText(msg.getMessage());
                break;
            case SELF:
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setText(msg.getMessage());
        }
        return view;
    }

}
