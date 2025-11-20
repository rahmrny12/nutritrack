package com.example.nutritrack.ui.log_food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.MealModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CreateMyMealActivity extends AppCompatActivity {

    private EditText etName, etCalories, etCarbs, etProtein, etFat;
    private Button btnSave;

    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_my_meal);

        etName = findViewById(R.id.etMealName);
        etCalories = findViewById(R.id.etCalories);
        etCarbs = findViewById(R.id.etCarbs);
        etProtein = findViewById(R.id.etProtein);
        etFat = findViewById(R.id.etFat);
        btnSave = findViewById(R.id.btnSaveMeal);

        btnSave.setOnClickListener(v -> saveMeal());
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

        int calories = Integer.parseInt(caloriesStr);
        int carbs = carbsStr.isEmpty() ? 0 : Integer.parseInt(carbsStr);
        int protein = proteinStr.isEmpty() ? 0 : Integer.parseInt(proteinStr);
        int fat = fatStr.isEmpty() ? 0 : Integer.parseInt(fatStr);

        // Auto date & time
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "User tidak ditemukan.", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = "meal_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        DatabaseReference mealRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("meals")
                .child(key);

        MealModel meal = new MealModel(
                name,
                calories,
                carbs,
                protein,
                fat,
                date,
                time,
                "" // image kosong untuk sekarang
        );

        mealRef.setValue(meal)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Meal berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}