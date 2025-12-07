package com.example.nutritrack.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.UpdateProfileResponse;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.RetrofitClient;
import com.example.nutritrack.data.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    EditText inputFullname, inputEmail, inputHeight, inputWeight;
    Button btnSave, btnCancel;

    String userId;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        inputFullname = findViewById(R.id.inputFullname);
        inputEmail = findViewById(R.id.inputEmail);
        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        loading = new ProgressDialog(this);
        loading.setMessage("Memproses...");
        loading.setCancelable(false);

        userId = new UserPreferences(this).getUserId();

        // 🔥 LOAD DEFAULT PROFILE
        loadUserProfile();

        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> finish());
    }

    // ============================================================
    // 1. MUAT PROFIL DEFAULT DARI SERVER
    // ============================================================
    private void loadUserProfile() {
        loading.show();

        UserService api = RetrofitClient.getInstance().create(UserService.class);

        api.getProfile(userId).enqueue(new Callback<UserService.UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserService.UserProfileResponse> call, Response<UserService.UserProfileResponse> response) {
                loading.dismiss();

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(EditProfileActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserService.UserProfileResponse.Data user = response.body().data;

                // 🔥 Set default values
                inputFullname.setText(user.fullname);
                inputEmail.setText(user.email);

                inputHeight.setText(user.height == 0 ? "" : String.valueOf(user.height));
                inputWeight.setText(user.weight == 0 ? "" : String.valueOf(user.weight));
            }

            @Override
            public void onFailure(Call<UserService.UserProfileResponse> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ============================================================
    // 2. SIMPAN PERUBAHAN PROFIL
    // ============================================================
    private void saveProfile() {

        String fullname = inputFullname.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String heightStr = inputHeight.getText().toString().trim();
        String weightStr = inputWeight.getText().toString().trim();

        if (fullname.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nama dan email wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        loading.show();

        UserService api = RetrofitClient.getInstance().create(UserService.class);

        UserService.UpdateProfileRequest req =
                new UserService.UpdateProfileRequest(
                        userId,
                        fullname,
                        email,
                        heightStr,
                        weightStr
                );

        api.updateProfile(req).enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                loading.dismiss();

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(EditProfileActivity.this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(EditProfileActivity.this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
