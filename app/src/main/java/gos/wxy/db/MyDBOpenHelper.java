package gos.wxy.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import gos.wxy.base.User;

/**
 * Created by wuxy on 2017/8/23.
 */

public class MyDBOpenHelper extends SQLiteOpenHelper {
    private String TAG = this.getClass().getSimpleName();
    private SQLiteDatabase db;


    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i(TAG,"onUpgrade");
        db = getWritableDatabase();
    }

    //数据库第一次创建时被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user(num INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),password varchar(20))");
        Log.i(TAG,"onCreate");
        this.db =db;

    }

    //软件版本号发生改变时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE person ADD phone VARCHAR(12) NULL");
        Log.i(TAG,"onUpgrade");

    }

    public void addUser(User user){
        db.execSQL("insert into user(name,password) values(?,?)",new String[]{user.getName(),user.getPassword()});
        Log.i(TAG,"addUser:"+user.getName());
    }

    public void deleteUser(User user){
        db.execSQL("delete from user where name = ?",new String[]{user.getName()});
        Log.i(TAG,"deleteUser:"+user.getName());

    }

    public boolean exist(User user){
        Cursor cursor = db.rawQuery("select * from user where name = ?",new String[]{user.getName()});
        boolean ret = cursor.moveToNext();
        cursor.close();
        Log.i(TAG,"exist:"+user.getName()+" "+ret);
        return ret;
    }

    public long getUserCount(){
        Cursor cursor = db.rawQuery("select count(*) from user",null);
        cursor.moveToFirst();
        long ret = cursor.getLong(0);
        cursor.close();
        return ret;
    }

    public void update(User user){
        db.execSQL("update user set password = ? where name = ?",new String[]{user.getPassword(),user.getName()});
    }



}
