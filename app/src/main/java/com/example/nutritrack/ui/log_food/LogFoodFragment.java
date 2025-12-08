package com.example.nutritrack.ui.log_food;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.DiaryDetail;
import com.example.nutritrack.data.model.DiaryModel;
import com.example.nutritrack.data.model.FoodModel;
import com.example.nutritrack.data.model.MealModel;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.DiaryApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogFoodFragment extends Fragment {

    private LinearLayout tabAll, tabMyMeals;
    private View indicatorAll, indicatorMyMeals;
    private TextView tvDailyCount;
    private AppCompatEditText searchEditText;

    private int apiDiaryCount = 0; // count from API (today)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_food, container, false);

        tabAll = view.findViewById(R.id.tabAll);
        tabMyMeals = view.findViewById(R.id.tabMyMeals);
        indicatorAll = view.findViewById(R.id.indicatorAll);
        indicatorMyMeals = view.findViewById(R.id.indicatorMyMeals);
        tvDailyCount = view.findViewById(R.id.tvDailyCount);
        searchEditText = view.findViewById(R.id.searchview);

        // 🔍 Search listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendSearchQueryToChild(s.toString());
            }
        });

        // Load diary count from API at startup
        loadTodayDiaryCountFromAPI();

        // Click = open Diary tab
        tvDailyCount.setOnClickListener(v -> {
            BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
            navView.setSelectedItemId(R.id.navigation_diary);
        });

        // Create meal button
        view.findViewById(R.id.btnCreateMeal).setOnClickListener(v ->
                startActivity(new Intent(getContext(), CreateMyMealActivity.class))
        );

        // Default tab
        replaceFragment(new FoodAllFragment());
        activateAll();

        tabAll.setOnClickListener(v -> {
            replaceFragment(new FoodAllFragment());
            activateAll();
        });

        tabMyMeals.setOnClickListener(v -> {
            FoodMyMealsFragment frag = new FoodMyMealsFragment();
            frag.setOnMealUpdatedListener(this::updateDailyCountDisplay);
            replaceFragment(frag);
            activateMyMeals();
        });

        return view;
    }

    // ================================
    //  LOAD DIARY FROM API
    // ================================
    private void loadTodayDiaryCountFromAPI() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int userId = Integer.valueOf(new UserPreferences(requireContext()).getUserId());

        DiaryApiService api = RetrofitClient.getInstance().create(DiaryApiService.class);

        api.getDiaryByDate(userId, today).enqueue(new Callback<DiaryApiService.DiaryResponse>() {
            @Override
            public void onResponse(Call<DiaryApiService.DiaryResponse> call, Response<DiaryApiService.DiaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    apiDiaryCount = response.body().data.size();
                }
                updateDailyCountDisplay();
            }

            @Override
            public void onFailure(Call<DiaryApiService.DiaryResponse> call, Throwable t) {
                updateDailyCountDisplay();
            }
        });
    }

    // Total = API + Temp
    private void updateDailyCountDisplay() {
//        int tempCount = DiaryTempStore.getInstance().getAll().size();

        if (apiDiaryCount == 0) {
            tvDailyCount.setVisibility(View.GONE);
        } else {
            tvDailyCount.setVisibility(View.VISIBLE);
            tvDailyCount.setText(
                    getString(R.string.action_today_logged_meal) +
                            " " + (apiDiaryCount > 99 ? "99+" : apiDiaryCount)
            );
        }
    }

    // ================================
    //  SAVE TO DIARY (ONLINE or TEMP)
    // ================================
    protected void saveToDiary(Object item, String type) {

        // Get category based on time
        String category = getCurrentCategory();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        int userId = Integer.parseInt(new UserPreferences(requireContext()).getUserId());

        DiaryModel payload;

        if (type.equals("meal")) {
            MealModel m = (MealModel) item;

            payload = new DiaryModel(
                    null,
                    null,
                    userId,
                    m.getId(),
                    null,
                    date,
                    category
            );

        } else { // FOOD
            FoodModel f = (FoodModel) item;

            payload = new DiaryModel(
                    null,
                    null,
                    userId,
                    null,
                    f.getId(),
                    date,
                    category
            );
        }

        sendToDiaryAPI(payload);
    }

    // Send to API
    private void sendToDiaryAPI(DiaryModel payload) {
        DiaryApiService api = RetrofitClient.getInstance().create(DiaryApiService.class);

        api.createDiary(payload).enqueue(new Callback<DiaryApiService.ApiResponse>() {
            @Override
            public void onResponse(Call<DiaryApiService.ApiResponse> call, Response<DiaryApiService.ApiResponse> response) {
                if (response.isSuccessful()) {
                    apiDiaryCount++;
                    updateDailyCountDisplay();
                    Toast.makeText(getContext(), "Saved to diary!", Toast.LENGTH_SHORT).show();
                } else {
                    saveToTemp(payload);
                }
            }

            @Override
            public void onFailure(Call<DiaryApiService.ApiResponse> call, Throwable t) {
                saveToTemp(payload);
            }
        });
    }

    // If API fails → Save temporarily
    private void saveToTemp(DiaryModel payload) {
//        DiaryTempStore.getInstance().addItem(payload);
        updateDailyCountDisplay();
        Toast.makeText(getContext(), "Offline saved. Will sync later.", Toast.LENGTH_SHORT).show();
    }

    private String getCurrentCategory() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 4 && hour < 11) return "Breakfast";
        if (hour >= 11 && hour < 16) return "Lunch";
        return "Dinner";
    }

    private void sendSearchQueryToChild(String query) {
        Fragment frag = getChildFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (frag instanceof Searchable) ((Searchable) frag).onSearchQuery(query);
    }

    private void replaceFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    public interface Searchable {
        void onSearchQuery(String query);
    }

    private void activateAll() {
        indicatorAll.setBackgroundColor(Color.parseColor("#0F9E99"));
        indicatorMyMeals.setBackgroundColor(Color.parseColor("#D3D3D3"));
    }

    private void activateMyMeals() {
        indicatorAll.setBackgroundColor(Color.parseColor("#D3D3D3"));
        indicatorMyMeals.setBackgroundColor(Color.parseColor("#0F9E99"));
    }
}
