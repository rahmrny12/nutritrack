package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.DailyGoalResponse;
import com.example.nutritrack.data.model.UpdateUserHealthResponse;
import com.example.nutritrack.data.model.HealthResponse;
import com.example.nutritrack.data.model.ProgressResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HealthApiService {
    @GET("auth.php?action=get_health")
    Call<HealthResponse> getHealth(
            @Query("user_id") String userId
    );

    @POST("auth.php?action=update_health")
    Call<UpdateUserHealthResponse> updateHealth(@Body HashMap<String, String> body);

    @GET("daily_goals.php")
    Call<DailyGoalResponse> getDailyGoal(
            @Query("userId") String userId,
            @Query("date") String date
    );

    @GET("auth.php?action=get_progress")
    Call<ProgressResponse> getProgress(
            @Query("id") String userId
    );

}
