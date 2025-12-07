package com.example.nutritrack.data.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.example.nutritrack.LogWaterActivity;
import com.example.nutritrack.R;
import com.example.nutritrack.data.model.MonthlySummaryResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class HomeCarouselAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CALORIE = 0;
    private static final int TYPE_WEIGHT = 1;

    public MonthlySummaryResponse.SummaryData summaryData = null;
    public List<ProgressEntry> progressList = new ArrayList<>();

    public static class ProgressEntry {
        public float weight;
        public String date;

        public ProgressEntry(float weight, String date) {
            this.weight = weight;
            this.date = date;
        }
    }

    public void setProgressData(List<ProgressEntry> list) {
        this.progressList = list;
        notifyItemChanged(1); // refresh the weight chart page only
    }

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
                h.tvRemaining.setText(R.string.txt_no_log);
                h.progressFood.setProgress(0);
                h.progressWater.setProgress(0);
                h.gauge.setValue(0);
                return;
            }

            h.btnWater.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), LogWaterActivity.class);
                v.getContext().startActivity(intent);
            });

            h.tvCalorieGoal.setText(summaryData.calorieGoal + " cal");
            h.tvRemaining.setText(
                    summaryData.remainingCalories + "\n" +
                            h.itemView.getContext().getString(R.string.txt_remaining_goal_calorie)
            );

            h.progressFood.setMax(summaryData.calorieGoal);
            h.progressFood.setProgress(summaryData.foodCalories);

            h.progressWater.setMax(summaryData.waterGoal);
            h.progressWater.setProgress(summaryData.waterIntake);

            h.gauge.setValue(summaryData.gaugePercent);

        } else {

            WeightViewHolder w = (WeightViewHolder) holder;
            w.bind(progressList);
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
        LinearLayout btnWater;

        public CalorieViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCalorieGoal = itemView.findViewById(R.id.tvCalorieGoal);
            tvRemaining   = itemView.findViewById(R.id.tvRemaining);
            progressFood  = itemView.findViewById(R.id.progressFood);
            progressWater = itemView.findViewById(R.id.progressWater);
            gauge         = itemView.findViewById(R.id.calories_gauge);
            btnWater         = itemView.findViewById(R.id.btnWater);

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
        }

        public void bind(List<ProgressEntry> list) {
            if (list == null || list.isEmpty()) return;

            ArrayList<Entry> entries = new ArrayList<>();
            int index = 0;

            for (ProgressEntry p : list) {
                entries.add(new Entry(index, p.weight));
                index++;
            }

            LineDataSet set = new LineDataSet(entries, "Progress Berat Badan");

            // 🌈 GRADIENT COLOR (fade effect)
            int startColor = Color.parseColor("#00ADEF");
            int endColor = Color.parseColor("#88DFFC");
            set.setColor(startColor);
            set.setGradientColor(startColor, endColor);

            // 🌟 LINE STYLING
            set.setLineWidth(2.5f);
            set.setCircleColor(startColor);
            set.setCircleRadius(5f);
            set.setCircleHoleRadius(2.5f);
            set.setCircleHoleColor(Color.WHITE);

            // VALUE TEXT
            set.setValueTextSize(0f);

            // Smooth curve
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            // FILL AREA
            set.setDrawFilled(true);
            set.setFillColor(Color.parseColor("#332BBDED"));
            set.setFillAlpha(90);

            LineData lineData = new LineData(set);
            chart.setData(lineData);

            // 🧼 CLEAN UI
            chart.getAxisRight().setEnabled(false);
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getXAxis().setDrawGridLines(false);

            // === ✨ X AXIS — TAMPILKAN TANGGAL ===
            chart.getXAxis().setValueFormatter(new DateValueFormatter(list));
            chart.getXAxis().setGranularity(1f);
            chart.getXAxis().setGranularityEnabled(true);

            chart.getXAxis().setDrawLabels(true);
            chart.getXAxis().setTextColor(Color.GRAY);
            chart.getXAxis().setTextSize(10f);
            chart.getXAxis().setLabelRotationAngle(45f);
            chart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setAxisLineColor(Color.TRANSPARENT); // tetap clean

            // BORDER OFF
            chart.setDrawBorders(false);
            chart.setTouchEnabled(true);
            chart.setPinchZoom(false);

            // remove legend + description
            chart.getLegend().setEnabled(false);
            chart.getDescription().setEnabled(false);

            // ✨ ANIMATION
            chart.animateY(900);

            chart.invalidate();
        }


        public static class DateValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

            private final List<ProgressEntry> list;

            public DateValueFormatter(List<ProgressEntry> list) {
                this.list = list;
            }

            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;

                if (index < 0 || index >= list.size()) return "";

                // Format tanggal menjadi "05 Dec" atau sesuai kebutuhan
                String date = list.get(index).date;

                try {
                    // dari yyyy-MM-dd → dd MMM
                    java.text.SimpleDateFormat in = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.text.SimpleDateFormat out = new java.text.SimpleDateFormat("dd MMM");

                    return out.format(in.parse(date));
                } catch (Exception e) {
                    return date; // fallback
                }
            }
        }

    }



}
