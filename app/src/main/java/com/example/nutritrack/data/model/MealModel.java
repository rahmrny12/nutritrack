package com.example.nutritrack.data.model;

public class MealModel {

    private String name;
    private int calories;
    private int carbs;
    private int protein;
    private int fat;
    private String date;
    private String time;
    private String image;

    // Required empty constructor for Firebase
    public MealModel() {}

    public MealModel(String name, int calories, int carbs, int protein, int fat,
                     String date, String time, String image) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.date = date;
        this.time = time;
        this.image = image;
    }

    // GETTERS
    public String getName() { return name; }
    public int getCalories() { return calories; }
    public int getCarbs() { return carbs; }
    public int getProtein() { return protein; }
    public int getFat() { return fat; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getImage() { return image; }

    // SETTERS
    public void setName(String name) { this.name = name; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setCarbs(int carbs) { this.carbs = carbs; }
    public void setProtein(int protein) { this.protein = protein; }
    public void setFat(int fat) { this.fat = fat; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setImage(String image) { this.image = image; }
}
