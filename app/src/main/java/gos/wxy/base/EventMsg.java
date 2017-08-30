package gos.wxy.base;

import java.io.Serializable;

import gos.wxy.enums.EnumEventMode;

/**
 * Created by wuxy on 2017/8/24.
 */

public class EventMsg implements Serializable{
    private byte command;
    private String data;
    private EnumEventMode eventMode;

    public EventMsg(byte command, EnumEventMode eventMode) {
        this.command = command;
        this.eventMode = eventMode;
    }

    public EventMsg(byte command, String data, EnumEventMode eventMode) {
        this.command = command;
        this.data = data;
        this.eventMode = eventMode;
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public EnumEventMode getEventMode() {
        return eventMode;
    }

    public void setEventMode(EnumEventMode eventMode) {
        this.eventMode = eventMode;
    }
}
