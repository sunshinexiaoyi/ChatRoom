package gos.wxy.base;

import gos.wxy.enums.EnumChatType;

/**
 * Created by wuxy on 2017/8/17.
 */

public class ChatItem extends Message{
    EnumChatType chatType;

    public ChatItem() {
    }

    public ChatItem(EnumChatType chatType) {
        this.chatType = chatType;
    }

    public ChatItem(String message, EnumChatType chatType) {
        super(message);
        this.chatType = chatType;
    }

    public EnumChatType getChatType() {
        return chatType;
    }

    public void setChatType(EnumChatType chatType) {
        this.chatType = chatType;
    }
}
