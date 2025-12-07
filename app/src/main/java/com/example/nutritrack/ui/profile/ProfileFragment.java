package com.example.nutritrack.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritrack.data.model.HealthResponse;
import com.example.nutritrack.data.service.HealthApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.nutritrack.ForgotPasswordActivity;
import com.example.nutritrack.R;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.ui.EditProfileActivity;
import com.example.nutritrack.ui.home.HomeFragment;
import com.example.nutritrack.ui.login.LoginActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvGender, tvCalorie, tvProtein, tvFat, tvCarbs;
    private TextView tvHeight, tvBMI, tvWeight, tvTarget;
    private Button btnLogout;
    private Button btnEditProfile;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        UserPreferences userPref = new UserPreferences(requireContext());

        // Bind views
        tvName = view.findViewById(R.id.tvName);
        tvGender = view.findViewById(R.id.tvGender);
        tvCalorie = view.findViewById(R.id.tvCalorie);
        tvProtein = view.findViewById(R.id.tvProtein);
        tvFat = view.findViewById(R.id.tvFat);
        tvCarbs = view.findViewById(R.id.tvCarbs);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvBMI = view.findViewById(R.id.tvBMI);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvTarget = view.findViewById(R.id.tvTarget);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(() -> {
            fetchUserHealthData(userPref.getUserId(), requireContext());
        });


        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });


        // GET DATA FROM API AGAIN (RECOMMENDED)
        fetchUserHealthData(userPref.getUserId(), view.getContext());

        // Logout
        btnLogout.setOnClickListener(v -> {
            userPref.clear();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }


    // ============================================
    // FETCH USER DATA (Height, Weight, BMI, etc.)
    // ============================================
    private void fetchUserHealthData(String userId, Context context) {

        HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);

        api.getHealth(userId).enqueue(new Callback<HealthResponse>() {
            @Override
            public void onResponse(Call<HealthResponse> call, Response<HealthResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(context, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                    return;
                }

                HealthResponse.User user = response.body().data.user;

                // Update UI
                tvName.setText(user.fullname);
                String genderText;

                if (user.gender != null) {
                    if (user.gender.equalsIgnoreCase("male")) {
                        genderText = "Laki-laki";
                    } else if (user.gender.equalsIgnoreCase("female")) {
                        genderText = "Perempuan";
                    } else {
                        genderText = user.gender; // fallback
                    }
                } else {
                    genderText = "-";
                }

                tvGender.setText(genderText);

                tvCalorie.setText(user.dailyCaloriesTarget + " kcal");

                // OPTIONAL: you can calculate protein/ fat / carbs dynamically:
                tvProtein.setText(((int) (user.dailyCaloriesTarget * 0.15 / 4)) + " g");
                tvFat.setText(((int) (user.dailyCaloriesTarget * 0.25 / 9)) + " g");
                tvCarbs.setText(((int) (user.dailyCaloriesTarget * 0.60 / 4)) + " g");

                tvHeight.setText(user.height + " cm");
                tvWeight.setText(user.weight + " kg");
                tvBMI.setText(user.bmi + " (" + user.bmiCategory + ")");
                tvTarget.setText(user.dailyCaloriesTarget + " kcal"); // or weight target if available

                if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<HealthResponse> call, Throwable t) {
                Toast.makeText(context, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();

                if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });

    }
}
