package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.DiaryModel;
import com.example.nutritrack.data.model.MonthlySummaryResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DiaryApiService {

    @POST("diary.php")
    Call<DiaryApiService.ApiResponse> createDiary(@Body DiaryModel diary);

    class ApiResponse {
        public String status;
        public Object data;
        public String message;
    }

    @GET("diary.php")
    Call<DiaryResponse> getDiaryByDate(
            @Query("id_user") int userId,
            @Query("date") String date   // format: yyyy-MM-dd
    );


    class DiaryResponse {
        public String status;
        public List<DiaryModel> data;
    }

    @GET("diary.php")
    Call<MonthlySummaryResponse> getMonthlySummary(
            @Query("monthlySummary") int summary,
            @Query("userId") String userId,
            @Query("month") int month,
            @Query("year") int year
    );


}
