package com.example.nutritrack.data.model;

import java.io.Serializable;
import java.util.List;

public class MealModel implements Serializable {

    private Integer id;
    private String idUser;
    private String mealsName;
    private double calories;
    private double carbs;
    private double protein;
    private double fat;

    // ⭐ LIST OF FOOD INGREDIENTS
    private List<FoodModel> ingredients;


    // ======= CONSTRUCTORS =======

    // Basic constructor
    public MealModel(Integer id,
                     String name,
                     double calories,
                     double carbs,
                     double protein,
                     double fat) {

        this.id = id;
        this.mealsName = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }


    // ⭐ Full constructor with ingredients
    public MealModel(Integer id,
                     String idUser,
                     String name,
                     double calories,
                     double carbs,
                     double protein,
                     double fat,
                     List<FoodModel> ingredients) {

        this.id = id;
        this.idUser = idUser;
        this.mealsName = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.ingredients = ingredients;
    }


    // ======= GETTERS =======
    public Integer getId() { return id; }
    public String getIdUser() { return idUser; }
    public String getMealsName() { return mealsName; }
    public double getCalories() { return calories; }
    public double getCarbs() { return carbs; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public List<FoodModel> getIngredients() { return ingredients; }


    // ======= SETTERS =======
    public void setId(Integer id) { this.id = id; }
    public void setIdUser(String idUser) { this.idUser = idUser; }
    public void setMealsName(String mealsName) { this.mealsName = mealsName; }
    public void setCalories(double calories) { this.calories = calories; }
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public void setProtein(double protein) { this.protein = protein; }
    public void setFat(double fat) { this.fat = fat; }
    public void setIngredients(List<FoodModel> ingredients) { this.ingredients = ingredients; }
}
