package com.example.nutritrack.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.data.model.DailyGoalResponse;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.HealthApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.example.nutritrack.databinding.ActivityDiaryLagiBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryActivity extends AppCompatActivity {

    private ActivityDiaryLagiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryLagiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        loadDailyGoalFromPrefs();
        fetchDailyGoalFromApi();
    }

    /* ------------------------------------------------------------
       1️⃣ Load local goal first
       ------------------------------------------------------------ */
    private void loadDailyGoalFromPrefs() {

        UserPreferences prefs = new UserPreferences(this);

        String date = prefs.getSavedDailyGoalDate();
        if (date == null) return;

        updateUI(
                prefs.getSavedDailyGoalCalorie(),
                prefs.getSavedDailyGoalProtein(),
                prefs.getSavedDailyGoalCarbs(),
                prefs.getSavedDailyGoalFat(),
                0, 0, 0, 0
        );
    }

    /* ------------------------------------------------------------
       2️⃣ Remote fetch
       ------------------------------------------------------------ */
    private void fetchDailyGoalFromApi() {

        UserPreferences prefs = new UserPreferences(this);
        String userId = prefs.getUserId();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);

        api.getDailyGoal(userId, today).enqueue(new Callback<DailyGoalResponse>() {
            @Override
            public void onResponse(Call<DailyGoalResponse> call, Response<DailyGoalResponse> response) {

                if (!response.isSuccessful() || response.body() == null || response.body().data == null) {
                    return;
                }

                DailyGoalResponse.Data d = response.body().data;

                prefs.saveDailyGoals(
                        d.tanggal,
                        d.goal.calorieGoal,
                        d.goal.proteinGoal,
                        d.goal.carbsGoal,
                        d.goal.fatGoal,
                        "{}"
                );

                updateUI(
                        d.goal.calorieGoal,
                        d.goal.proteinGoal,
                        d.goal.carbsGoal,
                        d.goal.fatGoal,
                        d.consumed.calories,
                        d.consumed.protein,
                        d.consumed.carbs,
                        d.consumed.fat
                );
            }

            @Override
            public void onFailure(Call<DailyGoalResponse> call, Throwable t) {
                Log.e("DAILY_GOALS_API", t.getMessage());
            }
        });
    }

    /* ------------------------------------------------------------
       3️⃣ Update UI
       ------------------------------------------------------------ */
    private void updateUI(
            int calorieGoal, int proteinGoal, int carbsGoal, int fatGoal,
            int consumedCal, int consumedProtein, int consumedCarbs, int consumedFat
    ) {

        int remaining = Math.max(calorieGoal - consumedCal, 0);

        binding.tvNeedToGain.setText("Still need to gain");
        binding.tvCaloriesRemaining.setText(remaining + " kcal");

        // Protein
        binding.circleProtein.setProgress(consumedProtein);
        binding.circleProtein.setMaxProgress(proteinGoal);

        // Fat
//        binding.circleFat.tvPercent.setText(consumedFat + "%");
//        binding.circleFat.setProgress(consumedFat);
//        binding.circleFat.setMaxProgress(fatGoal);
//
//        // Carbs
//        binding.circleCarbs.tvPercent.setText(consumedCarbs + "%");
//        binding.circleCarbs.setProgress(consumedCarbs);
//        binding.circleCarbs.setMaxProgress(carbsGoal);
    }

}
