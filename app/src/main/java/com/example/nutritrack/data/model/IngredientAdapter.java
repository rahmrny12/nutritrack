package com.example.nutritrack.data.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack.R;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<IngredientModel> list;
    private OnAddClickListener listener;

    public interface OnAddClickListener {
        void onAdd(IngredientModel item);
    }

    public IngredientAdapter(List<IngredientModel> list, OnAddClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        IngredientModel m = list.get(pos);

        h.tvName.setText(m.name);
        h.tvInfo.setText(m.calories + " cal, " + m.grams + " g");
    }

    @Override
    public int getItemCount() { return list.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;

        public ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.txtFoodName);
            tvInfo = v.findViewById(R.id.txtFoodInfo);
        }
    }
}
