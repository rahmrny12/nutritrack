package com.example.nutritrack.ui.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutritrack.InitialProfileActivity;
import com.example.nutritrack.RegisterActivity;
import com.example.nutritrack.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    private Dialog loadingDialog;   // 🔹 loading popup
    private Dialog errorDialog;     // 🔹 dialog error + retry

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(getApplicationContext()))
                .get(LoginViewModel.class);

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading; // sudah tidak dipakai, boleh dihapus dari layout nantinya
        final TextView btnToRegister = binding.btnToRegister;

        // Prefill email dari register
        String emailFromRegister = getIntent().getStringExtra("email");
        if (emailFromRegister != null) {
            emailEditText.setText(emailFromRegister);
        }

        btnToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Observing form validation
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getEmailError() != null) {
                    emailEditText.setError(getString(loginFormState.getEmailError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        // Observing login result
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) return;

                hideLoadingDialog();

                // 1️⃣ ERROR — STRING dari API
                if (loginResult.getErrorMessage() != null) {
                    String rawError = loginResult.getErrorMessage();
                    String customMessage = rawError;

                    // Detect network error based on number or wording
                    if (rawError.contains("2132017255") ||
                            rawError.contains("Unable to resolve host") ||
                            rawError.contains("No address associated")) {

                        customMessage = "Tidak dapat terhubung ke server.\nPeriksa koneksi internet Anda.";
                    }

                    showRetryDialog(
                            customMessage,
                            () -> doLogin(emailEditText.getText().toString(),
                                    passwordEditText.getText().toString())
                    );
                }

                // 2️⃣ ERROR — Resource ID (misal error bawaan)
                else if (loginResult.getError() != null) {
                    Integer error = loginResult.getError();
                    showRetryDialog(
                            getString(loginResult.getError()),
                            () -> doLogin(emailEditText.getText().toString(),
                                    passwordEditText.getText().toString())
                    );
                }

                // 3️⃣ SUCCESS LOGIN
                else if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString()
                );
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doLogin(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    /* ============================================================
       LOGIN ACTION (dipanggil dari tombol & retry)
       ============================================================ */
    private void doLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog();
        loginViewModel.login(email, password);
    }

    /* ============================================================
       LOADING DIALOG
       ============================================================ */
    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new Dialog(this);
            loadingDialog.setContentView(com.example.nutritrack.R.layout.dialog_loading);
            loadingDialog.setCancelable(false);
            if (loadingDialog.getWindow() != null) {
                loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                loadingDialog.getWindow().setDimAmount(0.3f);
            }
        }
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /* ============================================================
       ERROR DIALOG DENGAN RETRY
       ============================================================ */
    private void showRetryDialog(String message, Runnable retryAction) {
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }

        errorDialog = new Dialog(this);
        errorDialog.setContentView(com.example.nutritrack.R.layout.dialog_error_retry);
        errorDialog.setCancelable(true);
        if (errorDialog.getWindow() != null) {
            errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            errorDialog.getWindow().setDimAmount(0.3f);
        }

        TextView errorText = errorDialog.findViewById(com.example.nutritrack.R.id.errorText);
        Button btnRetry = errorDialog.findViewById(com.example.nutritrack.R.id.btnRetry);
        Button btnCancel = errorDialog.findViewById(com.example.nutritrack.R.id.btnCancel);

        if (errorText != null) {
            errorText.setText(message);
        }

        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                errorDialog.dismiss();
                retryAction.run();
            });
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> errorDialog.dismiss());
        }

        errorDialog.show();
    }

    /* ============================================================
       UI UPDATE KETIKA LOGIN SUKSES
       ============================================================ */
    private void updateUiWithUser(LoggedInUserView model) {
        // Simpan status login di SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        // Arahkan ke halaman utama (InitialProfileActivity)
        Intent intent = new Intent(this, InitialProfileActivity.class);
        startActivity(intent);
        finish(); // Supaya tidak bisa back ke login lagi
    }

    // Masih dipakai kalau kamu mau tetap pakai Toast biasa di tempat lain
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
