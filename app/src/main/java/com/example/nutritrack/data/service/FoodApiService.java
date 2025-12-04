package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.FoodModel;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface FoodApiService {

    @GET("foods.php")
    Call<List<FoodModel>> getFoods();
}
