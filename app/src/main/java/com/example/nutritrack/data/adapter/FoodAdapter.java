package com.example.nutritrack.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.FoodModel;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> implements Filterable {

    private List<FoodModel> foodList;     // list yang tampil
    public List<FoodModel> fullList;      // list asli (untuk search)
    private OnFoodClickListener listener;

    public interface OnFoodClickListener {
        void onFoodClick(FoodModel food);
    }

    public FoodAdapter(List<FoodModel> foodList, OnFoodClickListener listener) {
        this.foodList = foodList;
        this.fullList = new ArrayList<>(foodList);  // simpan list lengkap
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodModel food = foodList.get(position);

        holder.name.setText(food.getFoodsName());
        holder.calories.setText(food.getCaloriesPerUnit() + " kcal");

        holder.itemView.setOnClickListener(v -> listener.onFoodClick(food));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // ======================
    //   FILTER IMPLEMENTATION
    // ======================
    @Override
    public Filter getFilter() {
        return foodFilter;
    }

    private final Filter foodFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FoodModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullList);  // tampilkan semua
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (FoodModel item : fullList) {
                    if (item.getFoodsName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            foodList.clear();
            foodList.addAll((List<FoodModel>) results.values);
            notifyDataSetChanged();
        }
    };

    // ======================
    // ViewHolder
    // ======================
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, calories;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtFoodName);
            calories = itemView.findViewById(R.id.txtFoodInfo);
        }
    }
}
