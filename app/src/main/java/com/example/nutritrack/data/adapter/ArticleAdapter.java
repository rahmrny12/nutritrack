package com.example.nutritrack.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.ArticleModel;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private Context context;
    private List<ArticleModel> articles;

    public ArticleAdapter(Context context, List<ArticleModel> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ArticleModel a = articles.get(pos);

        h.tvTitle.setText(a.title);
        h.tvDesc.setText(a.content.length() > 100 ? a.content.substring(0, 100) + "..." : a.content);
        h.tvCategory.setText("Health");
        h.tvDate.setText(a.createdAt);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvCategory, tvDate;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvTitle    = v.findViewById(R.id.tvTitle);
            tvDesc     = v.findViewById(R.id.tvDesc);
            tvCategory = v.findViewById(R.id.tvCategory);
            tvDate     = v.findViewById(R.id.tvDate);
        }
    }
}
