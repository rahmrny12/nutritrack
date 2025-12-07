package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.ApiResponse;
import com.example.nutritrack.data.model.LoginResponse;
import com.example.nutritrack.data.model.RegisterRequest;
import com.example.nutritrack.data.model.UpdateProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {

    @POST("auth.php?action=register")
    Call<ApiResponse> register(@Body RegisterRequest body);

    @POST("auth.php?action=login")
    Call<LoginResponse> login(@Body LoginRequest request);

    class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class UpdateProfileRequest {
        public String id;
        public String fullname;
        public String email;
        public String height;
        public String weight;

        public UpdateProfileRequest(String id, String fullname, String email, String height, String weight) {
            this.id = id;
            this.fullname = fullname;
            this.email = email;
            this.height = height;
            this.weight = weight;
        }
    }


    @POST("auth.php?action=update_health")
    Call<UpdateProfileResponse> updateProfile(
            @Body UpdateProfileRequest request
    );

    public class UserProfileResponse {
        public String status;
        public Data data;
        public String message;

        public static class Data {
            public int id;
            public String fullname;
            public String email;
            public String gender;
            public int height;
            public int weight;
            public int age;
            public int daily_calories_target;
        }
    }


    @GET("auth.php?action=profile")
    Call<UserProfileResponse> getProfile(@Query("id") String userId);


}
