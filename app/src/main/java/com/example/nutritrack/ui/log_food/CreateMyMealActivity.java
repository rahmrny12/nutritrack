package com.example.nutritrack.ui.log_food;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.FoodModel;
import com.example.nutritrack.data.model.MealModel;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.FoodApiService;
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

    List<FoodModel> apiFoodList = new ArrayList<>();  // store full food data
    List<String> ingredientNames = new ArrayList<>(); // store only names for dropdown
    List<FoodModel> selectedFoods = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateMyMealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        chipGroup = binding.chipGroupIngredients;
        search = binding.ingredientSearch;

        ingredientList = new ArrayList<>();

        // load food from API
        loadIngredientsFromApi();

        etName = binding.etMealName;
        etCalories = binding.etCalories;
        etCarbs = binding.etCarbs;
        etProtein = binding.etProtein;
        etFat = binding.etFat;
        btnSave = binding.btnSaveMeal;

        btnSave.setOnClickListener(v -> saveMeal());
    }

    // ⭐ Fetch ingredient list from API
    private void loadIngredientsFromApi() {

        FoodApiService api = RetrofitClient.getInstance().create(FoodApiService.class);

        api.getFoods().enqueue(new Callback<List<FoodModel>>() {
            @Override
            public void onResponse(Call<List<FoodModel>> call, Response<List<FoodModel>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                apiFoodList.clear();
                apiFoodList.addAll(response.body());

                ingredientNames.clear();
                for (FoodModel f : apiFoodList) {
                    ingredientNames.add(f.getFoodsName());
                }

                setupIngredientSearch(); // setup dropdown now that data is ready
            }

            @Override
            public void onFailure(Call<List<FoodModel>> call, Throwable t) {
                Toast.makeText(CreateMyMealActivity.this, "Gagal mengambil bahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ⭐ setup search + chip adding
    private void setupIngredientSearch() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        ingredientNames);

        search.setAdapter(adapter);

        search.setOnItemClickListener((adapterView, view, position, id) -> {

            String selectedName = adapterView.getItemAtPosition(position).toString();

// find full object
            FoodModel selectedFood = null;
            for (FoodModel f : apiFoodList) {
                if (f.getFoodsName().equals(selectedName)) {
                    selectedFood = f;
                    break;
                }
            }

// skip if null (should not happen)
            if (selectedFood == null) return;

// avoid duplicates
            if (!selectedFoods.contains(selectedFood)) {
                selectedFoods.add(selectedFood);      // store object
                ingredientList.add(selectedName);     // for chip display
                addChip(selectedName);
                recalcNutrition();
            }

            search.setText("");
        });
    }

    private void recalcNutrition() {
        double totalCalories = 0;
        double totalCarbs = 0;
        double totalProtein = 0;
        double totalFat = 0;

        for (FoodModel f : selectedFoods) {
            totalCalories += f.getCaloriesPerUnit();
            totalCarbs += f.getCarbsPerUnit();
            totalProtein += f.getProteinPerUnit();
            totalFat += f.getFatPerUnit();
        }

        // Auto-fill the input fields
        etCalories.setText(String.valueOf(totalCalories));
        etCarbs.setText(String.valueOf(totalCarbs));
        etProtein.setText(String.valueOf(totalProtein));
        etFat.setText(String.valueOf(totalFat));

        if (selectedFoods.isEmpty()) {
            etCalories.setText("");
            etCarbs.setText("");
            etProtein.setText("");
            etFat.setText("");
        }
    }


    private void addChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);

        chip.setOnCloseIconClickListener(v -> {
            ingredientList.remove(text);

            // remove from FoodModel list
            for (FoodModel f : selectedFoods) {
                if (f.getFoodsName().equals(text)) {
                    selectedFoods.remove(f);
                    break;
                }
            }

            chipGroup.removeView(chip);

            recalcNutrition(); // 🔥 update macros
        });

        chipGroup.addView(chip);
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

        UserPreferences userPrefs = new UserPreferences(this);
        String userId = userPrefs.getUserId();

        MealModel meal = new MealModel(null, userId, name, calories, carbs, protein, fat, selectedFoods);

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
