package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.WeeklyCaloriesResponse;
import com.example.nutritrack.data.model.WeeklyMacrosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AnalyticsApiService {

    @GET("analytics.php?action=weekly_calories")
    Call<WeeklyCaloriesResponse> getWeeklyCalories(
            @Query("userId") String userId,
            @Query("date") String date
    );

    @GET("analytics.php?action=weekly_macros")
    Call<WeeklyMacrosResponse> getWeeklyMacros(
            @Query("userId") String userId
    );


}
