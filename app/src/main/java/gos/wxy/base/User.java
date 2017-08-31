package gos.wxy.base;

/**
 * 用户类
 * name 用户名
 * password 密码
 * Created by wuxy on 2017/7/28.
 */

public class User {
    private String name;
    private String password = "123456"; //密码默认为 123456

    public User(){}         //一个无参public构造器
    public User(String name){
        this.name = name;
    }

    public User(String name,String password){
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if((null == obj)&&!(obj instanceof User)){
            return false;
        }

        User user = (User)obj;

        return (user.getName().equals(name) && user.getPassword().equals(password));
    }
}
