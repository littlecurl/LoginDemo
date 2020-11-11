package cn.edu.heuet.login.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.widget.AppCompatTextView;

import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import java.io.IOException;

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.constant.ModelConstant;
import cn.edu.heuet.login.constant.NetConstant;
import cn.edu.heuet.login.util.SharedPreferencesUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CountDownActivity extends BaseActivity {
    // 右上角的文字控件
    private AppCompatTextView countDownText;
    private CountDownTimer timer;

    final static String TAG = "CountDownActivity";
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fullScreenConfig();

        // 上面全屏的代码一定要写在setContentView之前
        setContentView(R.layout.activity_count_down);

        autoLogin();

        countDownText = findViewById(R.id.tv_count_down);
        countDownText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkToJump();
            }
        });

        initCountDown();
    }

    private void autoLogin() {
        SharedPreferences sp = getSharedPreferences(ModelConstant.LOGIN_INFO, MODE_PRIVATE);
        String telephoneInSP = sp.getString("telephone", "");
        String passwordInSP = sp.getString("encryptedPassword", "");
        // 异步登录
        // asyncValidate(telephoneInSP, passwordInSP);
        asyncValidateWithXHttp2(telephoneInSP, passwordInSP);
    }


    // 倒计时逻辑
    private void initCountDown() {
        // 倒计时总时长，倒计时间隔
        timer = new CountDownTimer(1000 * 6, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                // 因为点击跳过会直接 finish(),所以检测一下
                if (!isFinishing()) {
                    countDownText.setText(millisUntilFinished / 1000 + " 跳过");
                }
            }

            @Override
            public void onFinish() {
                checkToJump();
            }
        }.start();
    }


    // 首次打开程序判断
    private void checkToJump() {
        boolean isFirstLogin = SharedPreferencesUtils.getBoolean(CountDownActivity.this, ModelConstant.FIRST_LOGIN, true);
        // 首次打开，进入引导页
        if (isFirstLogin) {
            Intent it_to_guide = new Intent(CountDownActivity.this, GuideActivity.class);
            startActivity(it_to_guide);
            SharedPreferencesUtils.putBoolean(CountDownActivity.this, ModelConstant.FIRST_LOGIN, false);
        }
        // 非首次打开，登录回调判断
        else {
            if (isLogin) {
                startActivity(new Intent(CountDownActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(CountDownActivity.this, LoginActivity.class));
            }
        }
        // 回收内存
        destroyTimer();
        finish();
    }

    public void destroyTimer() {
        // 避免内存泄漏
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // 异步登录
    private void asyncValidateWithXHttp2(String account, String password) {
        XHttp.post(NetConstant.getLoginURL())
                .params("telephone", account)
                .params("password", password)
                .params("type", "autoLogin")
                .syncRequest(false)
                .execute(new SimpleCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) throws Throwable {
                        isLogin = true;
                        Log.d(TAG, "请求URL成功,自动登录成功");
                    }

                    @Override
                    public void onError(ApiException e) {
                        Log.d(TAG, "请求URL异常,自动登录失败" + e.toString());
//                        showToastInThread(CountDownActivity.this, e.getMessage());
                    }
                });
    }

    private void asyncValidate(final String account, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                final String telephone = account;  // 为了让键和值名字相同，我把account改成了telephone，没其他意思
                RequestBody requestBody = new FormBody.Builder()
                        .add("telephone", telephone)
                        .add("password", password)
                        .add("type", "autoLogin")
                        .build();
                Request request = new Request.Builder()
                        .url(NetConstant.getLoginURL())
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            if (getStatus(responseBodyJSONObject).equals("success")) {
                                isLogin = true;
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                        }
                    }
                });
            }
        }).start();
    }

    private String getStatus(JsonObject responseBodyJSONObject) {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status;
    }
}
