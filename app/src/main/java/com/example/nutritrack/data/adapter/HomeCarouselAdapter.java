package com.example.nutritrack.data.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.example.nutritrack.R;
import com.example.nutritrack.data.model.MonthlySummaryResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class HomeCarouselAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CALORIE = 0;
    private static final int TYPE_WEIGHT = 1;

    public MonthlySummaryResponse.SummaryData summaryData = null;

    public void updateDashboard(MonthlySummaryResponse.SummaryData data) {
        this.summaryData = data;
        notifyItemChanged(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_CALORIE) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_carousel_calorie, parent, false);
            return new CalorieViewHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_carousel_weight, parent, false);
            return new WeightViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_CALORIE) {

            CalorieViewHolder h = (CalorieViewHolder) holder;

            if (summaryData == null) {
                h.tvCalorieGoal.setText("0 cal");
                h.tvRemaining.setText("No\nData");
                h.progressFood.setProgress(0);
                h.progressWater.setProgress(0);
                h.gauge.setValue(0);
                return;
            }

            h.tvCalorieGoal.setText(summaryData.calorieGoal + " cal");
            h.tvRemaining.setText(summaryData.remainingCalories + "\nRemaining");

            h.progressFood.setMax(summaryData.calorieGoal);
            h.progressFood.setProgress(summaryData.foodCalories);

            h.progressWater.setMax(summaryData.waterGoal);
            h.progressWater.setProgress(summaryData.waterIntake);

            h.gauge.setValue(summaryData.gaugePercent);

        } else {

            WeightViewHolder w = (WeightViewHolder) holder;
            // The chart is static for now; actual data can be bound later
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /* ================================
       VIEW HOLDERS
       ================================ */

    static class CalorieViewHolder extends RecyclerView.ViewHolder {

        TextView tvCalorieGoal, tvRemaining;
        ProgressBar progressFood, progressWater;
        ArcGauge gauge;

        public CalorieViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCalorieGoal = itemView.findViewById(R.id.tvCalorieGoal);
            tvRemaining   = itemView.findViewById(R.id.tvRemaining);
            progressFood  = itemView.findViewById(R.id.progressFood);
            progressWater = itemView.findViewById(R.id.progressWater);
            gauge         = itemView.findViewById(R.id.calories_gauge);

            setupGaugeRanges(gauge);
        }

        private void setupGaugeRanges(ArcGauge gauge) {
            Range r1 = new Range();
            r1.setColor(Color.parseColor("#ce0000"));
            r1.setFrom(0);
            r1.setTo(33);

            Range r2 = new Range();
            r2.setColor(Color.parseColor("#E3E500"));
            r2.setFrom(33);
            r2.setTo(66);

            Range r3 = new Range();
            r3.setColor(Color.parseColor("#00b20b"));
            r3.setFrom(66);
            r3.setTo(100);

            gauge.addRange(r1);
            gauge.addRange(r2);
            gauge.addRange(r3);

            gauge.setMinValue(0);
            gauge.setMaxValue(100);
            gauge.setValue(0);
        }

    }

    static class WeightViewHolder extends RecyclerView.ViewHolder {

        LineChart chart;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.weightChart);
            setupWeightChart(chart);
        }

        private void setupWeightChart(LineChart chart) {
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(1, 50f));
            entries.add(new Entry(2, 50.5f));
            entries.add(new Entry(3, 51f));
            entries.add(new Entry(4, 50.8f));
            entries.add(new Entry(5, 51.2f));

            LineDataSet set = new LineDataSet(entries, "Weight (kg)");
            set.setColor(0xFF00ADEF);
            set.setLineWidth(2f);
            set.setCircleColor(0xFF00ADEF);
            set.setCircleRadius(4f);
            set.setValueTextSize(10f);
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            chart.setData(new LineData(set));
            chart.getDescription().setEnabled(false);
            chart.getAxisRight().setEnabled(false);
            chart.animateY(1000);
        }
    }


}
