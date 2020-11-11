package cn.edu.heuet.login.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.loader.ModelImageLoader;


public class GuideActivity extends BaseActivity {

    Banner banner;
    Button bt_start;
    private List<Integer> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fullScreenConfig();

        setContentView(R.layout.activity_guide);

        initUI();

        initBannerData();

        initViews();
    }

    private void initUI() {
        banner = findViewById(R.id.banner);
        bt_start = findViewById(R.id.bt_start);
    }

    // 初始化banner数据
    private void initBannerData() {
        imageList = new ArrayList<>();
        imageList.add(R.drawable.launcher_01);
        imageList.add(R.drawable.launcher_02);
        imageList.add(R.drawable.launcher_03);
        imageList.add(R.drawable.launcher_04);
    }

    private void initViews() {
        banner.setImageLoader(new ModelImageLoader())
                .setImages(imageList)
                .isAutoPlay(false)
                .start();


        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == imageList.size() - 1) {
                    bt_start.setVisibility(View.VISIBLE);
                    bt_start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                } else {
                    bt_start.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }
}
