package gos.wxy.base;

/**
 * 回应类
 * Created by wuxy on 2017/7/31.
 */

public class Respond {
    private byte command;
    private boolean flag ;
    private int reserve;

    public Respond(){}

    public Respond(byte command,boolean flag){
        this.command = command;
        this.flag = flag;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public byte getCommand() {
        return command;
    }


    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag(){
        return flag;
    }

    public int getReserve() {
        return reserve;
    }

    public void setReserve(int reserve) {
        this.reserve = reserve;
    }
}
