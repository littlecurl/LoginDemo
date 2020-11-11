package cn.edu.heuet.login.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.edu.heuet.login.R;
import cn.edu.heuet.login.activity.NewsDetailActivity;
import cn.edu.heuet.login.bean.News;

/**
 * @ClassName SearchAdapter
 * @Author littlecurl
 * @Date 2020/6/16 22:08
 * @Version 1.0.0
 * @Description 实现搜索结果的列表展示
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private List<News> newsList;

    /**
     * 接收参数
     *
     * @param newsList 新闻列表
     */
    public SearchAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new MyViewHolder(view);
    }
    /**
     * 创建 ViewHolder
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivPicture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivPicture = itemView.findViewById(R.id.iv_picture);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        News news = newsList.get(position);

        String title = news.getTitle();
        String picture = news.getPicture();
        // 设置 Title 、 Picture
        holder.tvTitle.setText(title);

        Glide.with(holder.itemView.getContext())
                .load(picture)
                .into(holder.ivPicture);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewsDetailActivity.class);
                intent.putExtra("news", news);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
