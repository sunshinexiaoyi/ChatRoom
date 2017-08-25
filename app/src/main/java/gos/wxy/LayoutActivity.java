package gos.wxy;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class LayoutActivity extends AppCompatActivity {
    OrientationEventListener orientationEventListener = null;
    FrameLayout frameLayout = null;
    final static int WHAT_LODING = 0x1;

    Handler handler = new Handler(){
        int i = 0;
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case WHAT_LODING:
                    frameLayout.setForeground(drawable[i++%8]);
                    break;
                default:
                    break;
            }
        }
    };

    Drawable[] drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        Log.i("orientation","onCreate");
        frameLayout = (FrameLayout)findViewById(R.id.frame_layout);
        loding();
       // init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("orientation",":"+"onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null != orientationEventListener){
            orientationEventListener.enable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != orientationEventListener){
            orientationEventListener.disable();
        }
    }


    public void sendMessage(View v){
        Log.i("click","click");
    }



    private void init(){



        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                //Log.i("orientation",":"+orientation);

                //顺时针旋转
                if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN){
                    Log.i("orientation","平放");
                   return;
                }
                if(orientation >360 || orientation <10){
                    Log.i("orientation","0");
                }else if(orientation >80 && orientation <100){
                    Log.i("orientation","90");
                }else if(orientation >170 && orientation <190){
                    Log.i("orientation","180");
                }else if(orientation >260 && orientation <280){
                    Log.i("orientation","270");
                }

            }
        };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.i("onConfigurationChanged","横屏");
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i("onConfigurationChanged","竖屏");
        }
    }


    private void loding(){

        Drawable[] drawableLoading = {
                getResources().getDrawable(R.drawable.loading_center1),
                getResources().getDrawable(R.drawable.loading_center2),
                getResources().getDrawable(R.drawable.loading_center3),
                getResources().getDrawable(R.drawable.loading_center4),
                getResources().getDrawable(R.drawable.loading_center5),
                getResources().getDrawable(R.drawable.loading_center6),
                getResources().getDrawable(R.drawable.loading_center7),
                getResources().getDrawable(R.drawable.loading_center8),
        };

        drawable = drawableLoading;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(WHAT_LODING);
            }
        },0,500);
    }
}



