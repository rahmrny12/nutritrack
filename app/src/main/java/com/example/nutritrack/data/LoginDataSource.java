package com.example.nutritrack.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nutritrack.data.model.LoggedInUser;
import com.example.nutritrack.data.model.LoginResponse;
import com.example.nutritrack.data.service.RetrofitClient;
import com.example.nutritrack.data.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final Context context;

    public LoginDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Login menggunakan Firebase Authentication (cara umum)
     * Hasil dikembalikan secara async melalui callback agar tidak blocking main thread.
     */
    public void login(String email, String password, LoginCallback callback) {

        UserService api = RetrofitClient.getInstance().create(UserService.class);

        UserService.LoginRequest req = new UserService.LoginRequest(email, password);

        api.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    callback.onResult(new Result.Error(new IOException("Pengguna tidak ditemukan")));
                    return;
                }

                LoginResponse res = response.body();

                if (!"success".equals(res.status)) {
                    callback.onResult(new Result.Error(new IOException("Login gagal: " + res.message)));
                }

                // Convert ke LoggedInUser kamu
                LoggedInUser user = new LoggedInUser(
                        String.valueOf(res.data.id),
                        res.data.fullname
                );

                callback.onResult(new Result.Success<>(user));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onResult(new Result.Error(new IOException("Network error: " + t.getMessage())));
            }
        });
    }


    /**
     * Callback untuk mengembalikan hasil login
     */
    public interface LoginCallback {
        void onResult(Result<LoggedInUser> result);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
