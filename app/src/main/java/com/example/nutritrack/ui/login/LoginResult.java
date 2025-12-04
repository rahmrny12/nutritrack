package com.example.nutritrack.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;
    @Nullable
    private String errorMessage;

    // Constructor for resource ID error
    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    // Constructor for API string error
    LoginResult(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Constructor for success
    LoginResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    @Nullable
    String getErrorMessage() {
        return errorMessage;
    }
}
