package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.ArticleModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ArticleApiService {

    @GET("articles.php")
    Call<List<ArticleModel>> getArticles();

}
