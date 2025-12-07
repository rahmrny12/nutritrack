package com.example.nutritrack.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.ReportCard;

import java.util.List;

public class ReportSwipeAdapter extends RecyclerView.Adapter<ReportSwipeAdapter.ViewHolder> {

    private List<ReportCard> list;

    public ReportSwipeAdapter(List<ReportCard> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ReportCard card = list.get(pos);
        h.title.setText(card.title);
        h.text.setText(card.text);
        RecyclerView.LayoutParams params =
                (RecyclerView.LayoutParams) h.itemView.getLayoutParams();
        if (pos == 0) params.leftMargin = 60;
        if (pos == list.size() - 1) params.rightMargin = 60;
        h.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, text;

        public ViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvCardTitle);
            text = v.findViewById(R.id.tvCardText);
        }
    }
}
