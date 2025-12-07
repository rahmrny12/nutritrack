package com.example.nutritrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.data.model.DailyGoalResponse;
import com.example.nutritrack.data.model.HealthResponse;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.HealthApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.example.nutritrack.databinding.ActivitySplashBinding;
import com.example.nutritrack.ui.login.LoginActivity;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Simple animation saja
        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        binding.appLogo.startAnimation(fadeIn);
        binding.appSubtitle.startAnimation(fadeIn);

        // ⛔ TIDAK ADA DELAY
        checkLoginAndHealth();
    }

    private void checkLoginAndHealth() {

        UserPreferences prefs = new UserPreferences(this);
        String userId = prefs.getUserId();

        if (userId == null || userId.isEmpty()) {
            goTo(LoginActivity.class);
            return;
        }

        Log.d("SPLASH_FLOW", "Request GET Health…");

        HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);

        api.getHealth(userId).enqueue(new Callback<HealthResponse>() {
            @Override
            public void onResponse(Call<HealthResponse> call, Response<HealthResponse> response) {

                Log.d("SPLASH_FLOW", "GET Health → onResponse()");
                Log.d("SPLASH_FLOW", "HTTP CODE: " + response.code());

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("SPLASH_FLOW", "GET Health FAILED");
                    goTo(LoginActivity.class);
                    return;
                }

                HealthResponse res = response.body();
                prefs.setHealthComplete(res.data.isComplete);

                Log.d("SPLASH_FLOW", "Health Complete = " + res.data.isComplete);

                fetchDailyGoals(userId, res.data.isComplete);
            }

            @Override
            public void onFailure(Call<HealthResponse> call, Throwable t) {
                Log.e("SPLASH_FLOW", "GET Health → onFailure(): " + t.getMessage());
                goTo(LoginActivity.class);
            }
        });
    }

        private void fetchDailyGoals(String userId, boolean isHealthComplete) {

            UserPreferences prefs = new UserPreferences(this);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            String savedDate = prefs.getSavedDailyGoalDate();
            String savedRec = prefs.getRecommendationJson();

            // ⭐ SKIP API IF RECOMMENDATION IS ALREADY SAVED FOR TODAY
            if (savedDate != null
                    && savedDate.equals(today)
                    && savedRec != null
                    && !savedRec.trim().isEmpty()
                    && !savedRec.equals("null")) {

                Log.d("DAILY_GOAL_DEBUG", "Recommendation for today already exists → SKIP API");
                navigateAfterLoad(isHealthComplete);
                return;
            }

            Log.d("SPLASH_FLOW", "Request GET DailyGoal… (user=" + userId + ", date=" + today + ")");

            HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);

            api.getDailyGoal(userId, today).enqueue(new Callback<DailyGoalResponse>() {
                @Override
                public void onResponse(Call<DailyGoalResponse> call, Response<DailyGoalResponse> response) {

                    Log.d("DAILY_GOAL_DEBUG", "---- onResponse Triggered ----");
                    Log.d("DAILY_GOAL_DEBUG", "HTTP CODE: " + response.code());

                    if (!response.isSuccessful() || response.body() == null) {
                        navigateAfterLoad(isHealthComplete);
                        return;
                    }

                    DailyGoalResponse.Data d = response.body().data;
                    if (d == null) {
                        navigateAfterLoad(isHealthComplete);
                        return;
                    }

                    String recJson = (d.recommendation != null)
                            ? new Gson().toJson(d.recommendation)
                            : null;

                    prefs.saveDailyGoals(
                            d.tanggal,
                            d.goal.calorieGoal,
                            d.goal.proteinGoal,
                            d.goal.carbsGoal,
                            d.goal.fatGoal,
                            recJson
                    );

                    navigateAfterLoad(isHealthComplete);
                }

                @Override
                public void onFailure(Call<DailyGoalResponse> call, Throwable t) {
                    navigateAfterLoad(isHealthComplete);
                }
            });
        }

    private void navigateAfterLoad(boolean isHealthComplete) {
        Log.d("SPLASH_FLOW", "Navigating… isHealthComplete=" + isHealthComplete);

        if (isHealthComplete) goTo(MainActivity.class);
        else goTo(InitialProfileActivity.class);
    }

    private void goTo(Class<?> target) {
        startActivity(new Intent(SplashActivity.this, target));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();  // Activity dihancurkan SETELAH request selesai
    }
}
