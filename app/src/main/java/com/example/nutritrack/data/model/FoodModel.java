package com.example.nutritrack.data.model;

import java.io.Serializable;

public class FoodModel implements Serializable {
    private Integer id;
    private String foodsName;
    private double caloriesPerUnit;
    private double proteinPerUnit;
    private double carbsPerUnit;
    private double fatPerUnit;

    public Integer getId() { return id; }
    public String getFoodsName() { return foodsName; }
    public double getCaloriesPerUnit() { return caloriesPerUnit; }
    public double getProteinPerUnit() { return proteinPerUnit; }
    public double getCarbsPerUnit() { return carbsPerUnit; }
    public double getFatPerUnit() { return fatPerUnit; }
}
