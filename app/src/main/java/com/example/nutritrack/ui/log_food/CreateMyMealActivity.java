package com.example.nutritrack.ui.log_food;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.MealModel;
import com.example.nutritrack.data.service.MealApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.example.nutritrack.databinding.ActivityCreateMyMealBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMyMealActivity extends AppCompatActivity {

    private EditText etName, etCalories, etCarbs, etProtein, etFat;
    private Button btnSave;
    ChipGroup chipGroup;
    AutoCompleteTextView search;
    List<String> ingredientList;
    ActivityCreateMyMealBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateMyMealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chipGroup = binding.chipGroupIngredients;
        search = binding.ingredientSearch;

        setupIngredientSearch();


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etName = findViewById(R.id.etMealName);
        etCalories = findViewById(R.id.etCalories);
        etCarbs = findViewById(R.id.etCarbs);
        etProtein = findViewById(R.id.etProtein);
        etFat = findViewById(R.id.etFat);
        btnSave = findViewById(R.id.btnSaveMeal);

        btnSave.setOnClickListener(v -> saveMeal());
    }

    private void setupIngredientSearch() {

        String[] FOOD_DATA = {
                "Chicken Breast", "Egg", "Rice", "Oats", "Banana",
                "Broccoli", "Milk", "Beef", "Salmon", "Bread"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                FOOD_DATA
        );

        binding.ingredientSearch.setAdapter(adapter);

        // Add chip when clicked
        binding.ingredientSearch.setOnItemClickListener((parent, view, pos, id) -> {
            String selected = parent.getItemAtPosition(pos).toString();

            if (!ingredientList.contains(selected)) {
                ingredientList.add(selected);
                addChip(selected);
            }

            binding.ingredientSearch.setText("");
        });
    }

    private void addChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);

        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroupIngredients.removeView(chip);
            ingredientList.remove(text);
        });

        binding.chipGroupIngredients.addView(chip);
    }



    private void saveMeal() {
        String name = etName.getText().toString().trim();
        String caloriesStr = etCalories.getText().toString().trim();
        String carbsStr = etCarbs.getText().toString().trim();
        String proteinStr = etProtein.getText().toString().trim();
        String fatStr = etFat.getText().toString().trim();

        if (name.isEmpty() || caloriesStr.isEmpty()) {
            Toast.makeText(this, "Nama dan kalori wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        double calories = Double.parseDouble(caloriesStr);
        double carbs = carbsStr.isEmpty() ? 0 : Double.parseDouble(carbsStr);
        double protein = proteinStr.isEmpty() ? 0 : Double.parseDouble(proteinStr);
        double fat = fatStr.isEmpty() ? 0 : Double.parseDouble(fatStr);

        // Create Model
        MealModel meal = new MealModel(null, name, calories, carbs, protein, fat);

        // Retrofit Service
        MealApiService api = RetrofitClient.getInstance().create(MealApiService.class);

        api.createMeal(meal).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CreateMyMealActivity.this, "Gagal menyimpan meal", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(CreateMyMealActivity.this, "Meal berhasil disimpan!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CreateMyMealActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
