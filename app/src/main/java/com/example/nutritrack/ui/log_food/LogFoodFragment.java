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
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritrack.R;
import com.example.nutritrack.TempDiaryPopupActivity;
import com.example.nutritrack.data.model.DiaryItemModel;
import com.example.nutritrack.data.model.FoodModel;
import com.example.nutritrack.data.model.IngredientModel;
import com.example.nutritrack.data.model.MealModel;
import com.example.nutritrack.data.service.DiaryTempStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogFoodFragment#} factory method to
 * create an instance of this fragment.
 */
public class LogFoodFragment extends Fragment {

    private LinearLayout tabAll, tabMyMeals;
    private View indicatorAll, indicatorMyMeals;
    private String currentSelectedCategory = "Breakfast";
    FloatingActionButton fab;
    TextView fabBadge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_food, container, false);

        tabAll = view.findViewById(R.id.tabAll);
        tabMyMeals = view.findViewById(R.id.tabMyMeals);
        indicatorAll = view.findViewById(R.id.indicatorAll);
        indicatorMyMeals = view.findViewById(R.id.indicatorMyMeals);
        fab = view.findViewById(R.id.btnDiaryAction);
        fabBadge = view.findViewById(R.id.fabBadge);


        updateFabVisibility();

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TempDiaryPopupActivity.class);
            startActivity(intent);
        });

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

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        String name = "";
        double calories = 0, carbs = 0, protein = 0, fat = 0;

        DiaryItemModel diaryItem;

        if (type.equals("meal")) {

            MealModel m = (MealModel) item;

            name = m.getMealsName();
            calories = m.getCalories();
            carbs = m.getCarbs();
            protein = m.getProtein();
            fat = m.getFat();

            diaryItem = new DiaryItemModel(
                    name, calories, carbs, protein, fat, type, time
            );

            // Set MEAL ID supaya bisa dikirim ke API diary
            diaryItem.setMealId(m.getId());

        } else if (type.equals("food")) {

            FoodModel ing = (FoodModel) item;

            name = ing.getFoodsName();
            calories = ing.getCaloriesPerUnit();

            diaryItem = new DiaryItemModel(
                    name, calories, carbs, protein, fat, "food", time
            );

            // Set FOOD ID
            diaryItem.setFoodId(ing.getId());
        }
        else {
            // Fallback
            Toast.makeText(getContext(), "Unknown diary type", Toast.LENGTH_SHORT).show();
            return;
        }

        // SAVE IN TEMPORARY STORE
        DiaryTempStore.getInstance().addItem(diaryItem);

        Toast.makeText(getContext(), "Diary Saved.", Toast.LENGTH_SHORT).show();

        updateFabVisibility();
    }

    private void updateFabVisibility() {
        int count = DiaryTempStore.getInstance().getAll().size();

        if (count == 0) {
            fab.hide();
            fabBadge.setVisibility(View.GONE);
        } else {
            fab.show();

            fabBadge.setVisibility(View.VISIBLE);
            fabBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        }
    }

}
