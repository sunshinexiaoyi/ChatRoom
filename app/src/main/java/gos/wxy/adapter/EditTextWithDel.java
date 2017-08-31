package gos.wxy.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;


/**
 * Created by lp on 2017/8/11.
 * 带删除按钮的EditText
 * 为EditText设置addTextChangedListener，
 * 然后重写TextWatcher（）里的抽象方法，这个用于监听输入框变化;
 * 然后setCompoundDrawablesWithIntrinsicBounds设置小叉叉的图片;
 * 最后，重写onTouchEvent方法，如果点击区域是小叉叉图片的位置，清空文本
 */

public class EditTextWithDel extends EditText {

    private final static String TAG = "EditTextWithDel";
    private Drawable img;

    public EditTextWithDel(Context context) {
        super(context);
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //添加EditText文本改变的监听器
    private void init() {
       // img = context.getResources().getDrawable(R.drawable.edit_delete); //获取资源文件
        img = getCompoundDrawables()[2]; // 直接获取右侧图片
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setDrawable();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setDrawable();

    }

    // 设置删除图片
    private void setDrawable() {
        if(length() > 1) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null , img, null);
        } else {
            setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0 , 0, 0);
        }
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (img != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX(); //鼠标点击的位置（相对于屏幕）
            int eventY = (int) event.getRawY();

            Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);
            Rect rect = new Rect();
            //getLocalVisibleRect(Rect r)方法可以把视图的长和宽映射到一个Rect对象上
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;

            if (rect.contains(eventX, eventY))
                setText("");
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
