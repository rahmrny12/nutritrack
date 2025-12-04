package com.example.nutritrack.data.model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREF_NAME = "nutritrack_user_pref";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PHOTO = "photo_url";   // ⭐ NEW

    private SharedPreferences prefs;

    public UserPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /* ----------------------------------------------------------
       SAVE USER PROFILE
    ---------------------------------------------------------- */
    public void saveUser(String id, String name, String email, String photoUrl) {
        prefs.edit()
                .putString(KEY_USER_ID, id)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_PHOTO, photoUrl)     // ⭐ NEW
                .apply();
    }

    public void savePhoto(String url) {
        prefs.edit().putString(KEY_USER_PHOTO, url).apply();
    }

    public String getPhoto() {
        return prefs.getString(KEY_USER_PHOTO, null);
    }

    /* ----------------------------------------------------------
       OLD METHOD (still works)
    ---------------------------------------------------------- */
    public void saveUser(String id, String name, String email) {
        prefs.edit()
                .putString(KEY_USER_ID, id)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .apply();
    }

    /* ----------------------------------------------------------
       GETTERS
    ---------------------------------------------------------- */
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public String getUserPhotoUrl() {      // ⭐ NEW
        return prefs.getString(KEY_USER_PHOTO, null);
    }

    /* ----------------------------------------------------------
       CLEAR ALL
    ---------------------------------------------------------- */
    public void clear() {
        prefs.edit().clear().apply();
    }

    /* ----------------------------------------------------------
       HEALTH PROFILE COMPLETE
    ---------------------------------------------------------- */
    public void setHealthComplete(boolean complete) {
        prefs.edit().putBoolean("healthComplete", complete).apply();
    }

    public boolean isHealthComplete() {
        return prefs.getBoolean("healthComplete", false);
    }

    /* ----------------------------------------------------------
       DAILY GOALS
    ---------------------------------------------------------- */
    public void saveDailyGoals(String date, int calorie, int protein, int carbs, int fat, String recommendation) {
        prefs.edit()
                .putString("dailyGoalDate", date)
                .putInt("dailyGoalCalorie", calorie)
                .putInt("dailyGoalProtein", protein)
                .putInt("dailyGoalCarbs", carbs)
                .putInt("dailyGoalFat", fat)
                .putString("dailyGoalRecommendation", recommendation)
                .apply();
    }

    public String getSavedDailyGoalDate() {
        return prefs.getString("dailyGoalDate", null);
    }

    public int getSavedDailyGoalCalorie() {
        return prefs.getInt("dailyGoalCalorie", 0);
    }

    public int getSavedDailyGoalProtein() {
        return prefs.getInt("dailyGoalProtein", 0);
    }

    public int getSavedDailyGoalCarbs() {
        return prefs.getInt("dailyGoalCarbs", 0);
    }

    public int getSavedDailyGoalFat() {
        return prefs.getInt("dailyGoalFat", 0);
    }

    public String getRecommendationJson() {
        return prefs.getString("dailyGoalRecommendation", null);
    }
}
