package gos.wxy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import gos.wxy.base.EventMsg;
import gos.wxy.base.Respond;
import gos.wxy.base.User;
import gos.wxy.db.MyDBOpenHelper;
import gos.wxy.db.SharedDb;
import gos.wxy.enums.EnumEventMode;
import gos.wxy.tool.Event;
import gos.wxy.tool.JsonParse;

import static gos.wxy.define.CommandType.COM_CHECK_LOGIN;
import static gos.wxy.define.CommandType.COM_CHECK_LOGOUT;
import static gos.wxy.define.CommandType.COM_SYSTEM_RESPOND;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private ImageView img = null;
    private EditText account = null;
    private EditText password = null;
    private TextView login = null;
    private TextView pswForget = null;
    private CheckBox checkPsw = null;
    private CheckBox checkAccount = null;

    private SharedPreferences sp; // 轻量级的存储类
    private String name = null;
    private String psw = null;
    private boolean flagLogin = false;

    private final String USER_NAME = "USER_NAME";
    private final String PASSWORD = "PASSWORD";
    private final String AUTO_ISCHECK = "AUTO_ISCHECK"; //自动登录框
    private final String ISCHECK = "ISCHECK"; // 记住密码

    //记录用户首次点击返回键的时间
    private long firstTime = 0;
    private String[] accounts = {"小白", "小红", "小花", "小蓝", "小黑"};

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
        // 沉浸式状态栏
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 去除标题栏，必须放在R前
        setContentView(R.layout.activity_main);

        Event.register(this);
        initView();

        // Bitmap代表了一个原始的位图，并且可以对位图进行一系列的变换操作
        //把图片用bitmap对象保存，BitmapDrawable对象可以调用getBitmap方法，得到这个位图
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.account_user)).getBitmap();
        Bitmap bm = makeRoundCorner(bitmap);
        img.setImageBitmap(makeRoundCorner(bm, 50));

        checkBoxState();// 判断多选框的状态

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Event.unregister(this);
    }

    private void initView(){
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        img = (ImageView) findViewById(R.id.img);
        account = (EditText) findViewById(R.id.edit_account);
        password = (EditText) findViewById(R.id.edit_password);
        login = (TextView) findViewById(R.id.login);
        pswForget = (TextView) findViewById(R.id.psw_forget);
        checkPsw = (CheckBox) findViewById(R.id.checkPsw);
        checkAccount = (CheckBox) findViewById(R.id.checkAccount);

        img.setOnClickListener(this);
        login.setOnClickListener(this);
        pswForget.setOnClickListener(this);
        checkPsw.setOnCheckedChangeListener(this);
        checkAccount.setOnCheckedChangeListener(this);

    }

    // 判断记住密码多选框的状态
    private void checkBoxState() {
        // Log.e("ISCHECK",sp.getString(ISCHECK,"assa") );
        boolean isCheck = sp.getBoolean(ISCHECK, false);
        boolean auto = sp.getBoolean(AUTO_ISCHECK, false);
        checkPsw.setChecked(isCheck);
        checkAccount.setChecked(auto);

        if (isCheck) {
            account.setText(sp.getString(USER_NAME, name));
            password.setText(sp.getString(PASSWORD, psw));
            if (auto) {
                login();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                login();// 登录检测
                break;
            case R.id.psw_forget:
                Toast.makeText(this, "请输入账号登录", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img:
                break;
        }
    }

    // 复选框监听事件
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()) {
            case R.id.checkPsw:
                if(! checkPsw.isChecked()) {
                    checkAccount.setChecked(false);
                }
                break;
            case R.id.checkAccount:
                if(checkAccount.isChecked()) {
                    // Toast.makeText(this, checkAccount.getText().toString(), Toast.LENGTH_SHORT).show();
                    checkPsw.setChecked(true);
                }
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if(System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true; //不返回，一次就立马退出，77777777
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initDB() {
        db = new MyDBOpenHelper(this,"user.db",null,1);
    }

    //登录验证
    private void login(){
        if(0 != account.getText().length() && 0 != password.getText().length()) {
            name = account.getText().toString();
            psw = password.getText().toString();

            User user = new User(name,psw);
            sendLogin(user);//发送登陆信息

            savedCheck();//保存设置信息

        } else if(0 == account.getText().length()) {
            Toast.makeText(this, R.string.account_error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.psw_error, Toast.LENGTH_SHORT).show();
        }
    }

    // 复选框勾选后的存储
    public void savedCheck() {
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(ISCHECK, checkPsw.isChecked());
        editor.putBoolean(AUTO_ISCHECK, checkAccount.isChecked());
        //登录成功和记住密码框为选中状态才保存用户信息
        if(checkPsw.isChecked()) {
            //记住用户名、密码,Editor接口中的方法可以写入数据和读取数据
            editor.putString(USER_NAME, name);
            editor.putString(PASSWORD, psw);
        }
        editor.commit(); // 提交

        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    protected void onStop() {
        super.onStop();
        // 释放图片资源，避免内存泄漏
        releaseImageViews();
    }

    /**
     * 发送登陆信息
     * @param user
     */
    private void sendLogin(User user){
        Event.send(new EventMsg(COM_CHECK_LOGIN, JSON.toJSONString(user),EnumEventMode.OUT));
    }

    private void jumpMainActivity(){
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra("NAME", name);
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
                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case COM_CHECK_LOGOUT:

                break;
        }
    }

    // 将原图设置成正方形
    public static Bitmap makeRoundCorner(Bitmap bitmap)
    {
        int width = bitmap.getWidth();  // 获取位图的宽度
        int height = bitmap.getHeight(); // 获取位图的高度
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height)
        {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        }
        else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }
        //创建指定格式、大小的位图
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        //创建一个Canvas对象，并且在绘画成功后，将该画图区域转换为Drawable图片或者通过setBitmap(bitmap)显现出来。
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();   //画笔对象 paint
        Rect rect = new Rect(left, top, right, bottom); //矩形：rectangular,剪裁一个区域，得到的是正方形
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true); //防止边缘的锯齿
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);  //设置颜色来显示画图区域
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint); //绘制方形图,角是与弧度的
        // 设置图层混合模式 ，PorterDuff.Mode.SRC_IN：取两层绘制交集，显示上层图片到画布上
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint); // 绘制用于变色图
        return output;
    }

    //把正方形变成圆形,圆角
    public static Bitmap makeRoundCorner(Bitmap bitmap, int px)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, px, px, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    //释放
    private void releaseImageViews() {
        releaseImageView(img);
    }
    // Drawable 引起的内在泄漏 ,回调
    private void releaseImageView(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if (d != null)
        {
            d.setCallback(null);
        }
    }

}

