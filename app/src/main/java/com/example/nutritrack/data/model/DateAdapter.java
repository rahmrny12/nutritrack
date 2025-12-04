package com.example.nutritrack.data.model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nutritrack.R;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private List<DateModel> dateList;
    private Context context;
    private int selectedPosition = -1;
    private OnDateClickListener listener;

    public DateAdapter(Context context, List<DateModel> dateList, OnDateClickListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }

    public interface OnDateClickListener {
        void onDateClick(String fullDate);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DateModel model = dateList.get(position);
        holder.txtDay.setText(model.dayName);
        holder.txtNumber.setText(String.valueOf(model.dayNumber));

        if (model.isSelected) {
            holder.card.setCardBackgroundColor(Color.parseColor("#E5F6F0"));
            holder.txtDay.setTextColor(Color.parseColor("#00695C"));
            holder.txtNumber.setTextColor(Color.parseColor("#00695C"));
        } else {
            holder.card.setCardBackgroundColor(Color.TRANSPARENT);
            holder.txtDay.setTextColor(Color.parseColor("#777777"));
            holder.txtNumber.setTextColor(Color.parseColor("#004D40"));
        }

        holder.itemView.setOnClickListener(v -> {

            for (DateModel d : dateList) d.isSelected = false;
            model.isSelected = true;
            notifyDataSetChanged();

            if (listener != null) {
                listener.onDateClick(model.fullDate); // SEND SELECTED DATE
            }
        });

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDay, txtNumber;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            card = itemView.findViewById(R.id.cardDate);
        }
    }
}
