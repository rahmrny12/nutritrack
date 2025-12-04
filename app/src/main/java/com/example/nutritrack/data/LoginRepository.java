package com.example.nutritrack.data;

import android.content.Context;

import com.example.nutritrack.data.model.LoggedInUser;
import com.example.nutritrack.data.model.UserPreferences;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private Context appContext;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource, Context context) {
        this.dataSource = dataSource;
        this.appContext = context.getApplicationContext();
    }

    public static LoginRepository getInstance(LoginDataSource dataSource, Context context) {
        if (instance == null) {
            instance = new LoginRepository(dataSource, context);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public void login(String email, String password, LoginDataSource.LoginCallback callback) {

        dataSource.login(email, password, result -> {

            if (result instanceof Result.Success) {

                LoggedInUser user = ((Result.Success<LoggedInUser>) result).getData();

                // Save to SharedPreferences
                UserPreferences prefs = new UserPreferences(appContext);
                prefs.saveUser(
                        user.getUserId(),
                        user.getDisplayName(),
                        email
                );

                setLoggedInUser(user); // existing logic
            }

            callback.onResult(result);
        });
    }

}