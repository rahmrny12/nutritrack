package com.example.nutritrack.data.model;

public class DateModel {
    public String dayName;
    public int dayNumber;
    public boolean isSelected;
    public String fullDate; // NEW: yyyy-MM-dd

    public DateModel(String dayName, int dayNumber, String fullDate, boolean isSelected) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.fullDate = fullDate;
        this.isSelected = isSelected;
    }
}
