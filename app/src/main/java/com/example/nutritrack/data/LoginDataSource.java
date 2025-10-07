package com.example.nutritrack.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nutritrack.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private Context context;
    private SharedPreferences sharedPref;

    // Add constructor to receive Context
    public LoginDataSource(Context context) {
        this.context = context;
        this.sharedPref = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
    }

    public Result<LoggedInUser> login(String username, String password) {
        try {
            // Get user data from SharedPreferences
            SharedPreferences sharedPref = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);

            String savedUsername = sharedPref.getString("username", "");
            String savedPassword = sharedPref.getString("password", "");
            String savedName = sharedPref.getString("name", "");

            // Check if credentials match
            if (username.equals(savedUsername) && password.equals(savedPassword)) {
                LoggedInUser user = new LoggedInUser(
                        java.util.UUID.randomUUID().toString(),
                        savedName.isEmpty() ? username : savedName
                );
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Username atau password salah"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}