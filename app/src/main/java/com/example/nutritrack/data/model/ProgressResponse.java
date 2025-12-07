package com.example.nutritrack.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProgressResponse {
    public String status;
    public Data data;

    public static class Data {
        public String message;
        public int count;
        public List<Entry> history;
    }

    public static class Entry {
        public int id;
        @SerializedName("user_id")
        public int userId;
        public double height;
        public double weight;
        public int age;
        public String gender;
        @SerializedName("waist_size")
        public double waistSize;
        public double bmi;
        @SerializedName("daily_calories_target")
        public double dailyCaloriesTarget;
        @SerializedName("bmi_category")
        public String bmiCategory;
        @SerializedName("created_at")
        public String createdAt;
    }
}
