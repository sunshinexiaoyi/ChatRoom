package gos.wxy.service;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import gos.wxy.base.ChatMessage;
import gos.wxy.base.Respond;
import gos.wxy.base.SocketUser;
import gos.wxy.base.User;
import gos.wxy.define.CommandType;
import gos.wxy.define.PrintInfo;
import gos.wxy.exception.DataPackageException;
import gos.wxy.tool.DataPackage;
import gos.wxy.tool.JsonParse;
import gos.wxy.tool.UserManager;

/**
 * 服务器
 */
public class  Server {
    public static void main(String[] args){
       new ServerManager().run();
    }
}

class ServerManager{
    private final int port = 17728;
    private boolean runFlag = true;
    private ServerSocket serverSocket = null;

    public void run(){
        try{
            serverSocket = new ServerSocket(port,10, InetAddress.getLocalHost());
            printServerInfo();
            while(runFlag){
                Socket socket =  serverSocket.accept();
                System.out.println(PrintInfo.INFO_CLIENTED);
                new ServerThread(socket).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void printServerInfo(){
        System.out.println(PrintInfo.INFO_ADDRESS+serverSocket.getInetAddress().getHostAddress());
        System.out.println(PrintInfo.INFO_PORT+serverSocket.getLocalPort());
    }

    class ServerThread extends Thread{
        Socket socket;
        User user;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        private boolean runFlag = true; //运行标志

        UserManager userManager = UserManager.getInstance();

        ServerThread(Socket socket){
            this.socket = socket;
            try{
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            }catch (IOException e){
                e.printStackTrace();
                stopRun();
            }
        }

        /**
         * 停止线程运行
         */
        private void stopRun(){
            runFlag = false;
            System.out.println(PrintInfo.INFO_EXIT_THREAD + getId());
        }

        @Override
        public void run() {
            System.out.println(PrintInfo.INFO_START_THREAD + getId());

            try{
                while (runFlag){
                    recv(inputStream);
                }
            }finally {
                try{
                    socket.close();
                    if(null != user){
                        userManager.remove(socket);
                    }

                    inputStream.close();
                    outputStream.close();

                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }

        /**
         * 解析数据包
         * @param dataPackage
         */
        void parseDataPackage(DataPackage dataPackage){
            StringBuilder printInfo = new StringBuilder(PrintInfo.INFO_RECV_COMMAND);
            if(null != user){
                printInfo.append("["+user.getName()+"]");
            }
            printInfo.append(" ->"+dataPackage.getData());
            System.out.println(printInfo);

            switch (dataPackage.getCommand()){
                case CommandType.COM_CHECK_LOGIN:
                    User recvUser = JsonParse.user(dataPackage.getData());
                    boolean flag = false;

                    if((null != recvUser) && userManager.checkIsSuccess(recvUser)){
                        if(userManager.socketUserIsExists(recvUser)){
                            SocketUser existSocketUser  = userManager.findSocketUser(recvUser);
                            userManager.remove(existSocketUser);
                        }

                        ChatMessage msg = new ChatMessage(recvUser.getName()+PrintInfo.INFO_ONLINE);
                        User systemUser = new User(PrintInfo.SYSTEM_USER);
                        sendAllUser(systemUser,msg);

                        this.user = recvUser;
                        userManager.add(new SocketUser(socket,recvUser));
                        flag = true;

                    }
                    sendRespond(dataPackage.getCommand(),flag);
                    break;

                case CommandType.COM_CHAT_SEND:
                    ChatMessage msg = JsonParse.message(dataPackage.getData());
                    sendAllUser(this.user,msg);
                    break;
                case CommandType.COM_CHECK_LOGOUT:
                    System.out.println(user.getName()+PrintInfo.INFO_LOGOUT);
                    userManager.remove(socket);
                    sendRespond(dataPackage.getCommand(),true);
                    ChatMessage msgLogout = new ChatMessage(user.getName()+PrintInfo.INFO_OFFLINE);
                    User systemUser = new User(PrintInfo.SYSTEM_USER);
                    sendAllUser(systemUser,msgLogout);
                    break;
                default:
                    break;
            }
        }

        /**
         * 发送应答
         * @param command   应答哪条命令
         * @param flag  成功标志
         */
        void sendRespond(byte command,boolean flag){
            Respond respond = new Respond(command,flag);
            DataPackage sendPackage = new DataPackage(CommandType.COM_SYSTEM_RESPOND,JSON.toJSONString(respond));
            send(sendPackage.toByte());
        }

        /**
         * 群发
         */
        void sendAllUser(User user,ChatMessage msg){
            ArrayList<SocketUser> socketUsers = userManager.getSocketUsers();
            for (SocketUser  socketUser:
                    socketUsers) {
                try{
                    OutputStream out =  socketUser.getSocket().getOutputStream();
                    String name = user.getName();
                    if(user.equals(socketUser.getUser())){
                        continue;
                       // name = PrintInfo.INFO_ME;
                    }
                    ChatMessage sendMsg = new ChatMessage(name+":"+msg.getMessage());
                    DataPackage send = new DataPackage(CommandType.COM_CHAT_SEND,JSON.toJSONString(sendMsg));
                    System.out.println("给用户["+socketUser.getUser().getName()+"]发送转发信息："+sendMsg.getMessage());
                    out.write(send.toByte());
                }
                catch (IOException e){
                    e.printStackTrace();
                    userManager.remove(socketUser);     //失败移除
                }
            }
        }

        /**
         * 发送
         * @param data 发送的byte[]
         */
        void send(byte[] data){
            if(null != outputStream){
                try{
                    outputStream.write(data);
                }catch (IOException ex){
                    ex.printStackTrace();
                    stopRun();
                }
            }
        }

        /**
         * 接收
         * @param inputStream
         */
        private void recv(InputStream inputStream){
            try{
                byte[] head = new byte[DataPackage.headLen];
                int len = inputStream.read(head);   //接收头部段

                if(-1 == len){  //tcp在接收到长度为-1时，应该判断客户端已经断开
                    System.out.println(PrintInfo.ERROR_RECV_LEN);
                    stopRun();
                }

                if(len != head.length){
                    return;
                }

                try{
                    DataPackage dataPackage = new DataPackage(head);
                    byte[] data = new byte[dataPackage.getDataLen()];
                    len = inputStream.read(data);   //接收数据段
                    if(len != data.length){
                        return;
                    }

                    dataPackage.setData(data);
                    parseDataPackage(dataPackage);
                }catch (DataPackageException e){
                    System.out.println(PrintInfo.ERROR_PARSE_DATA_FAILED);
                    //e.printStackTrace();

                }
            }catch (SocketException e){
                stopRun();
                System.out.println(PrintInfo.INFO_SOCKET_CLOSE);
                //e.printStackTrace();
            }
            catch (IOException e){
                stopRun();
                e.printStackTrace();
            }
        }
    }
}


