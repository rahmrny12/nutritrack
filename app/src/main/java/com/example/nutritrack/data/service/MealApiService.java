package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.MealModel;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MealApiService {

    @GET("meals.php")
    Call<List<MealModel>> getMeals();

    @POST("meals.php")
    Call<ResponseBody> createMeal(@Body MealModel meal);
}
