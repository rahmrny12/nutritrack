package com.example.nutritrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

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
            String nameInput = name.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String addressInput = address.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmInput = confirmPassword.getText().toString().trim();

            if (nameInput.isEmpty() || emailInput.isEmpty() || addressInput.isEmpty() ||
                    passwordInput.isEmpty() || confirmInput.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordInput.equals(confirmInput)) {
                Toast.makeText(RegisterActivity.this, "Password tidak sama", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // ✅ Registration success
                            String userId = mAuth.getCurrentUser().getUid();

                            DatabaseReference userRef = database.getReference("users").child(userId);
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", nameInput);
                            userMap.put("email", emailInput);
                            userMap.put("address", addressInput);
                            userMap.put("password", passwordInput);

                            userRef.setValue(userMap)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.putExtra("email", emailInput);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                // ⚠️ Email already registered
                                Toast.makeText(RegisterActivity.this, "Email sudah terdaftar, silakan login.", Toast.LENGTH_SHORT).show();

                                // Kirim email ke LoginActivity agar autofill
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("email", emailInput);
                                startActivity(intent);
                                finish();

                            } else if (e instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(RegisterActivity.this, "Password terlalu lemah, gunakan minimal 6 karakter.", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(RegisterActivity.this, "Format email tidak valid.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

    }
}
