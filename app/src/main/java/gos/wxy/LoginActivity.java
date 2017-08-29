package gos.wxy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import gos.wxy.base.EventMsg;
import gos.wxy.base.LoginSetting;
import gos.wxy.base.Respond;
import gos.wxy.base.User;
import gos.wxy.db.MyDBOpenHelper;
import gos.wxy.db.SharedDb;
import gos.wxy.enums.EnumEventMode;
import gos.wxy.tool.DataPackage;
import gos.wxy.tool.Event;
import gos.wxy.tool.JsonParse;
import gos.wxy.view.EditTextWithIcon;

import static gos.wxy.define.CommandType.*;

public class LoginActivity extends AppCompatActivity  implements CompoundButton.OnCheckedChangeListener {
    private final String TAG = this.getClass().getCanonicalName();

    /*登陆输入*/
    EditTextWithIcon editUser;
    EditTextWithIcon editPassword;

    /*登陆设置*/
    CheckBox checkRemember;
    CheckBox checkAuto;
    CheckBox checkHiding;

    SharedDb sharedDb = new SharedDb(this);
    MyDBOpenHelper db;

    /**
     * 事件接收
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recvEvent(EventMsg msg){
        if(msg.getEventMode() == EnumEventMode.IN){
            switch (msg.getCommand()){
                case COM_SYSTEM_RESPOND:
                    parseRespond(JsonParse.respond(msg.getData()));
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Event.register(this);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Event.unregister(this);
    }

    private void initView(){
        initViewElement();
        initViewData();

    }

    private void initViewElement(){
        checkRemember = (CheckBox)findViewById(R.id.checkRemember);
        checkAuto = (CheckBox)findViewById(R.id.checkAuto);
        checkHiding = (CheckBox)findViewById(R.id.checkHiding);

        checkRemember.setOnCheckedChangeListener(this);
        checkAuto.setOnCheckedChangeListener(this);
        checkHiding.setOnCheckedChangeListener(this);

        editUser = (EditTextWithIcon)findViewById(R.id.editUser);
        editPassword= (EditTextWithIcon)findViewById(R.id.editPassword);
        editUser.requestFocus();
        Button buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUser.clearFocus();
                editPassword.clearFocus();
                login();


            }
        });
    }

    private void initViewData(){
        initLoginSetting();
        //initDB();
    }

    private void initDB() {
        db = new MyDBOpenHelper(this,"user.db",null,1);
    }

    /**
     * 初始化登陆设置
     */
    private void initLoginSetting(){
        LoginSetting loginSetting = sharedDb.getLoginSetting();
        if(null != loginSetting){
            checkAuto.setChecked(loginSetting.getAuto());
            checkHiding.setChecked(loginSetting.getHiding());
            checkRemember.setChecked(loginSetting.getRemember());

            if(loginSetting.getRemember()){
                initLoginInput();
                if(loginSetting.getAuto()){
                    login();
                }
            }
        }

    }

    private void initLoginInput(){
        User user = sharedDb.getUser();
        if(null != user){
            editUser.setText(user.getName());
            editPassword.setText(user.getPassword());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){//选中
            switch (buttonView.getId()){
                case R.id.checkRemember:
                    break;
                case R.id.checkAuto:
                    checkRemember.setChecked(isChecked);
                    break;
                case R.id.checkHiding:
                    break;
                default:
                    break;
            }
        }else { //取消
            switch (buttonView.getId()){
                case R.id.checkRemember:
                    checkAuto.setChecked(isChecked);
                    break;
                case R.id.checkAuto:

                    break;
                case R.id.checkHiding:
                    break;
                default:
                    break;
            }
        }

    }

    private LoginSetting getLoginSettingFromView(){
        LoginSetting loginSetting = new LoginSetting();
        loginSetting.setAuto(checkAuto.isChecked());
        loginSetting.setHiding(checkHiding.isChecked());
        loginSetting.setRemember(checkRemember.isChecked());

        return loginSetting;
    }

    private void login(){
        String name = editUser.getText().toString();
        String password = editPassword.getText().toString();
        if(name.length()<1){
            editUser.setShakeAnimation();
        } else if(password.length()<1){
            editPassword.setShakeAnimation();
        }else {
            //Toast.makeText(LoginActivity.this, "用户名:"+name+"\n"+"密码:"+password, Toast.LENGTH_SHORT).show();
            User user = new User(name,password);
            sendLogin(user);
            saveSetInfo(user);
        }
    }

    /**
     * 发送登陆信息
     * @param user
     */
    private void sendLogin(User user){
        Event.send(new EventMsg(COM_CHECK_LOGIN, JSON.toJSONString(user),EnumEventMode.OUT));
    }


    /**
     * 保存设置信息
     * @param user
     */
    private void saveSetInfo(User user){
        LoginSetting loginSetting = getLoginSettingFromView();
        sharedDb.setLoginSetting(loginSetting);
        if(loginSetting.getRemember()){
            sharedDb.setUser(user);

        }
    }

    private void jumpMainActivity(){
        Intent intent = new Intent(this,ChatActivity.class);
        startActivity(intent);
    }

    /**
     * 解析应答
     * @param respond
     */
    private void parseRespond(Respond respond){
        switch (respond.getCommand()){
            case COM_CHECK_LOGIN:
                if(respond.getFlag()){
                    jumpMainActivity();
                }else {
                    Toast.makeText(LoginActivity.this, "用户名/密码有误，请重新输入！", Toast.LENGTH_SHORT).show();

                }
                break;
            case COM_CHECK_LOGOUT:

                break;
        }

    }

}

