package com.example.nutritrack;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogWaterActivity extends AppCompatActivity {

    private TextView txtTotalDrink;
    private int totalDrink = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_water);

        txtTotalDrink = findViewById(R.id.txtTotalDrink);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btn250).setOnClickListener(v -> addWater(250));
        findViewById(R.id.btn500).setOnClickListener(v -> addWater(500));
        findViewById(R.id.btn1000).setOnClickListener(v -> addWater(1000));
        findViewById(R.id.btnCustom).setOnClickListener(v -> showCustomDialog());

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            Toast.makeText(this, "Water saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void addWater(int amount) {
        totalDrink += amount;
        txtTotalDrink.setText(totalDrink + " ml");
        findViewById(R.id.btnSave).setEnabled(true);
    }

    private void showCustomDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter ml");

        new AlertDialog.Builder(this)
                .setTitle("Custom Water Amount")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    int val = Integer.parseInt(input.getText().toString());
                    addWater(val);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
