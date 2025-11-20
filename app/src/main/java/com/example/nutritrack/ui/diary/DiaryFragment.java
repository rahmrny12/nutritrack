package com.example.nutritrack.ui.diary;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nutritrack.R;
import com.example.nutritrack.data.model.DiaryAdapter;
import com.example.nutritrack.data.model.DiaryItem;
import com.example.nutritrack.data.model.MealModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.*;

public class DiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiaryAdapter adapter;

    private List<DiaryItem> itemList = new ArrayList<>();
    private Map<String, List<MealModel>> groupedMeals = new HashMap<>();

    public DiaryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerDiary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DiaryAdapter(requireContext(), itemList, groupedMeals);
        recyclerView.setAdapter(adapter);

        loadDiary();
    }

    private void loadDiary() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("diary")
                .child(today);

        ref.get().addOnSuccessListener(snapshot -> {

            itemList.clear();
            groupedMeals.clear();

            // HEADER always first
            itemList.add(new DiaryItem(DiaryItem.TYPE_HEADER));

            String[] categories = {"Breakfast", "Lunch", "Dinner"};

            for (String category : categories) {

                if (!snapshot.hasChild(category)) continue;

                // add collapsed category
                itemList.add(new DiaryItem(DiaryItem.TYPE_CATEGORY, category));

                List<MealModel> mealList = new ArrayList<>();

                for (DataSnapshot itemSnap : snapshot.child(category).getChildren()) {

                    String type = itemSnap.child("type").getValue(String.class);

                    if ("meal".equals(type)) {
                        MealModel meal = itemSnap.getValue(MealModel.class);
                        if (meal != null) mealList.add(meal);
                    }

                    if ("ingredient".equals(type)) {
                        MealModel ing = new MealModel();
                        ing.setName(itemSnap.child("name").getValue(String.class));
                        ing.setCalories(itemSnap.child("calories").getValue(Integer.class));
                        ing.setTime(itemSnap.child("time").getValue(String.class));
                        mealList.add(ing);
                    }
                }

                groupedMeals.put(category, mealList);
            }

            adapter.notifyDataSetChanged();
        });
    }
}
