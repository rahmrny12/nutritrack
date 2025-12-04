package com.example.nutritrack.ui.log_food;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.MealModel;

public class MealDetailActivity extends AppCompatActivity {

    private MealModel meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meal_detail);

        // RECEIVE MEAL OBJECT
        meal = (MealModel) getIntent().getSerializableExtra("meal");

        if (meal == null) {
            finish();
            return;
        }

        // FIND UI ELEMENTS
        TextView mealTitle = findViewById(R.id.mealTitle);
        TextView calories = findViewById(R.id.calories);
        TextView proteinValue = findViewById(R.id.proteinValue);
        TextView carbsValue = findViewById(R.id.carbsValue);
        TextView fatValue = findViewById(R.id.fatValue);
        TextView cookingTime = findViewById(R.id.cookingTime);
        TextView weight = findViewById(R.id.weight);
        TextView ingredientsList = findViewById(R.id.ingredientsList);
        Button addButton = findViewById(R.id.addButton);
        ImageView favIcon = findViewById(R.id.favoriteIcon);

        // APPLY DATA
        mealTitle.setText(meal.getMealsName());
        calories.setText(((int) meal.getCalories()) + " kcal");

        proteinValue.setText(meal.getProtein() + "g");
        carbsValue.setText(meal.getCarbs() + "g");
        fatValue.setText(meal.getFat() + "g");

        // OPTIONAL (fake data)
        cookingTime.setText("15 min");
        weight.setText("100 gr");

        // Placeholder ingredients (since MealModel doesn't have ingredients yet)
        ingredientsList.setText("- No ingredients data -");

        // FAVORITE CLICK (optional)
        favIcon.setOnClickListener(v -> {
            favIcon.setImageResource(android.R.drawable.btn_star_big_on);
        });

        // ADD BUTTON ACTION (save to diary)
        addButton.setOnClickListener(v -> {
            // You can trigger save here if needed
            finish();
        });
    }
}
