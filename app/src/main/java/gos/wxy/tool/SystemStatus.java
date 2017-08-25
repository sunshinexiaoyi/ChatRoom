package gos.wxy.tool;

/**
 * 系统状态
 * Created by wuxy on 2017/8/4.
 */

public class SystemStatus {
    private boolean run = true;         //运行
    private boolean connect = false;    //连接
    private boolean login = false;      //登陆

    public void setRun(boolean run) {
        this.run = run;
    }
    public boolean getRun(){
        return run;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }
    public boolean getConnect(){
        return connect;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
    public boolean getLogin(){
        return login;
    }

    /**
     * 重置状态
     */
    public void reset(){
        run = true;         //运行
        connect = false;    //连接
        login = false;      //登陆
    }


}
