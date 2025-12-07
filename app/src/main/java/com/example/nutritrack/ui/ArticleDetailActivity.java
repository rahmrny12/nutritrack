package com.example.nutritrack.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nutritrack.R;
import com.example.nutritrack.data.model.ArticleModel;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Get article from intent
        ArticleModel article = (ArticleModel) getIntent().getSerializableExtra("article");
        if (article == null) {
            finish();
            return;
        }

        // Views
        LinearLayout btnBack = findViewById(R.id.btnBack);
        ImageView imgCover = findViewById(R.id.imgCover);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvContent = findViewById(R.id.tvContent);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Set data
        tvTitle.setText(article.title);
        tvCategory.setText("Health");
        tvDate.setText(article.createdAt);
        tvContent.setText(article.content);

        // If you have image URL, load via Glide
        if (article.imageUrl != null && !article.imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(article.imageUrl)
                    .into(imgCover);
        } else {
            imgCover.setVisibility(View.GONE); // ← hide the photo
        }
    }
}
