package com.example.nutritrack.data.model;

public class LoginResponse {

    public String status;
    public UserData data;
    public String message;

    public static class UserData {
        public String message;
        public int id;
        public String fullname;
        public String gender;
        public String email;
    }
}
