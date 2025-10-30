package com.example.nutritrack.ui;


public class Meal {
    private String name, calories;
    private int img, img2;

    public Meal(String name, String calories, int img, int img2) {
        this.name = name;
        this.calories = calories;
        this.img = img;
        this.img2= img2;
    }

    public String getName() {
        return name;
    }

    public String getCalories() {
        return calories;
    }

    public int getImg() { return img; }
    public int getImg2() { return img2; }
}

