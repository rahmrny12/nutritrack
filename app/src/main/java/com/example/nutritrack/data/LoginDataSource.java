package com.example.nutritrack.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nutritrack.data.model.LoggedInUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final Context context;

    public LoginDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Login menggunakan Firebase Authentication (cara umum)
     * Hasil dikembalikan secara async melalui callback agar tidak blocking main thread.
     */
    public void login(String email, String password, LoginCallback callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser == null) {
                            callback.onResult(new Result.Error(new IOException("User tidak ditemukan")));
                            return;
                        }

                        String userId = firebaseUser.getUid();

                        // Ambil data tambahan dari Firebase Realtime Database
                        FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(userId)
                                .get()
                                .addOnCompleteListener(dataTask -> {
                                    if (dataTask.isSuccessful() && dataTask.getResult().exists()) {
                                        DataSnapshot snapshot = dataTask.getResult();
                                        String name = snapshot.child("name").getValue(String.class);

                                        LoggedInUser user = new LoggedInUser(
                                                userId,
                                                name != null ? name : email
                                        );

                                        callback.onResult(new Result.Success<>(user));

                                    } else {
                                        callback.onResult(new Result.Error(
                                                new IOException("Data user tidak ditemukan")));
                                    }
                                })
                                .addOnFailureListener(e ->
                                        callback.onResult(new Result.Error(
                                                new IOException("Gagal membaca data user", e)))
                                );

                    } else {
                        String errorMsg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login gagal";
                        callback.onResult(new Result.Error(new IOException(errorMsg)));
                    }
                });
    }

    /**
     * Callback untuk mengembalikan hasil login
     */
    public interface LoginCallback {
        void onResult(Result<LoggedInUser> result);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
