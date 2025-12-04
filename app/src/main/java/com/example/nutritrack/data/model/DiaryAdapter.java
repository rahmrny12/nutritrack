package com.example.nutritrack.data.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack.R;

import java.util.List;
import java.util.Map;

public class DiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DiaryItem> items;
    private final Context context;
    private final Map<String, List<MealModel>> mealsByCategory;

    // Data from daily_goals.php
    private DailyGoalResponse.Data dailyGoalData;

    // Fallback summary (calculated from diary list)
    private int totalCalories, targetCalories;
    private int totalProtein, targetProtein;
    private int totalFat, targetFat;
    private int totalCarbs, targetCarbs;

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
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.item_diary_header, parent, false);
            return new HeaderViewHolder(v);
        }

        if (viewType == DiaryItem.TYPE_CATEGORY) {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.item_diary_category, parent, false);
            return new CategoryViewHolder(v);
        }

        if (viewType == DiaryItem.TYPE_MEAL_GROUP) {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.item_meal_group, parent, false);
            return new MealGroupViewHolder(v);
        }

        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        DiaryItem item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind();
        } else if (holder instanceof CategoryViewHolder) {
            ((CategoryViewHolder) holder).bind(item.category, item.expanded);
        } else if (holder instanceof MealGroupViewHolder) {
            ((MealGroupViewHolder) holder).bind(item.mealsList); // <-- NEW
        } else if (holder instanceof MealViewHolder) {
            ((MealViewHolder) holder).bind(item.meal);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    /* -------------------------------------------------------------
       PUBLIC API FROM FRAGMENT
       ------------------------------------------------------------- */

    public void setDailySummary(int totalCalories, int targetCalories,
                                int totalProtein, int targetProtein,
                                int totalFat, int targetFat,
                                int totalCarbs, int targetCarbs) {
        this.totalCalories = totalCalories;
        this.targetCalories = targetCalories;
        this.totalProtein = totalProtein;
        this.targetProtein = targetProtein;
        this.totalFat = totalFat;
        this.targetFat = targetFat;
        this.totalCarbs = totalCarbs;
        this.targetCarbs = targetCarbs;

        // header usually at position 0, but just in case:
        if (!items.isEmpty() && items.get(0).type == DiaryItem.TYPE_HEADER) {
            notifyItemChanged(0);
        } else {
            notifyDataSetChanged();
        }
    }

    public void setDailyGoal(DailyGoalResponse.Data data) {
        this.dailyGoalData = data;

        if (!items.isEmpty() && items.get(0).type == DiaryItem.TYPE_HEADER) {
            notifyItemChanged(0);
        } else {
            notifyDataSetChanged();
        }
    }

    /* -------------------------------------------------------------
       HEADER VIEW HOLDER
       ------------------------------------------------------------- */
    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvNeedToGain, tvCaloriesRemaining;
        com.example.nutritrack.ui.CircleShape circleProtein, circleFat, circleCarbs;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            tvNeedToGain = itemView.findViewById(R.id.tvNeedToGain);
            tvCaloriesRemaining = itemView.findViewById(R.id.tvCaloriesRemaining);

            circleProtein = itemView.findViewById(R.id.circleProtein);
            circleFat = itemView.findViewById(R.id.circleFat);
            circleCarbs = itemView.findViewById(R.id.circleCarbs);
        }

        public void bind() {

            // 1) PAKAI DATA DAILY GOAL DARI API JIKA LENGKAP
            if (dailyGoalData != null &&
                    dailyGoalData.goal != null &&
                    dailyGoalData.consumed != null &&
                    dailyGoalData.remaining != null) {

                DailyGoalResponse.Goal goal = dailyGoalData.goal;
                DailyGoalResponse.Consumed consumed = dailyGoalData.consumed;
                DailyGoalResponse.Remaining remaining = dailyGoalData.remaining;

                // Text kalori dari API (remaining)
                int remainingCalories = remaining.calories;
                tvNeedToGain.setText("Still need to gain");
                tvCaloriesRemaining.setText(remainingCalories + " kcal");

                // Percent makro dari API
                int proteinPercent = getPercent(consumed.protein, goal.proteinGoal);
                int fatPercent = getPercent(consumed.fat, goal.fatGoal);
                int carbsPercent = getPercent(consumed.carbs, goal.carbsGoal);

                circleProtein.setLabel("Protein");
                circleProtein.setPercentage(proteinPercent);

                circleFat.setLabel("Fat");
                circleFat.setPercentage(fatPercent);

                circleCarbs.setLabel("Carbs");
                circleCarbs.setPercentage(carbsPercent);
                return;
            }

            // 2) FALLBACK: PAKAI DATA YANG DIHITUNG SENDIRI (setDailySummary)
            tvNeedToGain.setText("Still need to gain");

            int remaining = Math.max(targetCalories - totalCalories, 0);
            tvCaloriesRemaining.setText(remaining + " kcal");

            int proteinPercent = getPercent(totalProtein, targetProtein);
            int fatPercent = getPercent(totalFat, targetFat);
            int carbsPercent = getPercent(totalCarbs, targetCarbs);

            circleProtein.setLabel("Protein");
            circleProtein.setPercentage(proteinPercent);

            circleFat.setLabel("Fat");
            circleFat.setPercentage(fatPercent);

            circleCarbs.setLabel("Carbs");
            circleCarbs.setPercentage(carbsPercent);
        }
    }

    // helper di dalam DiaryAdapter
    private int getPercent(int total, int target) {
        if (target <= 0) return 0;
        int p = (int) (100f * total / target);
        if (p > 100) p = 100;
        if (p < 0) p = 0;
        return p;
    }

    /* -------------------------------------------------------------
       CATEGORY VIEW HOLDER (Accordion Title)
       ------------------------------------------------------------- */
    class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory, tvCategoryCalories;
        ImageView ivArrow;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryCalories = itemView.findViewById(R.id.tvCategoryCalories);
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

    /* -------------------------------------------------------------
       MEAL VIEW HOLDER
       ------------------------------------------------------------- */
    class MealViewHolder extends RecyclerView.ViewHolder {

        ImageView imgMeal;
        TextView mealName, mealNutrition, mealCalories;

        public MealViewHolder(View itemView) {
            super(itemView);

            imgMeal = itemView.findViewById(R.id.imgMeal);
            mealName = itemView.findViewById(R.id.mealName);
            mealNutrition = itemView.findViewById(R.id.mealNutrition);
            mealCalories = itemView.findViewById(R.id.mealCalories);
        }

        public void bind(MealModel meal) {

            // Name
            mealName.setText(meal.getMealsName());

            // Nutrition
            String macro = "Carbs " + meal.getCarbs() + "g • Protein " + meal.getProtein() + "g • Fat " + meal.getFat() + "g";
            mealNutrition.setText(macro);

            // Calories
            mealCalories.setText(((int) meal.getCalories()) + " kcal");

            // Placeholder image (or replace with real)
            imgMeal.setImageResource(R.drawable.ic_food_placeholder);
        }
    }


    /* -------------------------------------------------------------
       ACCORDION LOGIC
       ------------------------------------------------------------- */
    private void toggleExpand(int pos) {
        DiaryItem categoryItem = items.get(pos);
        categoryItem.expanded = !categoryItem.expanded;

        if (!categoryItem.expanded) {
            // Collapse → remove group item below this category (if exists)
            int nextPos = pos + 1;
            if (nextPos < items.size() &&
                    items.get(nextPos).type == DiaryItem.TYPE_MEAL_GROUP) {
                items.remove(nextPos);
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
        if (list == null || list.isEmpty()) return;

        // Insert ONE group item right after the category row
        items.add(pos + 1, new DiaryItem(list));
    }


    class MealGroupViewHolder extends RecyclerView.ViewHolder {

        LinearLayout containerMeals;

        public MealGroupViewHolder(View itemView) {
            super(itemView);
            containerMeals = itemView.findViewById(R.id.containerMeals);
        }

        public void bind(List<MealModel> meals) {

            containerMeals.removeAllViews();

            LayoutInflater inflater = LayoutInflater.from(context);

            for (MealModel m : meals) {
                View row = inflater.inflate(R.layout.item_meal, containerMeals, false);

                ImageView imgMeal = row.findViewById(R.id.imgMeal);
                TextView mealName = row.findViewById(R.id.mealName);
                TextView mealNutrition = row.findViewById(R.id.mealNutrition);
                TextView mealCalories = row.findViewById(R.id.mealCalories);

                mealName.setText(m.getMealsName());
                mealNutrition.setText("Carbs " + m.getCarbs() + "g • Protein " + m.getProtein() + "g • Fat " + m.getFat() + "g");
                mealCalories.setText(((int) m.getCalories()) + " kcal");

                imgMeal.setImageResource(R.drawable.ic_food_placeholder);

                containerMeals.addView(row);
            }
        }
    }


}
