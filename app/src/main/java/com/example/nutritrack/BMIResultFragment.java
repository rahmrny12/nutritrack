package com.example.nutritrack;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.nutritrack.data.model.HealthResponse;
import com.example.nutritrack.data.service.HealthApiService;
import com.example.nutritrack.data.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BMIResultFragment extends Fragment {

    private String userId;

    private TextView txtBMI, txtCategory;

    private Button backBtn, confirmBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bmi_result, container, false);

        txtBMI = view.findViewById(R.id.bmiValueTextView);
        txtCategory = view.findViewById(R.id.bmiCategoryTextView);
        backBtn = view.findViewById(R.id.backBtn);
        confirmBtn = view.findViewById(R.id.confirmBtn);

        confirmBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish(); // optional: prevents going back to fragment
        });


        backBtn.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });


        userId = getArguments().getString("user_id");

        fetchLatestHealth();

        return view;
    }

    private void animateBMI(double bmiValue) {

        ValueAnimator animator = ValueAnimator.ofFloat(0f, (float) bmiValue);
        animator.setDuration(1500); // 1.5 seconds
        animator.setInterpolator(new DecelerateInterpolator()); // smooth slowdown

        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            txtBMI.setText(String.format("%.2f", animatedValue));
        });

        animator.start();
    }


    private void fetchLatestHealth() {

        HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);

        api.getHealth(userId).enqueue(new Callback<HealthResponse>() {
            @Override
            public void onResponse(Call<HealthResponse> call, Response<HealthResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Failed loading data", Toast.LENGTH_SHORT).show();
                    return;
                }

                HealthResponse.User user = response.body().data.user;

                animateBMI(user.bmi);
                txtCategory.setText(user.bmiCategory);
            }

            @Override
            public void onFailure(Call<HealthResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
