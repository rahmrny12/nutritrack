package com.example.nutritrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.ui.login.LoginActivity;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, username, address, password, confirmPassword;
    private Button registerBtn;
    private ProgressBar loading;
    TextView btnToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).hide();

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
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
            String nameInput = name.getText().toString().trim();
            String usernameInput = username.getText().toString().trim();
            String addressInput = address.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmInput = confirmPassword.getText().toString().trim();

            if (nameInput.isEmpty() || usernameInput.isEmpty() || addressInput.isEmpty() || passwordInput.isEmpty() || confirmInput.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordInput.equals(confirmInput)) {
                Toast.makeText(RegisterActivity.this, "Password tidak sama", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simpan ke SharedPreferences
            SharedPreferences sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("name", nameInput);
            editor.putString("username", usernameInput);
            editor.putString("address", addressInput);
            editor.putString("password", passwordInput);
            editor.apply();

            Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();

            // Pindah ke LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
