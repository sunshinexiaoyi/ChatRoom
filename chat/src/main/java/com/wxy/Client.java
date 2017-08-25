package com.wxy;

import com.alibaba.fastjson.JSON;
import com.wxy.tool.Check;
import com.wxy.define.CommandType;
import com.wxy.tool.DataPackage;
import com.wxy.tool.JsonParse;
import com.wxy.tool.Keyboard;
import com.wxy.base.Message;
import com.wxy.define.PrintInfo;
import com.wxy.base.Respond;
import com.wxy.tool.SystemStatus;
import com.wxy.base.User;
import com.wxy.exception.DataPackageException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 客户端
 * Created by wuxy on 2017/7/28.
 */

public class Client {
    public static void main(String[] args){
        new ClientManager().run();
    }

}

class ClientManager{
    private Socket socket = null;
    private OutputStream outputStream = null;
    private ClientThread clientThread = null;

    private final String cmdStr = "cmd:";   //命令控制
    private SystemStatus systemStatus = new SystemStatus();

    public void run(){
        while (systemStatus.getRun()){
            if(!systemStatus.getConnect()){
                setConnectStatus(connect());
                if(systemStatus.getConnect()){
                    clientThread = new ClientThread(socket);
                    clientThread.start();
                }
            }else if(!systemStatus.getLogin()){
                 login();
                 synchronized (systemStatus){
                    try{
                        systemStatus.wait(500);//延时等待回应
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }else {
                 input();
            }
        }
    }

    /**
     * 连接
     * @return
     */
    private boolean connect(){
        boolean retFlag = false;
        String portStr = Keyboard.getInput(PrintInfo.INPUT_PORT);
        try{
            if(Check.isPort(portStr)){
                try {
                    InetAddress localAddress = InetAddress.getLocalHost();
                    try{
                        socket = new Socket(localAddress.getHostAddress(),Integer.parseInt(portStr));
                        outputStream = socket.getOutputStream();
                        retFlag = true;
                    }catch (ConnectException e){
                        //e.printStackTrace();
                        System.out.println(PrintInfo.ERROR_SOCKET+e.getMessage());

                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                }catch (UnknownHostException e){
                    e.printStackTrace();

                }
            }else {
                System.out.println(PrintInfo.ERROR_PORT_ILLEGALITY);
            }
        }finally {
            return retFlag;
        }

    }

    /**
     * 登陆
     */
    private void login(){
        String name = Keyboard.getInput(PrintInfo.INPUT_NAME);
        String password = Keyboard.getInput(PrintInfo.INPUT_PASSWORD);
        User user = new User(name,password);
        DataPackage dataPackage = new DataPackage(CommandType.COM_CHECK_LOGIN, JSON.toJSONString(user));
        send(dataPackage.toByte());
    }

    /**
     * 输入
     */
    private void input(){
        String msgStr = Keyboard.getInput("");
        if(msgStr.startsWith(cmdStr)){
            inputCommandParse(msgStr);
        }else {
            chat(msgStr);
        }
    }

    /**
     * 聊天
     */
    private void chat(String data){
        Message  msg = new Message(data);
        DataPackage dataPackage = new DataPackage(CommandType.COM_CHAT_SEND, JSON.toJSONString(msg));
        if(null != socket){
            try{
                byte[] send =dataPackage.toByte();
                socket.getOutputStream().write(send);

            }catch (SocketException e){
                System.out.println(PrintInfo.ERROR_SOCKET+ e.getMessage());
                systemStatus.reset();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 输入命令解析
     * @param data
     */
    private void inputCommandParse(String data){
        String cmd = data.substring(cmdStr.length());
        try{
            byte command = (byte) Integer.parseInt(cmd);
            send(new DataPackage(command,"").toByte());
            try{
                Thread.sleep(500); //延时等待回应
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
            System.out.println(PrintInfo.ERROR_CMD_FORMAT);
        }

    }

    /*控制部分*/
    public void setConnectStatus(boolean connectStatus) {
        systemStatus.setConnect(connectStatus);
        if(connectStatus){
            System.out.println(PrintInfo.SUCCESS_CONNECT);
        }else {
            System.out.println(PrintInfo.ERROR_CONNECTED_FAILED);
        }
    }

    public void setLoginStatus(boolean loginStatus) {
        if(loginStatus){
            System.out.println(PrintInfo.SUCCESS_LOGIN);
            System.out.println(PrintInfo.INFO_INPUT_LOGOUT);

            System.out.println(PrintInfo.INPUT_MSG);

        }else {
            System.out.println(PrintInfo.ERROR_LOGIN_FAILED);
        }

        synchronized (systemStatus){
            systemStatus.setLogin(loginStatus);
            systemStatus.notify();
        }
    }

    /**
     * 发送
     * @param data
     */
    void send(byte[] data){
        if(null != outputStream){
            try{
                outputStream.write(data);
            }catch (IOException e){
                System.out.println(PrintInfo.ERROR_SOCKET+e.getMessage());
                systemStatus.reset();
               // e.printStackTrace();
            }
        }
    }

    class ClientThread extends Thread{
        private Socket socket = null;
        private boolean runFlag = true;
        InputStream inputStream = null;

        ClientThread(Socket socket){
            this.socket = socket;
            try{
                inputStream = socket.getInputStream();
            }catch (IOException e){
                e.printStackTrace();
                stopRun();
            }
        }

        @Override
        public void run() {
            try {
                while (runFlag){
                    recv(inputStream);}
            }finally {
                try{
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
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
                    e.printStackTrace();

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

        /**
         * 解析数据包
         * @param dataPackage
         */
        private void parseDataPackage(DataPackage dataPackage){
            //System.out.println(PrintInfo.INFO_RECV_COMMAND+dataPackage.getCommand());
            switch (dataPackage.getCommand()) {
                case CommandType.COM_SYSTEM_RESPOND:
                    Respond respond = JsonParse.respond(dataPackage.getData());
                    parseRespond(respond);
                    break;
                case CommandType.COM_CHAT_SEND:
                    Message msg = JsonParse.message(dataPackage.getData());
                    System.out.println(msg.getMessage());
                    break;
                default:
                    break;
            }

        }

        /**
         * 解析应答
         * @param respond
         */
        private void parseRespond(Respond respond){
            switch (respond.getCommand()){
                case CommandType.COM_CHECK_LOGIN:
                    setLoginStatus(respond.getFlag());
                    break;
                case CommandType.COM_CHECK_LOGOUT:
                    setLoginStatus(false);
                    break;
            }
        }

        /**
         * 停止线程运行
         */
        private void stopRun(){
            runFlag = false;
        }

    }

}



