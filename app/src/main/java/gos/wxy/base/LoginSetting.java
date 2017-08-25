package gos.wxy.base;

/**
 * Created by wuxy on 2017/8/11.
 */

public class LoginSetting {
    private boolean remember;
    private boolean auto;
    private boolean hiding;

    public LoginSetting(){}

    public void setAuto(boolean auto) {
        this.auto = auto;
    }
    public boolean getAuto(){
        return auto;
    }


    public void setHiding(boolean hiding) {
        this.hiding = hiding;
    }
    public boolean getHiding(){
        return hiding;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }
    public boolean getRemember(){
        return remember;
    }


}
