package com.example.nutritrack;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritrack.data.model.DiaryDetail;
import com.example.nutritrack.data.model.FoodModel;
import com.example.nutritrack.data.model.MealModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MealDetailDialog extends DialogFragment {

    private MealModel meal;

    public MealDetailDialog(MealModel meal) {
        this.meal = meal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_meal_detail_dialog, container, false);

        TextView title = v.findViewById(R.id.mealTitle);
        TextView calories = v.findViewById(R.id.calories);
        TextView protein = v.findViewById(R.id.proteinValue);
        TextView carbs = v.findViewById(R.id.carbsValue);
        TextView fat = v.findViewById(R.id.fatValue);
        TextView ingredients = v.findViewById(R.id.ingredientsList);
        Button add = v.findViewById(R.id.addButton);

        title.setText(meal.getMealsName());
        calories.setText(((int) meal.getCalories()) + " kcal");
        protein.setText(meal.getProtein() + " g");
        carbs.setText(meal.getCarbs() + " g");
        fat.setText(meal.getFat() + " g");

        StringBuilder sb = new StringBuilder();
        for (FoodModel f : meal.getIngredients()) {
            sb.append("• ").append(f.getFoodsName()).append("\n");
        }
        ingredients.setText(sb.toString());

        add.setOnClickListener(btn -> {
            saveToDiary(meal, "meal");
            Bundle result = new Bundle();
            result.putBoolean("refresh", true);
            getParentFragmentManager().setFragmentResult("meal_added", result);
            dismiss();
        });

        return v;
    }

    private void saveToDiary(Object item, String type) {

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        String name = "";
        double calories = 0, carbs = 0, protein = 0, fat = 0;

        DiaryDetail diaryItem;

        if (type.equals("meal")) {

            MealModel m = (MealModel) item;

            name = m.getMealsName();
            calories = m.getCalories();
            carbs = m.getCarbs();
            protein = m.getProtein();
            fat = m.getFat();

            diaryItem = new DiaryDetail(
                    name, calories, carbs, protein, fat, type, time
            );

            diaryItem.setMealId(m.getId());

        }
        else if (type.equals("food")) {

            FoodModel ing = (FoodModel) item;

            name = ing.getFoodsName();
            calories = ing.getCaloriesPerUnit();

            diaryItem = new DiaryDetail(
                    name, calories, carbs, protein, fat, "food", time
            );

            diaryItem.setFoodId(ing.getId());
        }
        else {
            Toast.makeText(getContext(), "Unknown diary type", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: add save diary
        Toast.makeText(getContext(), "Added to diary!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
