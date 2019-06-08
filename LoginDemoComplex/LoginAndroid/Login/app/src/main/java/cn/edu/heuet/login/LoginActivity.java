package cn.edu.heuet.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // 声明UI对象
    Button bt_login = null;
    EditText et_account = null;
    EditText et_password = null;
    TextView tv_register = null;
    TextView tv_forget_password = null;
    TextView tv_service_agreement = null;
    ImageView iv_third_method1 = null;
    ImageView iv_third_method2 = null;
    ImageView iv_third_method3 = null;

    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;
    // 声明并初始化token
    String token = null;

    // Log打印的通用Tag
    private final String TAG = "LoginActivity";

    /*
        为了避免onCreate方法体看起来过于庞大
        把一些代码封装成方法放到onCreate之外了
        比如initUI()、setOnClickListener()等等
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 初始化UI对象
        initUI();
        // 为点击事件设置监听器
        setOnClickListener();

        /*
            设置当输入框焦点失去时提示错误信息
            第一个参数指明输入框对象
            第二个参数指明输入数据类型
            第三个参数指明输入不合法时提示信息
         */
        setOnFocusChangeErrMsg(et_account,"phone","手机号格式不正确");
        setOnFocusChangeErrMsg(et_password,"password","密码必须不少于6位");

        /*
          创建一个sp对象
          这种语法很奇怪，上来啥也没有就是get
          相当于声明了一个小型数据库，名为login_info，模式是私有模式
          私有模式应该是会对数据进行加密
          这种小型数据库只能存放k,v键值对
         */
        sp = getSharedPreferences("login_info",MODE_PRIVATE);

        /*
          这里只是token自动登录的原理，真实用法比这个复杂一些
          第一次登陆时，token为null，在最上面有初始化
          第二次登录时，token就会有值了
        */
        token = sp.getString("token",null);

        /*
         要想实现自动登录，最好还是再有个splash启动界面，
         就像微信启动页是个地球一样，
         应该在splash界面进行判断，而不是login界面
         */
        if (token != null){
            /*
              为了让登录成功后，界面有点东西
              我把输入的手机号传过去了
              正好可以练习一下Intent在不同Activity之间传参的知识点
             */
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
        bt_login = findViewById(R.id.bt_login); // 登录按钮
        et_account = findViewById(R.id.et_account); // 输入账号
        et_password = findViewById(R.id.et_password); // 输入密码
        tv_register = findViewById(R.id.tv_register); // 注册
        tv_forget_password = findViewById(R.id.tv_forget_password); // 忘记密码
        tv_service_agreement = findViewById(R.id.tv_service_agreement); // 同意协议
        iv_third_method1 = findViewById(R.id.iv_third_method1); // 第三方登录方式1
        iv_third_method2 = findViewById(R.id.iv_third_method2); // 第三方登录方式2
        iv_third_method3 = findViewById(R.id.iv_third_method3); // 第三方登录方式3
    }
    /*
    当输入账号FocusChange时，校验账号是否是中国大陆手机号
    当输入密码FocusChange时，校验密码是否不少于6位
     */
    private void setOnFocusChangeErrMsg(EditText editText,String inputType, String errMsg){
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        if (!hasFocus){
                            if(inputType == "phone"){
                                if (isTelphoneValid(inputStr)){
                                    editText.setError(null);
                                }else {
                                    editText.setError(errMsg);
                                }
                            }
                            if (inputType == "password"){
                                if (isPasswordValid(inputStr)){
                                    editText.setError(null);
                                }else {
                                    editText.setError(errMsg);
                                }
                            }
                        }
                    }
                }
        );
    }

    // 校验账号不能为空且必须是中国大陆手机号（宽松模式匹配）
    private boolean isTelphoneValid(String account) {
        if (account == null) {
            return false;
        }
        // 首位为1, 第二位为3-9, 剩下九位为 0-9, 共11位数字
        String pattern = "^[1]([3-9])[0-9]{9}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(account);
        return m.matches();
    }

    // 校验密码不少于6位
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }


    // 为点击事件的UI对象设置监听器
    private void setOnClickListener(){
        bt_login.setOnClickListener(this); // 登录按钮
        tv_register.setOnClickListener(this); // 注册文字
        tv_forget_password.setOnClickListener(this); // 忘记密码文字
        tv_service_agreement.setOnClickListener(this); // 同意协议文字
        iv_third_method1.setOnClickListener(this); // 第三方登录方式1
        iv_third_method2.setOnClickListener(this); // 第三方登录方式2
        iv_third_method3.setOnClickListener(this); // 第三方登录方式3
    }

    // 因为 implements View.OnClickListener 所以OnClick方法可以写到onCreate方法外
    @Override
    public void onClick(View v) {
        // 获取用户输入的账号和密码以进行验证
        String account = et_account.getText().toString();
        String password = et_password.getText().toString();

        switch (v.getId()){
            // 登录按钮 响应事件
            case R.id.bt_login:
                // 让密码输入框失去焦点,触发setOnFocusChangeErrMsg方法
                et_password.clearFocus();
                // 发送URL请求之前,先进行校验
                if (!(isTelphoneValid(account) && isPasswordValid(password))){
                    Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                /*
                   因为验证是耗时操作，所以独立成方法
                   在方法中开辟子线程，避免在当前UI线程进行耗时操作
                */
                asyncValidate(account,password);
                break;
            // 注册用户 响应事件
            case R.id.tv_register:
                /*
                  关于这里传参说明：给用户一个良好的体验，
                  如果在登录界面填写过的，就不需要再填了
                  所以Intent把填写过的数据传递给注册界面
                 */
                Intent it_login_to_register = new Intent(this, RegisterActivity.class);
                it_login_to_register.putExtra("account",account);
                startActivity(it_login_to_register);
                break;

            // 以下功能目前都没有实现
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
    /*
      okhttp异步POST请求 要求API level 21+
      account 本来想的是可以是 telphone或者username
      但目前只实现了telphone
     */
    private void asyncValidate(final String account, final String password){
        /*
         发送请求属于耗时操作，所以开辟子线程执行
         上面的参数都加上了final，否则无法传递到子线程中
        */
        new Thread( new Runnable() {
            @Override
            public void run() {
                // okhttp异步POST请求； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体requestBody
                final String telphone = account;  // 为了让键和值名字相同，我把account改成了telphone，没其他意思
                RequestBody requestBody = new FormBody.Builder()
                        .add("telphone", telphone)
                        .add("password", password)
                        .build();
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(loginURL)
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue( new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        showToastInThread(LoginActivity.this,"请求URL失败, 请重试！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 先判断一下服务器是否异常
                        String responseStr = response.toString();
                        if (responseStr.contains("404") || responseStr.contains("500")) {
                            Log.d(TAG,"服务器异常");
                            showToastInThread(LoginActivity.this, responseStr);
                        } else {
                             /*
                            注意这里，同一个方法内
                            response.body().string()只能调用一次，多次调用会报错
                             */
                            /* 使用Gson解析response的JSON数据的第一步 */
                            String responseBodyStr = response.body().string();
                            /* 使用Gson解析response的JSON数据的第二步 */
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            // 如果返回的status为success，则getStatus返回true，登录验证通过
                            if (getStatus(LoginActivity.this, responseBodyJSONObject)) {
                            /*
                             更新token，下次自动登录
                             真实的token值应该是一个加密字符串
                             我为了让token不为null，就随便传了一个字符串
                            */
                                editor = sp.edit();
                                editor.putString("token", "token_value");
                                editor.putString("telphone", telphone);
                                if (editor.commit()) {
                                    Intent it_login_to_main = new Intent(LoginActivity.this, MainActivity.class);
                                    it_login_to_main.putExtra("telphone", telphone);
                                    startActivity(it_login_to_main);
                                    // 登录成功后，登录界面就没必要占据资源了
                                    finish();
                                } else {
                                    showToastInThread(LoginActivity.this, "token保存失败，请重新登录");
                                }
                            } else {
                                getResponseData(LoginActivity.this, responseBodyJSONObject);
                                Log.d(TAG, "账号或密码验证失败");
                            }
                        }
                    }
                });

            }
        }).start();
    }

    /*
      使用Gson解析response的JSON数据
      本来总共是有三步的，一、二步在方法调用之前执行了
    */
    private boolean getStatus(Context context, JsonObject responseBodyJSONObject)throws IOException {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status.equals("success");
    }

    /*
      使用Gson解析response返回异常信息的JSON中的data对象
      这也属于第三步，一、二步在方法调用之前执行了
     */
    private void getResponseData(Context context, JsonObject responseBodyJSONObject){
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG,"errorCode: "+errorCode+" errorMsg: "+errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context,errorMsg);
    }

    // 实现在子线程中显示Toast
    private void showToastInThread(Context context,String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }












    // 以下是我尝试过的一些失败的方法
    // 我就是保留做个纪念，大家可以删除

    /*
        在子线程中显示Toast，下面方法虽然也能，但如果使用它，就必须在runOnUiThread()方法之后再使用
        否则runOnUiThread()方法将不会起作用
        目前还不清楚原理
     */
    /*
    private void showToastInThread(Context context,String msg){
        Looper.prepare();
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Looper.loop();
    }
    */


    /*
        Ion异步发送POST请求
        请求可以发送成功，但是参数会丢失
        目前不清楚原理
     */
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
        xUtils发送POST请求
        这个需要设置的参数有点多
        而且，XUtils这个框架太过庞大
        容易牵一发而动全身
        不如okhttp只专注于一件事
        而且这个框架低版本需要手动引入jar包，不是很方便
        故放弃此框架
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
