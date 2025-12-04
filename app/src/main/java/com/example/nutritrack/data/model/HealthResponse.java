package com.example.nutritrack.data.model;

import com.google.gson.annotations.SerializedName;

public class HealthResponse {

    public String status;
    public Data data;
    public String message;

    public static class Data {
        public User user;

        @SerializedName("missing_fields")
        public String[] missingFields;

        @SerializedName("filled_fields")
        public Filled filledFields;

        @SerializedName("is_complete")
        public boolean isComplete;
    }

    public static class User {
        public int id;
        public String fullname;
        public String email;

        @SerializedName("height")
        public Double height;

        @SerializedName("weight")
        public Double weight;

        @SerializedName("age")
        public Integer age;

        public String gender;

        @SerializedName("waist_size")
        public Double waistSize;

        public Double bmi;

        @SerializedName("bmi_category")
        public String bmiCategory;

        @SerializedName("daily_calories_target")
        public Integer dailyCaloriesTarget;
    }

    public static class Filled {
        public Double height;
        public Double weight;
        public Integer age;
        public String gender;

        @SerializedName("waist_size")
        public Double waistSize;

        public Double bmi;
    }
}
