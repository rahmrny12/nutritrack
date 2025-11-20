package com.example.nutritrack.ui.log_food;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.IngredientModel;
import com.example.nutritrack.data.model.MealModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogFoodFragment#} factory method to
 * create an instance of this fragment.
 */
public class LogFoodFragment extends Fragment {

    private LinearLayout tabAll, tabMyMeals;
    private View indicatorAll, indicatorMyMeals;
    private String currentSelectedCategory = "Breakfast";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_food, container, false);

        tabAll = view.findViewById(R.id.tabAll);
        tabMyMeals = view.findViewById(R.id.tabMyMeals);
        indicatorAll = view.findViewById(R.id.indicatorAll);
        indicatorMyMeals = view.findViewById(R.id.indicatorMyMeals);

        LinearLayout btnCreateMeal = view.findViewById(R.id.btnCreateMeal);

        btnCreateMeal.setOnClickListener(v -> {
            openCreateMeal();
        });

        // DEFAULT → ALL
        replaceFragment(new FoodAllFragment());
        activateAll();

        tabAll.setOnClickListener(v -> {
            replaceFragment(new FoodAllFragment());
            activateAll();
        });

        tabMyMeals.setOnClickListener(v -> {
            replaceFragment(new FoodMyMealsFragment());
            activateMyMeals();
        });

        Spinner spinner = view.findViewById(R.id.spinnerKategori);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.meal_categories,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                currentSelectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

// Auto-select based on time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String selectedCategory;

        if (hour >= 4 && hour < 11) {
            selectedCategory = "Breakfast";
        } else if (hour >= 11 && hour < 16) {
            selectedCategory = "Lunch";
        } else {
            selectedCategory = "Dinner";
        }

        // Set selection
        int position = adapter.getPosition(selectedCategory);
        spinner.setSelection(position);
        currentSelectedCategory = selectedCategory;

        return view;
    }

    private void openCreateMeal() {
        // Jika mau buka Activity
        Intent intent = new Intent(getContext(), CreateMyMealActivity.class);
        startActivity(intent);
    }

    private void replaceFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void activateAll() {
        indicatorAll.setBackgroundColor(Color.parseColor("#0F9E99"));
        indicatorMyMeals.setBackgroundColor(Color.parseColor("#D3D3D3"));
    }

    private void activateMyMeals() {
        indicatorAll.setBackgroundColor(Color.parseColor("#D3D3D3"));
        indicatorMyMeals.setBackgroundColor(Color.parseColor("#0F9E99"));
    }

    protected void saveToDiary(Object item, String type) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String category = currentSelectedCategory; // "Breakfast", "Lunch", etc
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        long ts = System.currentTimeMillis();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("diary")
                .child(date)
                .child(category)
                .child(String.valueOf(ts));

        Map<String, Object> data = new HashMap<>();

        if (type.equals("meal")) {
            MealModel meal = (MealModel) item;
            data.put("name", meal.getName());
            data.put("calories", meal.getCalories());
            data.put("carbs", meal.getCarbs());
            data.put("protein", meal.getProtein());
            data.put("fat", meal.getFat());
        } else if (type.equals("ingredient")) {
            IngredientModel ing = (IngredientModel) item;
            data.put("name", ing.name);
            data.put("calories", ing.calories);
        }

        data.put("type", type);
        data.put("time", time);

        ref.setValue(data).addOnSuccessListener(v -> {
            Toast.makeText(getContext(), "Saved to " + category, Toast.LENGTH_SHORT).show();
        });
    }

}
