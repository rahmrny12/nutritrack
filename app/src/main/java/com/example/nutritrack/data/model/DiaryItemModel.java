package com.example.nutritrack.data.model;

public class DiaryItemModel {

    private String name;
    private double calories;
    private double carbs;
    private double protein;
    private double fat;
    private String type; // "meal" or "food"
    private String time;

    private Integer mealId; // ID meal dari API
    private Integer foodId; // ID food dari API

    public DiaryItemModel(String name, double calories, double carbs,
                          double protein, double fat, String type, String time) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.type = type;
        this.time = time;
    }

    // ---- SETTERS ----
    public void setMealId(Integer mealId) {
        this.mealId = mealId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    // ---- GETTERS ----
    public String getName() { return name; }
    public double getCalories() { return calories; }
    public double getCarbs() { return carbs; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public String getType() { return type; }
    public String getTime() { return time; }

    public Integer getMealId() { return mealId; }
    public Integer getFoodId() { return foodId; }
}
