package com.example.nutritrack.data.model;

import java.util.List;

public class WeeklyCaloriesResponse {

    public WeeklyData data;

    public static class WeeklyData {
        public int userId;
        public Period periode;
        public List<WeekEntry> weekly_calories;
    }

    public static class Period {
        public String start;
        public String end;
    }

    public static class WeekEntry {
        public String date;
        public int calories;
    }
}
