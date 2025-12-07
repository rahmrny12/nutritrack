package com.example.nutritrack.data.model;

public class UpdateUserHealthResponse {
    public String status;
    public Data data;

    public static class Data {
        public String message;
        public User user;
        public boolean history_saved;
    }

    public static class User {
        public int id;
        public String fullname;
        public String email;
        public Integer age;
        public Integer height;
        public Integer weight;
        public Integer daily_calories_target;
        public String gender;
        public String bmi_category;
        public Double bmi;
        public Integer waist_size;
    }
}
