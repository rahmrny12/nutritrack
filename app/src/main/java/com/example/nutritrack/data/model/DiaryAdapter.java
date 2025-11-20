package com.example.nutritrack.data.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack.R;

import java.util.List;
import java.util.Map;

public class DiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiaryItem> items;
    private Context context;
    private Map<String, List<MealModel>> mealsByCategory;

    public DiaryAdapter(Context ctx, List<DiaryItem> items,
                        Map<String, List<MealModel>> mealsByCategory) {
        this.context = ctx;
        this.items = items;
        this.mealsByCategory = mealsByCategory;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == DiaryItem.TYPE_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_diary_header, parent, false));
        }

        if (viewType == DiaryItem.TYPE_CATEGORY) {
            return new CategoryViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_diary_category, parent, false));
        }

        return new MealViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_meal, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        DiaryItem item = items.get(position);

        if (holder instanceof HeaderViewHolder)
            ((HeaderViewHolder) holder).bind();

        else if (holder instanceof CategoryViewHolder)
            ((CategoryViewHolder) holder).bind(item.category, item.expanded);

        else if (holder instanceof MealViewHolder)
            ((MealViewHolder) holder).bind(item.meal);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // -------------------------------------------
    // Header ViewHolder
    // -------------------------------------------
    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvNeedToGain, tvCaloriesRemaining;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            tvNeedToGain = itemView.findViewById(R.id.tvNeedToGain);
            tvCaloriesRemaining = itemView.findViewById(R.id.tvCaloriesRemaining);
        }

        public void bind() {
            tvNeedToGain.setText("Still need to gain");
            tvCaloriesRemaining.setText("354 kcal");
        }
    }

    // -------------------------------------------
    // Category ViewHolder (Accordion Title)
    // -------------------------------------------
    class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory, tvCategoryCalories;
        ImageView ivArrow;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryCalories = itemView.findViewById(R.id.tvCategoryCalories); // ADD THIS
            ivArrow = itemView.findViewById(R.id.ivArrow);

            itemView.setOnClickListener(v -> toggleExpand(getAdapterPosition()));
        }

        public void bind(String category, boolean expanded) {
            tvCategory.setText(category);

            int calories = calculateTotalCalories(category);
            tvCategoryCalories.setText(calories + " kcal");

            // Rotate arrow
            ivArrow.setRotation(expanded ? 0 : -90);
        }
    }

    private int calculateTotalCalories(String category) {
        List<MealModel> meals = mealsByCategory.get(category);
        if (meals == null) return 0;

        int total = 0;
        for (MealModel m : meals) {
            total += m.getCalories();
        }
        return total;
    }

    // -------------------------------------------
    // Meal ViewHolder
    // -------------------------------------------
    class MealViewHolder extends RecyclerView.ViewHolder {

        TextView tvMealName, tvCalories;

        public MealViewHolder(View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.mealName);
            tvCalories = itemView.findViewById(R.id.mealCalories);
        }

        public void bind(MealModel meal) {
            if (meal == null) return;

            tvMealName.setText(meal.getName());
            tvCalories.setText(meal.getCalories() + " kcal");
        }
    }

    // -------------------------------------------
    // Accordion Logic
    // -------------------------------------------
    private void toggleExpand(int pos) {
        DiaryItem categoryItem = items.get(pos);
        categoryItem.expanded = !categoryItem.expanded;

        if (!categoryItem.expanded) {
            // Collapse → remove meals below this category
            int i = pos + 1;
            while (i < items.size() && items.get(i).type == DiaryItem.TYPE_MEAL) {
                items.remove(i);
            }
            notifyDataSetChanged();
            return;
        }

        // Expand → insert meals
        insertMeals(categoryItem.category, pos);
        notifyDataSetChanged();
    }

    private void insertMeals(String category, int pos) {
        List<MealModel> list = mealsByCategory.get(category);
        if (list == null) return;

        int index = pos + 1;

        for (MealModel m : list) {
            items.add(index, new DiaryItem(m));
            index++;
        }
    }
}
