package gos.wxy.base;

/**
 * Created by wuxy on 2017/8/24.
 */

public class Net {
    private String address;
    private int port;

    public Net() {
    }

    public Net(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
