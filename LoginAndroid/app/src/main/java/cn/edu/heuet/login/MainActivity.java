package cn.edu.heuet.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_welcom = null;
    Button bt_logout = null;
    private SharedPreferences sp;
    private final String TAG = "MainActivity"; // Log打印的通用Tag
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_welcom = findViewById(R.id.tv_welcom);
        bt_logout = findViewById(R.id.bt_logout);
        bt_logout.setOnClickListener(this);


        Intent intent = getIntent();
        String telphone = intent.getStringExtra("telphone");
        tv_welcom.setText("手机号为："+telphone+" 的用户，欢迎你！");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_logout:
                sp = getSharedPreferences("login_info",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("token",null);
                if (editor.commit()){
                    Intent it_main_to_login = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(it_main_to_login);
                } else {
                    // 这个else基本执行不了
                    Intent it_main_to_login = new Intent();
                    startActivity(it_main_to_login);
                    Log.d(TAG,"token清除失败,下次还会自动登录");
                }
                break;
        }
    }
}
