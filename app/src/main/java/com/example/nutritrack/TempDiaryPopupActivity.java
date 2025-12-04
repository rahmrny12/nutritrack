package com.example.nutritrack;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutritrack.data.adapter.TempDiaryPopupAdapter;
import com.example.nutritrack.data.model.DiaryModel;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.DiaryApiService;
import com.example.nutritrack.data.service.DiaryTempStore;
import com.example.nutritrack.data.model.DiaryItemModel;
import com.example.nutritrack.data.service.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TempDiaryPopupActivity extends AppCompatActivity {

    private ListView listView;
    private TempDiaryPopupAdapter adapter;
    Button btnSave;
    TextView diaryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_temp_diary);

        listView = findViewById(R.id.listTempDiary);
        diaryText = findViewById(R.id.diaryText);
        btnSave = findViewById(R.id.btnSaveDiary);
        btnSave.setOnClickListener(v -> saveDiary());

        // Load data
        List<DiaryItemModel> diaryItems = DiaryTempStore.getInstance().getAll();
        adapter = new TempDiaryPopupAdapter(this, diaryItems);
        listView.setAdapter(adapter);

        ImageButton btnClose = findViewById(R.id.btnCloseDiary);
        btnClose.setOnClickListener(v -> finish());

// Get current hour
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String category;

        if (hour >= 4 && hour < 11) {
            category = "Breakfast";
        } else if (hour >= 11 && hour < 16) {
            category = "Lunch";
        } else {
            category = "Dinner";
        }

        // Update UIs
        diaryText.setText(category + " Time");
    }

    private void saveDiary() {

        List<DiaryItemModel> list = DiaryTempStore.getInstance().getAll();

        if (list.isEmpty()) {
            Toast.makeText(this, "Nothing to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        DiaryApiService api = RetrofitClient.getInstance().create(DiaryApiService.class);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Get category from title
        String category = diaryText.getText().toString().replace(" Time", "");

        UserPreferences prefs = new UserPreferences(this);
        int userId = Integer.valueOf(prefs.getUserId());

        for (DiaryItemModel item : list) {

            DiaryModel payload = new DiaryModel(
                    null,                     // idCustomMeals
                    null,
                    userId,                   // idUser
                    item.getType().equals("meal") ? item.getMealId() : null,
                    item.getType().equals("food") ? item.getFoodId() : null,
                    date,
                    category
            );

            api.createDiary(payload).enqueue(new Callback<DiaryApiService.ApiResponse>() {
                @Override
                public void onResponse(Call<DiaryApiService.ApiResponse> call,
                                       Response<DiaryApiService.ApiResponse> response) {

                    if (!response.isSuccessful()) {
                        Toast.makeText(TempDiaryPopupActivity.this,
                                "Gagal menyimpan diary (" + response.code() + ")",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(TempDiaryPopupActivity.this,
                            "Berhasil menyimpan 1 item diary",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<DiaryApiService.ApiResponse> call, Throwable t) {
                    Toast.makeText(TempDiaryPopupActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

        }

        Toast.makeText(this, "Diary Saved.", Toast.LENGTH_SHORT).show();

        DiaryTempStore.getInstance().clear();
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Refresh when reopened
        adapter.notifyDataSetChanged();

        int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.85);

        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

    }
}
