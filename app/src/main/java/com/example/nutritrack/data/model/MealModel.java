package com.example.nutritrack.data.model;

import java.io.Serializable;

public class MealModel implements Serializable {
    private Integer id;
    private String mealsName;
    private double calories;
    private double carbs;
    private double protein;
    private double fat;

    public MealModel(Integer id, String name, double calories, double carbs, double protein, double fat) {
        this.id = id;
        this.mealsName = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    // GETTERS (optional)
    public Integer getId() { return id; }
    public String getMealsName() { return mealsName; }
    public double getCalories() { return calories; }
    public double getCarbs() { return carbs; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
}
