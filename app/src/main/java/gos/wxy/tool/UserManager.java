package gos.wxy.tool;


import java.net.Socket;
import java.util.ArrayList;

import gos.wxy.base.SocketUser;
import gos.wxy.base.User;
import gos.wxy.define.PrintInfo;

/**
 * Created by wuxy on 2017/8/1.
 */

public
class UserManager{
    private ArrayList<User> users = new ArrayList<>();  //所有用户
    private ArrayList<SocketUser> socketUsers = new ArrayList<>();  //登陆的用户组

    private static UserManager userManager = new UserManager();

    private UserManager(){
        users.add(new User("小明"));
        users.add(new User("小花"));
        users.add(new User("小红"));
        users.add(new User("小白"));
        users.add(new User("q","q"));
    }
    public static UserManager getInstance(){
        return userManager;
    }

    public ArrayList<SocketUser> getSocketUsers(){
        return socketUsers;
    }

    public synchronized void add(SocketUser socketUser){
        System.out.println(PrintInfo.SUCCESS_ADD_USER);
        socketUsers.add(socketUser);
    }

    public void remove(Socket socket){
        SocketUser socketUser = findSocketUser(socket);
        if(null != socketUser){
            remove(socketUser);
        }
    }

    public void remove(SocketUser socketUser){
        socketUsers.remove(socketUser);
        System.out.println(socketUser.getUser().getName()+PrintInfo.INFO_REMOVE);
    }

    public SocketUser findSocketUser(User user){
        for (SocketUser s :
                socketUsers) {
            if (user.equals(s.getUser())) {
                return s;
            }
        }

        return null;
    }

    public SocketUser findSocketUser(Socket socket){
        for (SocketUser s :
                socketUsers) {
            if (socket.equals(s.getSocket())) {
                return s;
            }
        }

        return null;
    }


    /**
     * 判断用户是否存在
     * @param user
     * @return
     */
    public boolean socketUserIsExists(User user){

        return (null != findSocketUser(user));
    }

    public boolean checkIsSuccess(User user){
        for (User u :
                users) {
            if (u.equals(user)){
                return true;
            }
        }
        return false;
    }


}