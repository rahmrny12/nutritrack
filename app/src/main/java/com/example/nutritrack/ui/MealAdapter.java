package com.example.nutritrack.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nutritrack.R;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private Context context;
    private List<Meal> mealList;

    public MealAdapter(Context context, List<Meal> mealList) {
        this.context = context;
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.mealName.setText(meal.getName());
//        holder.mealCalories.setText(meal.getCaloriesRange());
//        holder.mealImage.setImageResource(meal.getImageResId());
//        holder.mealImage2.setImageResource(meal.getImageResId());
        holder.addButton.setOnClickListener(v ->
                Toast.makeText(context, "Add " + meal.getName(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mealName, mealCalories;
        ImageView mealImage, mealImage2;
        ImageButton addButton;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.mealName);
            mealCalories = itemView.findViewById(R.id.mealCalories);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealImage2 = itemView.findViewById(R.id.mealImage2);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }
}
