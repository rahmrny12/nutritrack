package com.example.nutritrack.data.model;

public class WeeklyMacrosResponse {
    public WeeklyData data;

    public static class WeeklyData {
        public int userId;
        public Period periode;
        public Macros total_macros;
    }

    public static class Period {
        public String start;
        public String end;
    }

    public static class Macros {
        public int protein;
        public int karbohidrat;
        public int lemak;
    }
}
