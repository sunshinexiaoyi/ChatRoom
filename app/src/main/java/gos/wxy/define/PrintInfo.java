package gos.wxy.define;

/**
 * 打印提示类
 * Created by wuxy on 2017/7/31.
 */

public interface PrintInfo {
    String INPUT_PORT = "请输入端口：";
    String INPUT_NAME= "请输入用户名：";
    String INPUT_PASSWORD= "请输入密码：";
    String INPUT_MSG= "请输入聊天信息：";

    String ERROR_CLIENT_DISCONNECTED= "客户端断开连接!";
    String ERROR_SERVER_DISCONNECTED= "服务器断开连接!";
    String ERROR_CONNECTED_FAILED= "连接失败!";
    String ERROR_LOGIN_FAILED= "登陆失败!";
    String ERROR_PORT_ILLEGALITY= "输入端口不合法!";

    String ERROR_CMD_FORMAT= "命令格式有误!";
    String ERROR_DATAPACKAGE_FAILED= "数据包构建异常!";

    String ERROR_SOCKET= "网络连接异常:";
    String ERROR_PARSE_DATA_FAILED = "解析数据失败";

    String ERROR_RECV_LEN = "接收长度为-1，结束！";


    String SUCCESS_ADD_USER= "添加用户成功!";
    String SUCCESS_LOGIN= "登陆成功!";
    String SUCCESS_CONNECT= "连接成功!";

    String INFO_ADDRESS= "地址:";
    String INFO_PORT= "端口:";

    String INFO_WAIT_CLIENT= "等待客户端连接";
    String INFO_CLIENTED= "客户端已连接";

    String INFO_RECV_COMMAND= "接收到命令：";
    String INFO_RECV_LEN= "接收数据长度：";
    String INFO_SEND_LEN= "发送数据长度：";
    String INFO_SOCKET_CLOSE= "网络连接关闭!";
    String INFO_ONLINE= "上线了!";
    String INFO_OFFLINE = "下线了!";
    String INFO_REMOVE= "移除!";
    String INFO_RESET= "重置系统状态!";
    String INFO_INPUT_LOGOUT = "可输入 cmd:11 登出!";
    String INFO_LOGOUT = "退出登陆";

    String INFO_EXIT_THREAD = "结束线程";
    String INFO_START_THREAD = "开始线程";


    String INFO_ME = "我";




    String SYSTEM_USER= "系统信息";




}
