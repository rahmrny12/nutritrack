package com.example.nutritrack.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.example.nutritrack.R;
import com.example.nutritrack.data.adapter.ArticleAdapter;
import com.example.nutritrack.data.adapter.HomeCarouselAdapter;
import com.example.nutritrack.data.adapter.RekomendasiAdapter;
import com.example.nutritrack.data.model.ArticleModel;
import com.example.nutritrack.data.model.DateAdapter;
import com.example.nutritrack.data.model.DateModel;
import com.example.nutritrack.data.model.MonthlySummaryResponse;
import com.example.nutritrack.data.model.ProgressResponse;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.ArticleApiService;
import com.example.nutritrack.data.service.DiaryApiService;
import com.example.nutritrack.data.service.HealthApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.example.nutritrack.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.nutritrack.ui.ArticleDetailActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<DateModel> dateList = new ArrayList<>();
    private List<MonthlySummaryResponse.SummaryItem> monthlyList = new ArrayList<>();
    private List<ArticleModel> articleList = new ArrayList<>();

    private DateAdapter dateAdapter;
    private HomeCarouselAdapter homeCarouselAdapter;
    private LinearLayout articleContainer;

    private int selectedMonth;
    private int selectedYear;
    private String userId;

    private JsonObject recommendationObj;
    private JsonArray rekomendasi;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        UserPreferences userPrefs = new UserPreferences(requireContext());
        String recJson = userPrefs.getRecommendationJson();   // bisa "null", "", atau json valid

        if (recJson == null || recJson.trim().isEmpty() || recJson.equals("null")) {
            recommendationObj = null;
        } else {
            try {
                recommendationObj = JsonParser.parseString(recJson).getAsJsonObject();
            } catch (Exception e) {
                recommendationObj = null;
            }
        }

        if (recommendationObj != null
                    && recommendationObj.has("rekomendasi")
                && recommendationObj.get("rekomendasi").isJsonArray()) {

            rekomendasi = recommendationObj.getAsJsonArray("rekomendasi");

            List<String> tips = new ArrayList<>();

            for (JsonElement e : rekomendasi) tips.add(e.getAsString());

            RekomendasiAdapter adapter = new RekomendasiAdapter(tips);
            binding.rekomendasiPager.setAdapter(adapter);

            binding.rekomendasiPager.setOffscreenPageLimit(3);
            binding.rekomendasiPager.setClipToPadding(false);
            binding.rekomendasiPager.setClipChildren(false);
            binding.rekomendasiPager.setPageTransformer((page, position) -> {
                float scale = 0.9f + (1 - Math.abs(position)) * 0.1f;
                page.setScaleY(scale);
                page.setScaleX(scale);
            });
        }

        userId = userPrefs.getUserId();
        binding.tvHello.setText(userPrefs.getUserName());


        setupDateSelector();
        setupCarousel();

        fetchMonthlySummary();

        articleContainer = binding.articleContainer;
        loadArticles();
        fetchProgressGraph();

        return root;
    }

    private void loadArticles() {

        ArticleApiService api = RetrofitClient.getInstance().create(ArticleApiService.class);

        api.getArticles().enqueue(new Callback<List<ArticleModel>>() {
            @Override
            public void onResponse(Call<List<ArticleModel>> call, Response<List<ArticleModel>> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                articleContainer.removeAllViews(); // clear old items

                for (ArticleModel article : response.body()) {
                    addArticleItem(article);
                }
            }

            private void addArticleItem(ArticleModel article) {

                View item = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_article, articleContainer, false);

                TextView tvTitle    = item.findViewById(R.id.tvTitle);
                TextView tvDesc     = item.findViewById(R.id.tvDesc);
                TextView tvCategory = item.findViewById(R.id.tvCategory);
                TextView tvDate     = item.findViewById(R.id.tvDate);

                tvTitle.setText(article.title);
                tvDesc.setText(article.content);
                tvCategory.setText("Health");   // or article.category
                tvDate.setText(article.createdAt);

                // Optional: click listener
                item.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                    intent.putExtra("article", article);
                    startActivity(intent);
                });

                articleContainer.addView(item);
            }


            @Override
            public void onFailure(Call<List<ArticleModel>> call, Throwable t) {}
        });
    }

    /* ------------------------------------------------------------
       SETUP DATE ROW
       ------------------------------------------------------------ */
    private void setupDateSelector() {

        dateAdapter = new DateAdapter(getContext(), dateList, this::updateDashboardForDate);

        binding.dateRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        binding.dateRecyclerView.setAdapter(dateAdapter);

        Calendar now = Calendar.getInstance();
        selectedMonth = now.get(Calendar.MONTH) + 1;
        selectedYear = now.get(Calendar.YEAR);

        generateMonthDates(selectedMonth, selectedYear);
    }

    /* ------------------------------------------------------------
       CAROUSEL SETUP
       ------------------------------------------------------------ */
    private void setupCarousel() {
        homeCarouselAdapter = new HomeCarouselAdapter();
        binding.homeCarousel.setAdapter(homeCarouselAdapter);
        binding.homeCarousel.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        new TabLayoutMediator(binding.carouselDots, binding.homeCarousel,
                (tab, position) -> {
                    View custom = LayoutInflater.from(getContext())
                            .inflate(R.layout.tab_dot, null);

                    ImageView dot = custom.findViewById(R.id.dot);

                    if (position == 0) {
                        dot.setImageResource(R.drawable.dot_selected);
                    } else {
                        dot.setImageResource(R.drawable.dot_unselected);
                    }

                    tab.setCustomView(custom);
                }
        ).attach();

        binding.carouselDots.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ImageView dot = tab.getCustomView().findViewById(R.id.dot);
                dot.setImageResource(R.drawable.dot_selected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ImageView dot = tab.getCustomView().findViewById(R.id.dot);
                dot.setImageResource(R.drawable.dot_unselected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    private void fetchProgressGraph() {

        HealthApiService
                api = RetrofitClient.getInstance().create(HealthApiService.class);

        api.getProgress(userId).enqueue(new Callback<ProgressResponse>() {
            @Override
            public void onResponse(Call<ProgressResponse> call, Response<ProgressResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<ProgressResponse.Entry> apiList = response.body().data.history;

                List<HomeCarouselAdapter.ProgressEntry> list = new ArrayList<>();

                for (ProgressResponse.Entry e : apiList) {
                    list.add(new HomeCarouselAdapter.ProgressEntry(
                            (float) e.weight,
                            e.createdAt
                    ));
                }

                // SEND TO VIEWPAGER ADAPTER
                homeCarouselAdapter.setProgressData(list);
            }

            @Override
            public void onFailure(Call<ProgressResponse> call, Throwable t) {}
        });
    }


    /* ------------------------------------------------------------
       API → Monthly Summary
       ------------------------------------------------------------ */
    private void fetchMonthlySummary() {

        DiaryApiService api = RetrofitClient.getInstance().create(DiaryApiService.class);

        api.getMonthlySummary(1, userId, selectedMonth, selectedYear)
                .enqueue(new Callback<MonthlySummaryResponse>() {
                    @Override
                    public void onResponse(Call<MonthlySummaryResponse> call,
                                           Response<MonthlySummaryResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) return;

                        monthlyList = response.body().data;

                        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(new Date());

                        updateDashboardForDate(today);
                    }

                    @Override
                    public void onFailure(Call<MonthlySummaryResponse> call, Throwable t) {}
                });
    }

    /* ------------------------------------------------------------
       UPDATE DASHBOARD BASED ON DATE
       ------------------------------------------------------------ */
    private void updateDashboardForDate(String date) {

        MonthlySummaryResponse.SummaryData summary = null;

        for (MonthlySummaryResponse.SummaryItem item : monthlyList) {
            if (item.date.equals(date)) {
                summary = item.data;
                break;
            }
        }

        homeCarouselAdapter.updateDashboard(summary);
    }

    /* ------------------------------------------------------------
       GENERATE DAYS FOR MONTH
       ------------------------------------------------------------ */
    private void generateMonthDates(int month, int year) {

        dateList.clear();

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);

        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat fmtDay = new SimpleDateFormat("EEE", Locale.ENGLISH);
        SimpleDateFormat fmtFull = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String today = fmtFull.format(new Date());

        for (int d = 1; d <= days; d++) {
            cal.set(Calendar.DAY_OF_MONTH, d);

            dateList.add(new DateModel(
                    fmtDay.format(cal.getTime()),
                    d,
                    fmtFull.format(cal.getTime()),
                    fmtFull.format(cal.getTime()).equals(today)
            ));
        }

        dateAdapter.notifyDataSetChanged();
    }
}
