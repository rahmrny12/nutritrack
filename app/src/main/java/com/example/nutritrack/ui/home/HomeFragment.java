package com.example.nutritrack.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.example.nutritrack.data.model.DateAdapter;
import com.example.nutritrack.data.model.DateModel;
import com.example.nutritrack.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView dateRecyclerView;
    private DateAdapter dateAdapter;
    private List<DateModel> dateList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dateRecyclerView = binding.dateRecyclerView;
        dateList = new ArrayList<>();

        // contoh data minggu
        dateList.add(new DateModel("Sun", 4, false));
        dateList.add(new DateModel("Mon", 5, false));
        dateList.add(new DateModel("Tue", 6, false));
        dateList.add(new DateModel("Wed", 7, false));
        dateList.add(new DateModel("Thu", 8, false));
        dateList.add(new DateModel("Fri", 9, true)); // selected
        dateList.add(new DateModel("Sat", 10, false));

        dateAdapter = new DateAdapter(getContext(), dateList);
        dateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dateRecyclerView.setAdapter(dateAdapter);


        // Ambil nama user dari SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        String name = prefs.getString("name", "User");

        setupWeightChart(binding.weightChart);

        setupCaloriesGauge(binding.caloriesGauge);

        return root;
    }

    private void setupCaloriesGauge(ArcGauge arcGauge) {
        // 🔹 Buat range warna (merah, kuning, hijau)
        Range range1 = new Range();
        range1.setColor(Color.parseColor("#ce0000"));
        range1.setFrom(0.0);
        range1.setTo(500.0); // 0–500 kalori sisa (zona merah)

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#E3E500"));
        range2.setFrom(500.0);
        range2.setTo(1000.0); // zona kuning

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#00b20b"));
        range3.setFrom(1000.0);
        range3.setTo(1500.0); // zona hijau (aman)

        // Tambahkan range ke gauge
        arcGauge.addRange(range1);
        arcGauge.addRange(range2);
        arcGauge.addRange(range3);

        // 🔹 Set nilai min, max, dan current value
        arcGauge.setMinValue(0.0);
        arcGauge.setMaxValue(1500.0);
        arcGauge.setValue(800.0); // contoh: sisa kalori 800

    }

    private void setupWeightChart(LineChart chart) {
        // Sample data (dates and weights)
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 50f));
        entries.add(new Entry(2, 50.5f));
        entries.add(new Entry(3, 51f));
        entries.add(new Entry(4, 50.8f));
        entries.add(new Entry(5, 51.2f));

        LineDataSet dataSet = new LineDataSet(entries, "Weight (kg)");
        dataSet.setColor(0xFF00ADEF); // Blue line
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(0xFF00ADEF);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(0xFF333333);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(0xFF6B6B6B);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(0xFF6B6B6B);

        chart.getAxisRight().setEnabled(false);
        chart.animateY(1000);
    }
}
