package com.example.nutritrack.data.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DailyGoalResponse {

    public String status;

    @SerializedName("data")
    public Data data;

    public static class Data {

        public int id;

        @SerializedName("userId")
        public int userId;

        public String tanggal;

        public Goal goal;
        public Consumed consumed;
        public Remaining remaining;

        // ⭐ FLEXIBLE FIELD → Tidak memecah JSON AI
        @SerializedName("recommendation")
        public JsonObject recommendation;

        @SerializedName("history7Days")
        public List<HistoryItem> history7Days;
    }

    // ==============================
    // GOAL
    // ==============================
    public static class Goal {
        @SerializedName("calorieGoal")
        public int calorieGoal;

        @SerializedName("proteinGoal")
        public int proteinGoal;

        @SerializedName("carbsGoal")
        public int carbsGoal;

        @SerializedName("fatGoal")
        public int fatGoal;
    }

    // ==============================
    // CONSUMED
    // ==============================
    public static class Consumed {
        public int calories;
        public int protein;
        public int carbs;
        public int fat;
        public int water;
    }

    // ==============================
    // REMAINING
    // ==============================
    public static class Remaining {
        public int calories;
        public int protein;
        public int carbs;
        public int fat;
    }

    // ==============================
    // HISTORY ITEM
    // ==============================
    public static class HistoryItem {
        public String date;
        public Goal goal;
        public Consumed consumed;
    }
}
