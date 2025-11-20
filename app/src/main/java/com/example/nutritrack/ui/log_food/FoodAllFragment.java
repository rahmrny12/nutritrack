package com.example.nutritrack.ui.log_food;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nutritrack.R;
import com.example.nutritrack.data.adapter.FoodAdapter;
import com.example.nutritrack.data.model.FoodModel;
import com.example.nutritrack.data.model.IngredientAdapter;
import com.example.nutritrack.data.model.IngredientModel;
import com.example.nutritrack.data.service.FoodApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodAllFragment#} factory method to
 * create an instance of this fragment.
 */
public class FoodAllFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<FoodModel> foodList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_all, container, false);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerIngredients);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FoodAdapter(foodList, food -> {
            LogFoodFragment parent = (LogFoodFragment) getParentFragment();
            if (parent != null) {
                parent.saveToDiary(food, "food");
            }
        });

        recyclerView.setAdapter(adapter);

        loadFoods();

        swipeRefresh.setOnRefreshListener(this::loadFoods);

        return view;
    }

    private void loadFoods() {
        swipeRefresh.setRefreshing(true);

        FoodApiService api = RetrofitClient.getInstance().create(FoodApiService.class);
        Call<List<FoodModel>> call = api.getFoods();

        call.enqueue(new Callback<List<FoodModel>>() {
            @Override
            public void onResponse(Call<List<FoodModel>> call, Response<List<FoodModel>> response) {
                swipeRefresh.setRefreshing(false);

                if (!response.isSuccessful() || response.body() == null) return;

                foodList.clear();
                foodList.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<FoodModel>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
