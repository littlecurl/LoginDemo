package cn.edu.heuet.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.IOException;

import cn.edu.heuet.login.common.Common;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener, Common {

    // 声明UI对象，默认权限是protected，即在同一个包内可以通用
    Button bt_login = null;
    EditText et_account = null;
    EditText et_password = null;
    TextView tv_register = null;
    TextView tv_forget_password = null;
    TextView tv_service_agreement = null;
    ImageView iv_third_method1 = null;
    ImageView iv_third_method2 = null;
    ImageView iv_third_method3 = null;

    SharedPreferences sp = null;
    private SharedPreferences.Editor editor;
    String token = null;

    private final String TAG = "LoginActivity"; // Log打印的通用Tag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 初始化UI对象
        initUI();
        // 为点击事件设置监听器
        setOnClickListener();

        /* 创建一个sp对象 */
        sp = getSharedPreferences("login_info",MODE_PRIVATE);
        /*
        第一次登陆时，token为null
        这里只是token自动登录的原理
        真实用法比这个复杂一些
        */
        token = sp.getString("token",null);

        /* 要想实现自动登录，最好还是有个splash启动界面，应该在splash界面进行判断，而不是login界面 */
        if (token != null){
            String telphone = sp.getString("telphone",null);
            Intent it_login_to_main = new Intent(LoginActivity.this,MainActivity.class);
            it_login_to_main.putExtra("telphone",telphone);
            startActivity(it_login_to_main);
            // 登录成功后，登录界面就没必要占据资源了
            finish();
        }
    }

    // 初始化UI对象
    private void initUI(){
        bt_login = findViewById(R.id.bt_login);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password1);
        tv_register = findViewById(R.id.tv_register);
        tv_forget_password = findViewById(R.id.tv_forget_password);
        tv_service_agreement = findViewById(R.id.tv_service_agreement);
        iv_third_method1 = findViewById(R.id.iv_third_method1);
        iv_third_method2 = findViewById(R.id.iv_third_method2);
        iv_third_method3 = findViewById(R.id.iv_third_method3);
    }

    // 为点击事件的UI对象设置监听器
    private void setOnClickListener(){
        bt_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);
        tv_service_agreement.setOnClickListener(this);
        iv_third_method1.setOnClickListener(this);
        iv_third_method2.setOnClickListener(this);
        iv_third_method3.setOnClickListener(this);
    }

    // 因为 implements View.OnClickListener 所以OnClick方法可以写到onCreate方法外
    @Override
    public void onClick(View v) {
        // 获取用户输入的账号和密码
        String account = et_account.getText().toString();
        String password = et_password.getText().toString();

        switch (v.getId()){
            // 登录按钮响应事件
            case R.id.bt_login:
                // 注释的这两个方法是我尝试过的失败的方法
                // asyncValidate2(account, password);
                // xUtilsPost(account,password);
                // 因为验证是耗时操作，所以独立成方法，在方法中开辟子线程
                // 避免在当前UI线程进行耗时操作
                asyncValidate(account,password);
                break;
            // 注册账号响应事件
            case R.id.tv_register:
                Intent it_login_to_register = new Intent(this, RegisterActivity.class);
                // 给用户一个良好的体验，填写过的就不需要再填了
                it_login_to_register.putExtra("account",account);
                it_login_to_register.putExtra("password",password);
                startActivity(it_login_to_register);
                break;
            case R.id.tv_forget_password:
                // 跳转到修改密码界面
                break;
            case R.id.tv_service_agreement:
                // 跳转到服务协议界面
                break;
            case R.id.iv_third_method1:
                // 跳转第三方登录方式1
                break;
            case R.id.iv_third_method2:
                // 跳转第三方登录方式2
                break;
            case R.id.iv_third_method3:
                // 跳转第三方登录方式3
                break;
        }
    }

    // okhttp的异步请求
    // account 可以是 telphone或者username
    // 但目前只实现了telphone，其实兼容二者原理也不难，使用正则判断一下就可以了，
    // 后端再加个验证方法，综合原因我不给实现了，留给你们练手
    private void asyncValidate(final String account, final String password){
        // Android端的判空处理
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证属于耗时操作，开辟子线程
        new Thread( new Runnable() {

            @Override
            public void run() {
                // okhttp的使用，POST，异步； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                final String telphone = account;
                // 2、构建请求体
                RequestBody requestBody = new FormBody.Builder()
                        .add("telphone", telphone)
                        .add("password", password)
                        .build();
                // 3、发送请求，特别强调这里是POST方式
                Request request = new Request.Builder()
                        .url(loginURL)
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue( new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        showToastInThread(LoginActivity.this,"网络异常, 请重试！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, response.protocol() + " " +response.code() + " " + response.message());
                        Headers headers = response.headers();
                        for (int i = 0; i < headers.size(); i++) {
                            Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                        }
                        // response.body().string()只能调用一次，多次调用会报错
                        // 因为在下面getStatus()方法中调用了，这里不能再次调用
                        // Log.d(TAG, "onResponse: " + response.body().string());
                        String responseBodyStr = response.body().string();
                        JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                        // 如果返回的status为success，代表验证通过
                        if ( getStatus(LoginActivity.this, responseBodyJSONObject) ){
                            /* 更新token，下次自动登录 */
                            editor = sp.edit();
                            editor.putString("token","token_value");
                            editor.putString("telphone",telphone);
                            if (editor.commit()) {
                                Intent it_login_to_main = new Intent(LoginActivity.this, MainActivity.class);
                                it_login_to_main.putExtra("telphone", telphone);
                                startActivity(it_login_to_main);
                                // 登录成功后，登录界面就没必要占据资源了
                                finish();
                            } else {
                                showToastInThread(LoginActivity.this, "token保存失败，请重新登录");
                                Log.d(TAG,"token保存失败，请重新登录");
                            }
                        } else {
                            getResponseData(LoginActivity.this, responseBodyJSONObject);
                            Log.d(TAG, "账号或密码验证失败");
                        }
                    }
                });

            }
        }).start();
    }

    // 使用Gson解析response的JSON数据，返回布尔值
    private boolean getStatus(Context context, JsonObject responseBodyJSONObject)throws IOException {
        // 通过JSON对象获取对应的属性值
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status.equals("success");
    }

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
    实现在子线程中显示Toast
     */
    private void showToastInThread(Context context,String msg){
        Looper.prepare();
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    // 以下是我尝试过的失败的方法

/*
    private void asyncValidate2 (final String username, final String password) {
        Log.d("LoginActivity","username:"+username+" password: "+password);
        Ion.with(this)
            .load("POST", "http://172.31.84.128:8090/user/login")
            .setBodyParameter("username", username)
            .setBodyParameter("password", password)
            .asJsonObject()
            .withResponse()
            .setCallback(
                new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> response) {
                        if (response != null) {
                            if (getStatus(response)){
                                // Success！！ Intent
                                Intent it_login_to_main =
                                        new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(it_login_to_main);
                            }  else {
                                //FAIL!! Show TOAST!
                                Toast.makeText(LoginActivity.this,
                                        "登录失败",Toast.LENGTH_SHORT).show();
                            }
                            Log.d("LoginActivity","响应码："+response.getHeaders().code());
                        }
                    }
                }
            );

    }

    private boolean getStatus( Response<JsonObject> response ){
        String status = null;
        int errorCode = -1;
        String errorMsg = null;
        try {
            JsonObject result = response.getResult();  //创建jsonObject对象

            status = result.get("status").getAsString(); // 解析status

            JsonObject data=result.get("data").getAsJsonObject(); // 获取result对象中的data对象

            errorCode = data.get("errorCode").getAsInt();
            errorMsg = data.get("errorMsg").getAsString();

//           登录成功返回的json为{ "status":"success", "data":null }
//           只获取status即可，data为null
//           如果还想获取对象中的对象，比如说data对象不空
//		   返回值为
//		  	{
//		   		"status":"success",
//		   		"data":{
//		   			"errorCode":123,
//		            "errorMsg":"参数不合法"
//		  		}
//		    }
//		   可以再创建一个对象
//		   JsonObject data=result.get("data").getAsJsonObject(); // 获取result对象中的data对象
//
//           int errorCode = data.get("errorCode").getAsInt();
//           String errorMsg = data.get("errorMsg").getAsString();

        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return status.equals("success");
    }
*/

/*
    private void xUtilsPost(String account, String password){
        RequestParams params = new RequestParams();
        params.addHeader("name", "value");
        params.addQueryStringParameter("name", "value");

        // 只包含字符串参数时默认使用BodyParamsEntity，
        // 类似于UrlEncodedFormEntity（"application/x-www-form-urlencoded"）。
        params.addBodyParameter("name", "value");

        // 加入文件参数后默认使用MultipartEntity（"multipart/form-data"），
        // 如需"multipart/related"，xUtils中提供的MultipartEntity支持设置subType为"related"。
        // 使用params.setBodyEntity(httpEntity)可设置更多类型的HttpEntity（如：
        // MultipartEntity,BodyParamsEntity,FileUploadEntity,InputStreamUploadEntity,StringEntity）。
        // 例如发送json参数：params.setBodyEntity(new StringEntity(jsonStr,charset));
        params.addBodyParameter("file", new File("path"));

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                "uploadUrl....",
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        // testTextView.setText("conn...");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (isUploading) {
                            //testTextView.setText("upload: " + current + "/" + total);
                        } else {
                            //testTextView.setText("reply: " + current + "/" + total);
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //testTextView.setText("reply: " + responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        //testTextView.setText(error.getExceptionCode() + ":" + msg);
                    }
                });
    }
*/


}
