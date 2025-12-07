package com.example.nutritrack.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nutritrack.R;
import com.example.nutritrack.data.adapter.ReportCardAdapter;
import com.example.nutritrack.data.model.DailyGoalResponse;
import com.example.nutritrack.data.model.ProgressResponse;
import com.example.nutritrack.data.model.ReportCard;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.model.WeeklyCaloriesResponse;
import com.example.nutritrack.data.model.WeeklyMacrosResponse;
import com.example.nutritrack.data.service.AnalyticsApiService;
import com.example.nutritrack.data.service.HealthApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends Fragment {

    private CardStackView cardStackView;
    private ReportCardAdapter adapter;

    private JsonObject recommendationObj;
    private final List<ReportCard> cards = new ArrayList<>();
    private String userId;

    private LineChart chartWeeklyCalories;
    private PieChart chartWeeklyMacros;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report, container, false);

        UserPreferences userPrefs = new UserPreferences(requireContext());
        userId = userPrefs.getUserId();
        chartWeeklyCalories = view.findViewById(R.id.chartWeeklyCalories);
        chartWeeklyMacros = view.findViewById(R.id.chartWeeklyMacros);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(() -> {
            refreshAllData(swipeRefresh);
        });


        cardStackView = view.findViewById(R.id.cardStackView);
        setupCardStack();

        loadRecommendation();
        fetchWeeklyCalories();
        fetchWeeklyMacros();


        return view;
    }

    private void refreshAllData(SwipeRefreshLayout swipeRefresh) {

        UserPreferences prefs = new UserPreferences(requireContext());
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);

        api.getDailyGoal(userId, today).enqueue(new Callback<DailyGoalResponse>() {
            @Override
            public void onResponse(Call<DailyGoalResponse> call, Response<DailyGoalResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    swipeRefresh.setRefreshing(false);
                    return;
                }

                DailyGoalResponse.Data d = response.body().data;
                if (d == null) {
                    swipeRefresh.setRefreshing(false);
                    return;
                }

                String recJson = (d.recommendation != null)
                        ? new Gson().toJson(d.recommendation)
                        : null;

                // SAVE to preferences
                prefs.saveDailyGoals(
                        d.tanggal,
                        d.goal.calorieGoal,
                        d.goal.proteinGoal,
                        d.goal.carbsGoal,
                        d.goal.fatGoal,
                        recJson
                );

                cards.clear();                // ⚡ clear old cards
                adapter.notifyDataSetChanged();

                loadRecommendation();
                fetchWeeklyCalories();
                fetchWeeklyMacros();

                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<DailyGoalResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
            }
        });

    }


    private void fetchWeeklyMacros() {

        AnalyticsApiService api = RetrofitClient.getInstance().create(AnalyticsApiService.class);

        api.getWeeklyMacros(userId).enqueue(new Callback<WeeklyMacrosResponse>() {
            @Override
            public void onResponse(Call<WeeklyMacrosResponse> call,
                                   Response<WeeklyMacrosResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                int protein = response.body().data.total_macros.protein;
                int karbo   = response.body().data.total_macros.karbohidrat;
                int lemak   = response.body().data.total_macros.lemak;

                showMacroPieChart(protein, karbo, lemak);
            }

            private void showMacroPieChart(int protein, int karbo, int lemak) {

                List<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(protein, "Protein"));
                entries.add(new PieEntry(karbo, "Karbohidrat"));
                entries.add(new PieEntry(lemak, "Lemak"));

                PieDataSet set = new PieDataSet(entries, "");
                set.setSliceSpace(4f);

                // Warna-warna makronutrien
                List<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#4CAF50"));  // Protein
                colors.add(Color.parseColor("#2196F3"));  // Karbo
                colors.add(Color.parseColor("#FF9800"));  // Lemak
                set.setColors(colors);

                set.setValueTextSize(12f);
                set.setValueTextColor(Color.WHITE);

                PieData data = new PieData(set);
                chartWeeklyMacros.setData(data);

                chartWeeklyMacros.setUsePercentValues(true);
                chartWeeklyMacros.getDescription().setEnabled(false);
                chartWeeklyMacros.setDrawHoleEnabled(true);
                chartWeeklyMacros.setHoleRadius(40f);
                chartWeeklyMacros.setTransparentCircleRadius(45f);
                chartWeeklyMacros.setEntryLabelColor(Color.BLACK);
                chartWeeklyMacros.setCenterText("Makro\nMingguan");
                chartWeeklyMacros.setCenterTextSize(14f);

                chartWeeklyMacros.getLegend().setEnabled(true);

                chartWeeklyMacros.animateY(900);
                chartWeeklyMacros.invalidate();
            }


            @Override
            public void onFailure(Call<WeeklyMacrosResponse> call, Throwable t) {}
        });
    }



    /* =====================================================
         CARD STACK CONFIG (Tinder Style Swipe)
     ===================================================== */
    private void setupCardStack() {

        CardStackLayoutManager manager =
                new CardStackLayoutManager(getContext(), new CardStackListener() {
                    @Override public void onCardDragging(Direction direction, float ratio) {}
                    @Override public void onCardSwiped(Direction direction) {}
                    @Override public void onCardRewound() {}
                    @Override public void onCardCanceled() {}
                    @Override public void onCardAppeared(View view, int position) {}
                    @Override
                    public void onCardDisappeared(View view, int position) {
                        if (position == cards.size() - 1) {

                            // Tambahkan ulang semua kartu ke deck (loop)
                            List<ReportCard> clone = new ArrayList<>(cards);
                            cards.addAll(clone);

                            if (cards.size() > 100) {
                                cards.clear();
                                cards.addAll(clone);
                            }

                            adapter.notifyItemRangeInserted(cards.size(), clone.size());
                        }
                    }


                });

        manager.setStackFrom(StackFrom.Top);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8f);
        manager.setScaleInterval(0.93f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(10f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollVertical(false);

        adapter = new ReportCardAdapter(cards);

        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
    }


    /* =====================================================
         LOAD JSON RECOMMENDATION
     ===================================================== */
    private void loadRecommendation() {

        UserPreferences prefs = new UserPreferences(requireContext());
        String recJson = prefs.getRecommendationJson();

        if (recJson == null || recJson.trim().isEmpty() || recJson.equals("null")) {
            cards.add(new ReportCard("Belum Ada Data",
                    "Mulai catat konsumsi harianmu untuk melihat laporan AI."));
            adapter.notifyDataSetChanged();
            return;
        }

        try {
            recommendationObj = JsonParser.parseString(recJson).getAsJsonObject();
        } catch (Exception e) {
            return;
        }

        if (recommendationObj == null) return;

        addAnalisisMakroCards();
        addAreaPerbaikanCards();

        cards.add(new ReportCard(
                "🎉 Selesai!",
                "Kamu sudah melihat semua analisis minggu ini.\n"
                        + "Lanjutkan catatan harianmu untuk laporan berikutnya 😊"
        ));

        adapter.notifyDataSetChanged();
    }


    /* =====================================================
         ANALISIS MAKRO → Jadi 4 Kartu Terpisah
     ===================================================== */
    private void addAnalisisMakroCards() {

        if (!recommendationObj.has("analisis_makro")) return;

        JsonObject m = recommendationObj.getAsJsonObject("analisis_makro");

        // Protein
        cards.add(new ReportCard(
                "Analisis Protein",
                safe(m, "protein")
        ));

        // Karbohidrat
        cards.add(new ReportCard(
                "Analisis Karbohidrat",
                safe(m, "karbohidrat")
        ));

        // Lemak
        cards.add(new ReportCard(
                "Analisis Lemak",
                safe(m, "lemak")
        ));

        // Summary
        cards.add(new ReportCard(
                "Ringkasan Makronutrien",
                safe(m, "kekurangan_atau_berlebihan")
        ));
    }


    /* =====================================================
         AREA DIPERBAIKI → Setiap item jadi satu kartu
     ===================================================== */
    private void addAreaPerbaikanCards() {

        if (!recommendationObj.has("area_diperbaiki")) return;

        JsonArray arr = recommendationObj.getAsJsonArray("area_diperbaiki");

        for (JsonElement el : arr) {

            JsonObject item = el.getAsJsonObject();

            String title = safe(item, "judul");
            String desc  = safe(item, "dampak");

            cards.add(new ReportCard(title, desc));
        }
    }


    /* =====================================================
         SAFE HELPER
     ===================================================== */
    private String safe(JsonObject obj, String key) {
        try {
            return obj.has(key) ? obj.get(key).getAsString() : "-";
        } catch (Exception e) {
            return "-";
        }
    }

    private void fetchWeeklyCalories() {

        AnalyticsApiService api = RetrofitClient.getInstance().create(AnalyticsApiService.class);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        api.getWeeklyCalories(userId, today)
                .enqueue(new Callback<WeeklyCaloriesResponse>() {
                    @Override
                    public void onResponse(Call<WeeklyCaloriesResponse> call,
                                           Response<WeeklyCaloriesResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) return;

                        List<Entry> entries = new ArrayList<>();
                        List<String> labels = new ArrayList<>();

                        int i = 0;
                        for (WeeklyCaloriesResponse.WeekEntry e : response.body().data.weekly_calories) {
                            entries.add(new Entry(i, e.calories)); // FIX
                            labels.add(e.date);
                            i++;
                        }

                        showCalorieChart(entries, labels);
                    }

                    private void showCalorieChart(List<Entry> entries, List<String> labels) {

                        LineDataSet set = new LineDataSet(entries, "Kalori Mingguan");

                        set.setColor(Color.parseColor("#00ADEF"));
                        set.setLineWidth(2.5f);
                        set.setCircleColor(Color.parseColor("#00ADEF"));
                        set.setCircleRadius(5f);
                        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        set.setDrawFilled(true);
                        set.setFillColor(Color.parseColor("#332BBDED"));
                        set.setFillAlpha(80);
                        set.setValueTextSize(0f);

                        LineData data = new LineData(set);
                        chartWeeklyCalories.setData(data);

                        chartWeeklyCalories.getAxisRight().setEnabled(false);
                        chartWeeklyCalories.getAxisLeft().setDrawGridLines(false);
                        chartWeeklyCalories.getXAxis().setDrawGridLines(false);

                        chartWeeklyCalories.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                int index = (int) value;
                                return index >= 0 && index < labels.size()
                                        ? labels.get(index)
                                        : "";
                            }
                        });

                        chartWeeklyCalories.getXAxis().setGranularity(1f);
                        chartWeeklyCalories.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                        chartWeeklyCalories.getDescription().setEnabled(false);
                        chartWeeklyCalories.getLegend().setEnabled(false);

                        chartWeeklyCalories.animateY(900);
                        chartWeeklyCalories.invalidate();
                    }

                    @Override
                    public void onFailure(Call<WeeklyCaloriesResponse> call, Throwable t) {}
                });

    }


}
