package com.wxy.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 键盘输入
 * Created by wuxy on 2017/7/31.
 */

public class Keyboard {
    static InputStreamReader reader = new InputStreamReader(System.in);
    static BufferedReader bufferedReader = new BufferedReader(reader);

    /**
     * 获取键盘输入
     * @param info  打印的提示信息
     * @return  输入的字符串
     */
    public static String getInput(String info){
        try{

            System.out.print(info);
            String portStr = bufferedReader.readLine();
            //System.out.println(portStr);
            return portStr;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }
}
