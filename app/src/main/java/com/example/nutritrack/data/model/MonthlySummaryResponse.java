package com.example.nutritrack.data.model;

import java.util.List;

public class MonthlySummaryResponse {
    public String status;
    public List<SummaryItem> data;

    public static class SummaryItem {
        public String date;
        public SummaryData data; // nullable when no food/water
    }

    public static class SummaryData {
        public int calorieGoal;
        public int foodCalories;
        public int waterIntake;
        public int waterGoal;
        public int remainingCalories;
        public int gaugePercent;
    }
}
