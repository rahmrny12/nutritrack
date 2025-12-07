package com.example.nutritrack.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.ReportCard;

import java.util.List;

public class ReportCardAdapter extends RecyclerView.Adapter<ReportCardAdapter.ViewHolder> {

    private List<ReportCard> cards;

    public ReportCardAdapter(List<ReportCard> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(com.example.nutritrack.R.layout.item_report_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ReportCard card = cards.get(pos);
        h.title.setText(card.title);
        h.text.setText(card.text);

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, text;
        ImageView icon;

        public ViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvCardTitle);
            text = v.findViewById(R.id.tvCardText);
            icon = v.findViewById(R.id.imgIcon);
        }
    }
}
