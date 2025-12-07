package com.example.nutritrack.data.model;

public class UpdateProfileResponse {

    public String status;
    public Data data;
    public String message;

    public static class Data {
        public String message;
        public User user;
    }

    public static class User {
        public int id;
        public String fullname;
        public String email;
        public String gender;
        public double height;
        public double weight;
        public int age;
        public double bmi;
        public int daily_calories_target;
        public String bmi_category;
    }
}
