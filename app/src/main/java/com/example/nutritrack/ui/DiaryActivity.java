package com.example.nutritrack.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nutritrack.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    private RecyclerView recyclerMeals;
    private MealAdapter adapter;
    private List<Meal> mealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_lagi);
//        CircleShape circleProtein = findViewById(R.id.circleProtein);
//        CircleShape circleFat = findViewById(R.id.circleFat);
//        CircleShape circleCarbs = findViewById(R.id.circleCarbs);
//
//        circleProtein.setProgress(50);
//        circleProtein.setProgressColor(0xFFFFC107); // kuning
//        circleProtein.setLabel("protein");

//        circleFat.setProgress(75);
//        circleFat.setProgressColor(0xFF9C27B0); // ungu
//        circleFat.setLabel("fat");
//
//        circleCarbs.setProgress(100);
//        circleCarbs.setProgressColor(0xFF03A9F4); // biru
//        circleCarbs.setLabel("carbs");

//
//        recyclerMeals = findViewById(R.id.recyclerMeals);
//        mealList = new ArrayList<>();
//
//        mealList = new ArrayList<>();
//        mealList.add(new Meal("Breakfast", "456 - 512 kcal", R.drawable.breakfast1, R.drawable.breakfast2));
//        mealList.add(new Meal("Lunch", "500 - 650 kcal", R.drawable.lunch1, R.drawable.lunch2));
//        mealList.add(new Meal("Dinner", "420 - 580 kcal", R.drawable.dinner1, R.drawable.dinner2));
//        mealList.add(new Meal("Snacks", "200 - 300 kcal", R.drawable.snack1, R.drawable.snack2));
//
//        adapter = new MealAdapter(this, mealList);
//        recyclerMeals.setLayoutManager(new LinearLayoutManager(this));
//        recyclerMeals.setAdapter(adapter);

//        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
//        bottomNav.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_home) {
//                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            } else if (itemId == R.id.nav_report) {
//                Toast.makeText(this, "Report clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            } else if (itemId == R.id.nav_add) {
//                Toast.makeText(this, "Add clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            } else if (itemId == R.id.nav_diary) {
//                Toast.makeText(this, "Diary clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            } else if (itemId == R.id.nav_more) {
//                Toast.makeText(this, "More clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            }

//            return false;
//        });

    }
}