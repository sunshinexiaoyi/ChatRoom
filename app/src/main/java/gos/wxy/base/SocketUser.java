package gos.wxy.base;

import java.net.Socket;

/**
 * 登陆用户类
 * Created by wuxy on 2017/7/31.
 */

public class SocketUser {
    private Socket socket;
    private User user;

    public SocketUser(Socket socket,User user){
        this.socket = socket;
        this.user = user;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

