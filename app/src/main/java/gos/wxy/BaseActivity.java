package gos.wxy;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import gos.wxy.view.EditTextWithIcon;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        TextView textView = (TextView)findViewById(R.id.menu_live);
        Drawable[] drawables = textView.getCompoundDrawables();
        drawables[1].setBounds(0,10,50,60);
        textView.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3]);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BaseActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });

        textView = (TextView)findViewById(R.id.baidu);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView phone = (TextView)findViewById(R.id.phone);
        phone.setAutoLinkMask(Linkify.ALL);
        phone.setMovementMethod(LinkMovementMethod.getInstance());

        TextView html = (TextView)findViewById(R.id.html);

        String s = "<font color='blue'><b>baidu.com</b>";

        html.setText(Html.fromHtml(s));

        final EditTextWithIcon editUser = (EditTextWithIcon)findViewById(R.id.editUser);
        final EditTextWithIcon editPassword = (EditTextWithIcon)findViewById(R.id.editPassword);

        Button buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUser.clearFocus();
                editPassword.clearFocus();

                String user = editUser.getText().toString();
                String password = editPassword.getText().toString();
                if(user.length()<1){
                    editUser.setShakeAnimation();
                } else if(password.length()<1){
                    editPassword.setShakeAnimation();
                }
            }
        });


       /* EditText editUser = (EditText)findViewById(R.id.editUser);

        drawables = editUser.getCompoundDrawables();
        Rect rect = drawables[0].getBounds();
        rect.offset(-25,0);

        drawables[0].setBounds(rect);
        editUser.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3]);
        editUser.requestFocus();//获取焦点才能设置成功

        EditText editPassword= (EditText)findViewById(R.id.editPassword);

        drawables = editPassword.getCompoundDrawables();

        drawables[0].setBounds(rect);
        editPassword.setCompoundDrawables(drawables[0],drawables[1],drawables[2],drawables[3]);
        editPassword.requestFocus();//获取焦点才能设置成功*/


    }
}
