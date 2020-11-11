package cn.edu.heuet.login.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.bean.News;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView ivPicture;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        News news = (News) intent.getSerializableExtra("news");

        initUI(news);

    }

    private void initUI(News news) {
        ivPicture = findViewById(R.id.iv_picture);
        tvContent = findViewById(R.id.tv_content);
        if (news == null) {
            return;
        }
        Glide.with(this).load(news.getPicture()).into(ivPicture);
        tvContent.setText(news.getTitle() + "\n" + news.getContent());
    }
}
