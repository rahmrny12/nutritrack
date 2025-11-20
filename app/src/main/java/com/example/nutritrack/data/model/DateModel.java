package com.example.nutritrack.data.model;

public class DateModel {
    public String dayName;
    public int dayNumber;
    public boolean isSelected;

    public DateModel(String dayName, int dayNumber, boolean isSelected) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.isSelected = isSelected;
    }
}
