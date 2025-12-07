package com.example.nutritrack.ui.log_food;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nutritrack.MealDetailDialog;
import com.example.nutritrack.R;
import com.example.nutritrack.data.model.MealModel;
import com.example.nutritrack.data.service.MealApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import com.example.nutritrack.ui.MealAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodMyMealsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private List<MealModel> mealList = new ArrayList<>();
    private ActivityResultLauncher<Intent> mealDetailLauncher;
    private OnMealUpdatedListener mealUpdateListener;

    public interface OnMealUpdatedListener {
        void onMealUpdated();
    }

    public void setOnMealUpdatedListener(OnMealUpdatedListener listener) {
        this.mealUpdateListener = listener;
    }

    public FoodMyMealsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_my_meals, container, false);

        mealDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (mealUpdateListener != null) {
                            mealUpdateListener.onMealUpdated();  // Notify parent
                        }
                    }
                }
        );


        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerMyMeals);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MealAdapter(mealList, new MealAdapter.OnMealClick() {
            @Override
            public void onAdd(MealModel meal) {
                LogFoodFragment parent = (LogFoodFragment) getParentFragment();
                if (parent != null) parent.saveToDiary(meal, "meal");
            }

            @Override
            public void onTap(MealModel meal) {
                MealDetailDialog dialog = new MealDetailDialog(meal);
                dialog.show(getActivity().getSupportFragmentManager(), "mealDetail");
                getParentFragmentManager().setFragmentResultListener(
                        "meal_added", getActivity(), (key, bundle) -> {

                            boolean refresh = bundle.getBoolean("refresh");

                            if (refresh) {
                                if (mealUpdateListener != null) {
                                    mealUpdateListener.onMealUpdated();  // Notify parent
                                }
                            };
                        });
            }
        });


        recyclerView.setAdapter(adapter);

        loadMeals();
        swipeRefresh.setOnRefreshListener(this::loadMeals);

        return view;
    }


    private void loadMeals() {
        swipeRefresh.setRefreshing(true);

        MealApiService api = RetrofitClient.getInstance().create(MealApiService.class);
        Call<List<MealModel>> call = api.getMeals();

        call.enqueue(new Callback<List<MealModel>>() {
            @Override
            public void onResponse(Call<List<MealModel>> call, Response<List<MealModel>> response) {
                swipeRefresh.setRefreshing(false);

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("MEALS_API", "Failed: " + response.code());
                    return;
                }

                mealList.clear();
                mealList.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<MealModel>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Log.e("MEALS_API_ERROR", t.getMessage(), t);
            }
        });
    }
}
