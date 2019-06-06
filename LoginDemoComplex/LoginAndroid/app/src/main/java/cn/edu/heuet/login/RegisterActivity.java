package cn.edu.heuet.login;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.edu.heuet.login.common.Common;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity
        implements View.OnClickListener, Common {

    // Log打印的通用Tag
    private final String TAG = "RegisterActivity";

    Button bt_get_otp = null;
    Button bt_submit_register = null;
    EditText et_telphone = null;
    EditText et_otpCode = null;
    EditText et_username = null;
    EditText et_gender = null;
    EditText et_age = null;
    EditText et_password1 = null;
    EditText et_password2 = null;

    String account = null;
    String password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // 初始化UI对象
        initUI();
        // 为点击事件设置监听器
        setOnClickListener();
        // 优化用户体验
        // 接收用户在登录界面输入的数据，输入过了就不该再输入了
        // 注意接收上一个页面Intent的信息，需要getIntent，而非重新new一个Intent
        Intent it_from_login = getIntent();
        account = it_from_login.getStringExtra("account");
        password =  it_from_login.getStringExtra("password");
        // 把对应的account设置到telphone输入框
        if (account.isEmpty()){
            et_telphone.setText(account);
        }
        if (password.isEmpty()){
            et_password1.setText(password);
        }
    }
    // 初始化UI对象
    private void initUI(){
        bt_get_otp = findViewById(R.id.bt_get_otp);
        bt_submit_register = findViewById(R.id.bt_submit_register);
        et_telphone = findViewById(R.id.et_telphone);
        et_otpCode = findViewById(R.id.et_otpCode);
        et_username = findViewById(R.id.et_username);
        et_gender = findViewById(R.id.et_gender);
        et_age = findViewById(R.id.et_age);
        et_password1 = findViewById(R.id.et_password1);
        et_password2 = findViewById(R.id.et_password2);
    }
    // 为点击事件的UI对象设置监听器
    private void setOnClickListener(){
        bt_get_otp.setOnClickListener(this);
        bt_submit_register.setOnClickListener(this);
    }

    // 因为 implements View.OnClickListener 所以OnClick方法可以写到onCreate方法外
    @Override
    public void onClick(View v) {
        String telphone = et_telphone.getText().toString();
        String otpCode = et_otpCode.getText().toString();
        String username = et_username.getText().toString();
        String gender = et_gender.getText().toString();
        String age = et_age.getText().toString();
        String password1 = et_password1.getText().toString();
        String password2 = et_password2.getText().toString();

        switch (v.getId()){
            case R.id.bt_get_otp:
                // 点击获取验证码按钮响应事件
                if(TextUtils.isEmpty(telphone)){
                    Toast.makeText(RegisterActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    asyncGetOtpCode(telphone);
                }
                break;
            case R.id.bt_submit_register:
                asyncRegister(telphone,otpCode,username,gender,age,password1,password2);
                // 点击提交注册按钮响应事件
                // 尽管后端进行了判空，但Android端依然需要判空
                break;
        }
    }
    // okhttp异步请求验证码
    private void asyncGetOtpCode( final String telphone){
        if (TextUtils.isEmpty(telphone)){
            Toast.makeText(this,"请输入手机号",Toast.LENGTH_SHORT).show();
        }
        // 发送请求属于耗时操作，开辟子线程
        new Thread( new Runnable() {
            @Override
            public void run() {
                // okhttp的使用，POST，异步； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(30,TimeUnit.SECONDS)
                        .build();
                // 2、构建请求体
                RequestBody requestBody = new FormBody.Builder()
                        .add("telphone", telphone)
                        .build();
                // 3、发送请求，特别强调这里是POST方式
                Request request = new Request.Builder()
                        .url(getOtpCodeURL)
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue( new Callback() {
                    // 5、重写两个回调方法
                    // onFailure有可能是请求连接超时导致的
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, response.protocol() + " " +response.code() + " " + response.message());
                        Headers headers = response.headers();
                        for (int i = 0; i < headers.size(); i++) {
                            Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                        }
                        // response.body().string()只能调用一次，多次调用会报错
                        // Log.d(TAG, "onResponse: " + response.body().string());
                        String responseData = response.body().string();
                        JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseData);
                        // 如果返回的status为success，代表获取验证码成功
                        if ( getResponseStatus(RegisterActivity.this,responseBodyJSONObject) ){
                            Log.d(TAG,"验证码已发送，注意查收！");
                        } else {
                            getResponseData(RegisterActivity.this,responseBodyJSONObject);
                        }
                    }
                });

            }
        }).start();
    }


    // okhttp异步请求进行注册
    // 参数统一传递字符串
    // 传递到后端再进行类型转换以适配数据库
    private void asyncRegister(final String telphone,final String otpCode,
                               final String username, final String gender,
                               final String age, final String password1,final String password2){

        if (TextUtils.isEmpty(telphone) || TextUtils.isEmpty(otpCode) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(gender) || TextUtils.isEmpty(age)
                || TextUtils.isEmpty(password1)|| TextUtils.isEmpty(password2)){
            Toast.makeText(RegisterActivity.this,"存在输入为空，不让注册",Toast.LENGTH_SHORT).show();
        } else if ( password1.equals(password2)){

            // 发送请求属于耗时操作，开辟子线程
            new Thread( new Runnable() {
                @Override
                public void run() {
                    // okhttp的使用，POST，异步； 总共5步
                    // 1、初始化okhttpClient对象
                    OkHttpClient okHttpClient = new OkHttpClient();
                    // 2、构建请求体
                    // 注意这里的name 要和后端意义对应，否则无法传递过去
                    RequestBody requestBody = new FormBody.Builder()
                            .add("telphone", telphone)
                            .add("otpCode" , otpCode)
                            .add("name", username)
                            .add("gender", gender)
                            .add("age", age)
                            .add("password", password1)
                            .build();
                    // 3、发送请求，特别强调这里是POST方式
                    Request request = new Request.Builder()
                            .url(registerURL)
                            .post(requestBody)
                            .build();
                    // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                    okHttpClient.newCall(request).enqueue( new Callback() {
                        // 5、重写两个回调方法
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            /*
                            作为开发版，下面的Log信息就保留在这了
                             */
                            Log.d(TAG, response.protocol() + " " +response.code() + " " + response.message());
                            Headers headers = response.headers();
                            for (int i = 0; i < headers.size(); i++) {
                                Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                            }

                            // response.body().string()只能调用一次，多次调用会报错
                            // Log.d(TAG, "onResponse: " + response.body().string());
                            String responseBodyStr = response.body().string();
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            // 如果返回的status为success，代表验证通过
                            if ( getResponseStatus(RegisterActivity.this, responseBodyJSONObject) ){
                                Intent it_register_to_main = new Intent(RegisterActivity.this,MainActivity.class);
                                it_register_to_main.putExtra("telphone",telphone);
                                startActivity(it_register_to_main);
                                // 注册成功后，注册界面就没必要占据资源了
                                finish();
                            } else {
                                getResponseData(RegisterActivity.this, responseBodyJSONObject);
                            }
                        }
                    });

                }
            }).start();
        } else {
            Toast.makeText(RegisterActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
        }
    }
    // 使用Gson解析response的JSON数据中的status，返回布尔值
    private boolean getResponseStatus(Context context, JsonObject responseBodyJSONObject) throws IOException {
        // Gson解析JSON，总共3步
        // 1、获取response对象的字符串序列化
        // String responseData = response.body().string();
        // 2、通过JSON解析器JsonParser()把字符串解析为JSON对象，
        //
        // *****前两步抽到方法外面了*****
        //
        // JsonObject jsonObject = (JsonObject) new JsonParser().parse(responseBodyStr);
        // 3、通过JSON对象获取对应的属性值
        String status = responseBodyJSONObject.get("status").getAsString();
        /*
        只有获取验证码时，返回的data才有数据
         */
        if (status.equals("successGetOtpCode")) {
            JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
            if (!dataObject.isJsonNull()) {
                String telphone = dataObject.get("telphone").getAsString();
                String otpCode = dataObject.get("otpCode").getAsString();
                /*
                 在子线程中更新UI
                 此方法必须在Toast方法之前调用
                 否则无法刷新出验证码，具体逻辑我也不清楚，尝试出来的
                  */
                setTextInThread(et_otpCode, otpCode);
                // 在子线程中显示Toast
                showToastInThread(context,"验证码："+otpCode);
                Log.d(TAG, "telphone: " + telphone + " otpCode: " + otpCode);
            }
            return true;
        }
        // 注册成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status.equals("success");
    }

    // 获取验证码返回

    // 使用Gson解析response返回异常信息的JSON中的data对象
    private void getResponseData(Context context, JsonObject responseBodyJSONObject){
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG,"errorCode: "+errorCode+" errorMsg: "+errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context,errorMsg);
    }

    /*
    在子线程中更新UI
    来实现自动填充验证码
     */
    private void setTextInThread(EditText editText,String otpCode){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText(otpCode);
            }
        });
    }
    /*
    实现在子线程中显示Toast
     */
    private void showToastInThread(Context context,String msg){
        Looper.prepare();
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Looper.loop();
    }




}



