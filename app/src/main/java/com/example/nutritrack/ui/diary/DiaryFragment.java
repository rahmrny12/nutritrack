package com.example.nutritrack.ui.diary;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.DailyGoalResponse;
import com.example.nutritrack.data.model.DiaryAdapter;
import com.example.nutritrack.data.model.DiaryItem;
import com.example.nutritrack.data.model.DiaryModel;
import com.example.nutritrack.data.model.MealModel;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.DiaryApiService;
import com.example.nutritrack.data.service.HealthApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiaryAdapter adapter;
    private TextView tvDailyDate;

    private List<DiaryItem> itemList = new ArrayList<>();
    private Map<String, List<MealModel>> groupedMeals = new HashMap<>();

    public DiaryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDailyDate = view.findViewById(R.id.tvDailyDate);   // 🔹 ambil dari layout

        // format tanggal cantik, misal: Mon, 02 Dec 2025
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat prettyFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
        String todayApi = apiFormat.format(new Date());
        String todayPretty = prettyFormat.format(new Date());
        tvDailyDate.setText(todayPretty);

        recyclerView = view.findViewById(R.id.recyclerDiary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DiaryAdapter(requireContext(), itemList, groupedMeals);
        recyclerView.setAdapter(adapter);

        loadDailyGoal();
        loadDiary();
    }

    private void loadDiary() {

        DiaryApiService api = RetrofitClient.getInstance().create(DiaryApiService.class);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int userId = Integer.valueOf(new UserPreferences(requireContext()).getUserId());

        api.getDiaryByDate(userId, today).enqueue(new Callback<DiaryApiService.DiaryResponse>() {
            @Override
            public void onResponse(Call<DiaryApiService.DiaryResponse> call,
                                   Response<DiaryApiService.DiaryResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                List<DiaryModel> diaryData = response.body().data;

                itemList.clear();
                groupedMeals.clear();

                // HEADER
                itemList.add(new DiaryItem(DiaryItem.TYPE_HEADER));

                String[] categories = {"Breakfast", "Lunch", "Dinner"};

                for (String category : categories) {

                    List<DiaryModel> filtered = new ArrayList<>();

                    for (DiaryModel d : diaryData) {
                        if (category.equalsIgnoreCase(d.getCategory())) {
                            filtered.add(d);
                        }
                    }

                    if (filtered.isEmpty()) continue;

                    itemList.add(new DiaryItem(DiaryItem.TYPE_CATEGORY, category));

                    List<MealModel> mealList = new ArrayList<>();

                    for (DiaryModel d : filtered) {

                        Integer id = (d.getIdMeals() != null) ? d.getIdMeals() : d.getIdFoods();

                        mealList.add(new MealModel(
                                id,
                                d.getName(),
                                d.getCalories(),
                                d.getCarbs(),
                                d.getProtein(),
                                d.getFat()
                        ));
                    }

                    groupedMeals.put(category, mealList);
                }

                // 🔹 HITUNG TOTAL HARI INI
                int totalCalories = 0;
                int totalProtein = 0;
                int totalFat = 0;
                int totalCarbs = 0;

                for (List<MealModel> list : groupedMeals.values()) {
                    for (MealModel m : list) {
                        totalCalories += m.getCalories();
                        totalProtein += m.getProtein();
                        totalFat += m.getFat();
                        totalCarbs += m.getCarbs();
                    }
                }

                // 🔹 TARGET (sementara hardcoded, bisa ambil dari profile / DB nanti)
                int targetCalories = 2000;
                int targetProtein = 120;
                int targetFat = 60;
                int targetCarbs = 250;

                // kirim ke adapter
                adapter.setDailySummary(
                        totalCalories, targetCalories,
                        totalProtein, targetProtein,
                        totalFat, targetFat,
                        totalCarbs, targetCarbs
                );

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<DiaryApiService.DiaryResponse> call, Throwable t) {
                Log.e("DIARY_API", "Failed: " + t.getMessage());
            }
        });
    }

    private void loadDailyGoal() {
        UserPreferences userPrefs = new UserPreferences(requireContext());
        String userId = userPrefs.getUserId();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);
        api.getDailyGoal(userId, today).enqueue(new Callback<DailyGoalResponse>
                () {
            @Override
            public void onResponse(Call<DailyGoalResponse> call,
                                   Response<DailyGoalResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().data == null) {
                    return;
                }

                DailyGoalResponse.Data data = response.body().data;

                if (tvDailyDate != null && data.tanggal != null) {
                    try {
                        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat prettyFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
                        Date parsed = apiFormat.parse(data.tanggal);
                        tvDailyDate.setText(prettyFormat.format(parsed));
                    } catch (Exception e) {
                        tvDailyDate.setText(data.tanggal); // fallback
                    }
                }

                // kirim ke adapter
                if (adapter != null) {
                    adapter.setDailyGoal(data);
                }
            }

            @Override
            public void onFailure(Call<DailyGoalResponse> call, Throwable t) {
                Log.e("DAILY_GOAL", "Failed: " + t.getMessage());
            }
        });
    }


}
