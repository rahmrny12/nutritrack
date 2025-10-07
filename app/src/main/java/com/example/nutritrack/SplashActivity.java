package com.example.nutritrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.databinding.ActivitySplashBinding;
import com.example.nutritrack.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Animasi fade-in
        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);
        binding.appLogo.startAnimation(fadeIn);
        binding.appSubtitle.startAnimation(fadeIn);

        // Delay 3 detik, lalu cek login
        new Handler().postDelayed(() -> {
            SharedPreferences sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE);
            boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

            Intent intent;
            if (isLoggedIn) {
                // Langsung ke MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Ke halaman login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 3000);
    }
}
