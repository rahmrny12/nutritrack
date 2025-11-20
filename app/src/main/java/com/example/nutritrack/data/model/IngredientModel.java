package com.example.nutritrack.data.model;

public class IngredientModel {
    public String name;
    public int calories;
    public double grams;

    public IngredientModel() {}

    public IngredientModel(String name, int calories, double grams) {
        this.name = name;
        this.calories = calories;
        this.grams = grams;
    }
}
