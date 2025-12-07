package com.example.nutritrack.data.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    public String status;
    public String message;

    @SerializedName("data")
    public Object data;
}
