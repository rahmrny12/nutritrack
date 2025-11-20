package com.example.nutritrack.ui.log_food;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.MealModel;
import com.example.nutritrack.ui.MealAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FoodMyMealsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;

    private FirebaseUser user;
    private DatabaseReference ref;
    private List<MealModel> mealList = new ArrayList<>();
    private MealAdapter adapter;

    public FoodMyMealsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_my_meals, container, false);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerMyMeals);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MealAdapter(mealList, meal -> {
            LogFoodFragment parent = (LogFoodFragment) getParentFragment();
            if (parent != null) {
                parent.saveToDiary(meal, "meal");
            }
        });
        recyclerView.setAdapter(adapter);

        loadData();

        // Swipe-to-refresh listener
        swipeRefresh.setOnRefreshListener(this::loadData);

        return view;
    }

    private void loadData() {
        swipeRefresh.setRefreshing(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            android.util.Log.e("DEBUG_MYMEALS", "User is NULL");
            swipeRefresh.setRefreshing(false);
            return;
        }

        String uid = user.getUid();
        android.util.Log.d("DEBUG_MYMEALS", "UID = " + uid);

        ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("meals");

        android.util.Log.d("DEBUG_MYMEALS", "Path: users/" + uid + "/meals");

        ref.get().addOnSuccessListener(snapshot -> {
            android.util.Log.d("DEBUG_MYMEALS", "Snapshot exists: " + snapshot.exists());
            android.util.Log.d("DEBUG_MYMEALS", "Children count: " + snapshot.getChildrenCount());

            mealList.clear();

            for (DataSnapshot ds : snapshot.getChildren()) {
                MealModel model = ds.getValue(MealModel.class);

                android.util.Log.d("DEBUG_MYMEALS", "Meal: " + ds.getKey());

                if (model != null) {
                    mealList.add(model);
                } else {
                    android.util.Log.e("DEBUG_MYMEALS", "Model NULL for key: " + ds.getKey());
                }
            }

            adapter.notifyDataSetChanged();
            android.util.Log.d("DEBUG_MYMEALS", "Adapter notified. Total items: " + mealList.size());

            swipeRefresh.setRefreshing(false);
        }).addOnFailureListener(e -> {
            android.util.Log.e("DEBUG_MYMEALS", "Firebase ERROR", e);
            swipeRefresh.setRefreshing(false);
        });
    }
}
