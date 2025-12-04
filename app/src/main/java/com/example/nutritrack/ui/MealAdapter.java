package com.example.nutritrack.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nutritrack.R;
import com.example.nutritrack.data.model.MealModel;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private List<MealModel> list;
    private OnMealClick listener;

    public interface OnMealClick {
        void onAdd(MealModel meal);
        void onTap(MealModel meal);
    }

    public MealAdapter(List<MealModel> list, OnMealClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealModel m = list.get(position);

        holder.mealTitle.setText(m.getMealsName());
        holder.mealSubtitle.setText(m.getCalories() + " cal");

        // CLICK ADD ICON
        holder.btnAddMeal.setOnClickListener(v -> {
            listener.onAdd(m);
        });

        // ON TAP ITEM
        holder.itemView.setOnClickListener(v -> {
            listener.onTap(m);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mealTitle, mealSubtitle;
        ImageButton btnEditMeal, btnAddMeal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mealTitle = itemView.findViewById(R.id.mealTitle);
            mealSubtitle = itemView.findViewById(R.id.mealSubtitle);
            btnAddMeal = itemView.findViewById(R.id.btnAddMeal);
        }
    }
}
