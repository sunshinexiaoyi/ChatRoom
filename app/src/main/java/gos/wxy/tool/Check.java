package gos.wxy.tool;

/**
 * 验证类
 * Created by wuxy on 2017/7/31.
 */

public class Check {
    /**
     * 检查输入是否为合法端口
     * @param portStr   输入字符串
     * @return  true合法 false非法
     */
    public static boolean isPort(String portStr){
        if(null != portStr){
            try {
                int port = Integer.parseInt(portStr);
                if(port>0&& port<65535){
                    return true;
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
