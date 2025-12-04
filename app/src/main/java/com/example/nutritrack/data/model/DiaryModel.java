package com.example.nutritrack.data.model;

public class DiaryModel {

    private Integer id;
    private Integer idCustomMeals;   // nullable
    private int idUser;
    private Integer idMeals;         // nullable
    private Integer idFoods;         // nullable
    private String date;
    private String category;

    public DiaryModel(Integer id, Integer idCustomMeals, int idUser, Integer idMeals,
                      Integer idFoods, String date, String category) {
        this.id = id;
        this.idCustomMeals = idCustomMeals;
        this.idUser = idUser;
        this.idMeals = idMeals;
        this.idFoods = idFoods;
        this.date = date;
        this.category = category;
    }

    // ===== Joined fields from meals/foods =====
    private String name;     // meal name OR food name
    private Double calories;
    private Double carbs;
    private Double protein;
    private Double fat;
    private String type;     // "meal" or "food"

    // ================== GETTERS ==================
    public Integer getId() { return id; }
    public Integer getIdCustomMeals() { return idCustomMeals; }
    public int getIdUser() { return idUser; }
    public Integer getIdMeals() { return idMeals; }
    public Integer getIdFoods() { return idFoods; }
    public String getDate() { return date; }
    public String getCategory() { return category; }

    public String getName() { return name; }
    public Double getCalories() { return calories; }
    public Double getCarbs() { return carbs; }
    public Double getProtein() { return protein; }
    public Double getFat() { return fat; }
    public String getType() { return type; }

    // ================== SETTERS (opsional) ==================
    public void setId(Integer id) { this.id = id; }
    public void setIdCustomMeals(Integer idCustomMeals) { this.idCustomMeals = idCustomMeals; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public void setIdMeals(Integer idMeals) { this.idMeals = idMeals; }
    public void setIdFoods(Integer idFoods) { this.idFoods = idFoods; }
    public void setDate(String date) { this.date = date; }
    public void setCategory(String category) { this.category = category; }

    public void setName(String name) { this.name = name; }
    public void setCalories(Double calories) { this.calories = calories; }
    public void setCarbs(Double carbs) { this.carbs = carbs; }
    public void setProtein(Double protein) { this.protein = protein; }
    public void setFat(Double fat) { this.fat = fat; }
    public void setType(String type) { this.type = type; }
}
