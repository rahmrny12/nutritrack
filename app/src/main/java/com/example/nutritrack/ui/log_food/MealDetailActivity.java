package com.example.nutritrack.ui.log_food;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.DiaryItemModel;
import com.example.nutritrack.data.model.FoodModel;
import com.example.nutritrack.data.model.MealModel;
import com.example.nutritrack.data.service.DiaryTempStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MealDetailActivity extends AppCompatActivity {

    private MealModel meal;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meal_detail);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // RECEIVE MEAL OBJECT
        meal = (MealModel) getIntent().getSerializableExtra("meal");

        if (meal == null) {
            finish();
            return;
        }

        // UI Elements
        TextView mealTitle = findViewById(R.id.mealTitle);
        TextView calories = findViewById(R.id.calories);
        TextView proteinValue = findViewById(R.id.proteinValue);
        TextView carbsValue = findViewById(R.id.carbsValue);
        TextView fatValue = findViewById(R.id.fatValue);

        TextView ingredientsList = findViewById(R.id.ingredientsList);

        Button addButton = findViewById(R.id.addButton);
        ImageView favIcon = findViewById(R.id.favoriteIcon);


        // ======= APPLY DATA =======

        mealTitle.setText(meal.getMealsName());
        calories.setText(((int) meal.getCalories()) + " kcal");

        proteinValue.setText(meal.getProtein() + " g");
        carbsValue.setText(meal.getCarbs() + " g");
        fatValue.setText(meal.getFat() + " g");


        // ======= SHOW INGREDIENT LIST =======
        if (meal.getIngredients() != null && !meal.getIngredients().isEmpty()) {

            StringBuilder sb = new StringBuilder();

            for (FoodModel f : meal.getIngredients()) {
                sb.append("• ")
                        .append(f.getFoodsName());

                // if you want macros:
                // sb.append("  (")
                //   .append(f.getCalories()).append(" kcal, ")
                //   .append(f.getProtein()).append("g protein)")
                //   .append("\n");

                sb.append("\n");
            }

            ingredientsList.setText(sb.toString().trim());

        } else {
            ingredientsList.setText("- No ingredients data -");
        }


        // ======= FAVORITE TOGGLE =======
        favIcon.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            favIcon.setImageResource(
                    isFavorite ?
                            android.R.drawable.btn_star_big_on :
                            android.R.drawable.btn_star_big_off
            );
        });


        // ======= ADD BUTTON ACTION =======
        addButton.setOnClickListener(v -> {
            saveToDiary(meal, "meal");
            setResult(RESULT_OK);   // ⬅️ send signal to fragment
            finish(); // close screen after saving
        });

    }

    private void saveToDiary(Object item, String type) {

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

            diaryItem.setMealId(m.getId());

        }
        else if (type.equals("food")) {

            FoodModel ing = (FoodModel) item;

            name = ing.getFoodsName();
            calories = ing.getCaloriesPerUnit();

            diaryItem = new DiaryItemModel(
                    name, calories, carbs, protein, fat, "food", time
            );

            diaryItem.setFoodId(ing.getId());
        }
        else {
            Toast.makeText(this, "Unknown diary type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save into temporary store
        DiaryTempStore.getInstance().addItem(diaryItem);

        Toast.makeText(this, "Added to diary!", Toast.LENGTH_SHORT).show();
    }

}
