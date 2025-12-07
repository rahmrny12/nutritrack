package com.example.nutritrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.data.model.ApiResponse;
import com.example.nutritrack.data.model.RegisterRequest;
import com.example.nutritrack.data.service.RetrofitClient;
import com.example.nutritrack.data.service.UserService;
import com.example.nutritrack.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, address, password, confirmPassword;
    private Button registerBtn;
    private ProgressBar loading;
    TextView btnToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).hide();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerBtn = findViewById(R.id.registerBtn);
        loading = findViewById(R.id.loading);
        btnToLogin = findViewById(R.id.btnToLogin);

        btnToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        registerBtn.setEnabled(true);

        registerBtn.setOnClickListener(v -> {

            String fullname = name.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String addressInput = address.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmInput = confirmPassword.getText().toString().trim();

            if (fullname.isEmpty() || emailInput.isEmpty() || addressInput.isEmpty() ||
                    passwordInput.isEmpty() || confirmInput.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordInput.equals(confirmInput)) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterRequest body = new RegisterRequest(
                    fullname,
                    emailInput,
                    passwordInput
            );

            UserService api = RetrofitClient.getInstance().create(UserService.class);

            api.register(body).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call,
                                       Response<ApiResponse> response) {

                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(RegisterActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ApiResponse res = response.body();

                    if (res.status.equals("success")) {

                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("email", emailInput);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, res.message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });


    }
}
