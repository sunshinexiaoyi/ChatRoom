package gos.wxy.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import gos.wxy.base.Net;
import gos.wxy.define.PrintInfo;
import gos.wxy.exception.DataPackageException;

/**
 * Created by wuxy on 2017/8/24.
 */

public class TcpNet {
    private Socket socket;
    private OutputStream out;
    private InputStream in;


    /**
     * 连接
     * @param net
     * @return
     */
    public boolean connect(Net net) {
        boolean ret = false;
        try {
            socket = new Socket(net.getAddress(),net.getPort());
            out = socket.getOutputStream();
            in = socket.getInputStream();
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(PrintInfo.ERROR_SOCKET+e.getMessage());

        }finally {
            return ret;
        }
    }

    /**
     * 发送
     * @param data
     */
    public void send(byte[] data)throws Exception{
        out.write(data);
    }


    public DataPackage recv() throws Exception{
        DataPackage dataPackage = null;
        try{
            byte[] head = new byte[DataPackage.headLen];
            int len = in.read(head);   //接收头部段
            if(len != head.length){
                return null;
            }
            try{
                dataPackage = new DataPackage(head);
                byte[] data = new byte[dataPackage.getDataLen()];
                len = in.read(data);   //接收数据段
                if(len != data.length){
                    return null;
                }
                dataPackage.setData(data);

            }catch (DataPackageException e){
                System.out.println(PrintInfo.ERROR_PARSE_DATA_FAILED);
                e.printStackTrace();
                dataPackage = null;
            }
        }catch (SocketException e){
            System.out.println(PrintInfo.INFO_SOCKET_CLOSE);
            e.printStackTrace();
            throw e;
        }
        catch (IOException e){
            e.printStackTrace();
            throw e;
        }finally {
            return dataPackage;
        }

    }

    public void close(){
        try {
            if(null != socket){
                socket.close();
            }
            if(null != in){
                in.close();
            }
            if(null != out){
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
