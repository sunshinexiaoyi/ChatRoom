package gos.wxy.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;


/**
 * Created by wuxy on 2017/8/10.
 */

public class EditTextWithIcon extends AppCompatEditText {
    private final  String TAG = this.getClass().getSimpleName();

    private Drawable[] drawables = null;
    private int[] offset = {-25,0};

    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;

    public EditTextWithIcon(Context context) {
        super(context);
        init();
    }

    public EditTextWithIcon(Context context, AttributeSet attrs) {//这构造方法很重要，不加这个很多属性不能在XML里面定义

        super(context, attrs);
        init();
    }

    public EditTextWithIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });

        drawables = getCompoundDrawables();

        Rect rect = drawables[0].getBounds();
        rect.offset(offset[0],offset[1]);
        drawables[0].setBounds(rect);

        mClearDrawable  = drawables[2];
        setDrawable();
        requestFocus(); //获取焦点才能设置成功
    }

    // 设置删除图片
    private void setDrawable() {
        if (length() < 1)
            setCompoundDrawables(drawables[0],drawables[1],null,drawables[3]);
        else
            setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3]);

    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClearDrawable != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchable = event.getX() > (getWidth()
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation(){
        this.setAnimation(shakeAnimation(5));
        requestFocus(); //获取焦点才能设置成功

    }


    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts){
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}
