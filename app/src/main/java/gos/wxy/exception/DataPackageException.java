package gos.wxy.exception;


import gos.wxy.define.PrintInfo;

/**
 * 异常类
 * 数据包解析异常
 * Created by wuxy on 2017/7/31.
 */

public class DataPackageException extends Exception{
    static private String msg = PrintInfo.ERROR_DATAPACKAGE_FAILED;
    public DataPackageException(){
        super(msg);
    }
}
