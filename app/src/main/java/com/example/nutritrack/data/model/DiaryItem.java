package com.example.nutritrack.data.model;

public class DiaryItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_CATEGORY = 1;
    public static final int TYPE_MEAL = 2;

    public int type;

    // Category fields
    public String category;
    public boolean expanded = true; // default expanded

    // Meal fields
    public MealModel meal;

    /** Constructor for HEADER */
    public DiaryItem(int type) {
        this.type = type;  // Only for TYPE_HEADER
    }

    /** Constructor for MEAL */
    public DiaryItem(MealModel meal) {
        this.type = TYPE_MEAL;
        this.meal = meal;
    }

    public DiaryItem(int typeCategory, String category) {
        this.type = TYPE_CATEGORY;
        this.category = category;
        this.expanded = false;
    }
}
