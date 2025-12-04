package com.example.nutritrack.data.service;

import com.example.nutritrack.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService { 

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
}
