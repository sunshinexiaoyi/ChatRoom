package gos.wxy.tool;


import java.io.Serializable;
import java.util.Arrays;

import gos.wxy.exception.DataPackageException;

/**
 * 数据包类
 * Created by wuxy on 2017/7/31.
 */

public class DataPackage implements Serializable{
    public  final static int headLen = 5;

    private byte command;
    private int dataLen = 0;
    private String data = null;

    public DataPackage(byte[] data) throws DataPackageException {
        if(data.length >= headLen){
            setCommand(data[0]);
            setDataLen(byteToInt(data,1,4));
            setData(new String(Arrays.copyOfRange(data,headLen,data.length)));
        }
        else {
            throw new DataPackageException();
        }
    }

    public DataPackage(byte command) {
        this.command = command;
    }

    public DataPackage(byte command, String data){
        this.command = command;
        this.data = data;
        if(null != data){
            this.dataLen = data.getBytes().length;  //数据长度，转化成byte[] 的长度
        }
    }

    public void setCommand(byte command) {
        this.command = command;
    }
    public byte getCommand() {
        return command;
    }

    public void setData(String data) {
        this.data = data;
    }
    public void setData(byte[] data) {
        this.data = new String(data);
    }
    public String getData() {
        return data;
    }

    public int getDataLen() {
        return dataLen;
    }
    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }

    /**
     * 将对象转化成byte[]
     * @return  byte[]
     */
    public byte[] toByte(){
        byte[] sendData = new byte[headLen+getDataLen()];
        int position = 0;

        sendData[position++] = command;
        position += intToByte(sendData,position,4,getDataLen());

        if (null != getData()) {
            byte[] data = getData().getBytes();
            System.arraycopy(data,0, sendData,position,data.length);
        }

        return sendData;
    }

    /**
     * 将byte数组转化成int类型 小端模式
     * @param srcBytes  输入数组
     * @param from  开始索引 包括该适索引
     * @param len   长度 小于4
     * @return  -1 失败 否则成功
     */
    private int byteToInt(byte[] srcBytes,int from,int len){
        int num = 0;
        if(4 < len){
            return -1;
        }

        for(int i=0;i<len;i++){
            num |= (srcBytes[from+i]&0xff)>>(Byte.SIZE*i);
        }

        return num;
    }

    /**
     * 将int转为byte数组
     * @param desBytes  目的数组
     * @param from  开始索引 包括该适索引
     * @param len   长度 小于4
     * @param srcInt    源int
     * @return  数组索引偏移长度
     */
    private int intToByte(byte[] desBytes,int from,int len,int srcInt){
        int i = 0;
        for(i=0;i<len;i++){
            desBytes[from+i] = (byte)((srcInt>>(Byte.SIZE*i))&0xff);
        }
        return i;
    }

}
