package com.example.nutritrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.nutritrack.data.model.UpdateUserHealthResponse;
import com.example.nutritrack.data.model.HealthResponse;
import com.example.nutritrack.data.model.UserPreferences;
import com.example.nutritrack.data.service.HealthApiService;
import com.example.nutritrack.data.service.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;

public class AskAntropometriFragment extends Fragment {

    private EditText inputHeight, inputWeight, inputAge, inputWaist;
    private String userId;
    private Spinner spinnerGender;

    public AskAntropometriFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ask_antropometri, container, false);

        // SharedPrefs user ID
        userId = new UserPreferences(requireContext()).getUserId();

        // Find all fields
        inputHeight = view.findViewById(R.id.inputHeight);
        inputWeight = view.findViewById(R.id.inputWeight);
        inputAge    = view.findViewById(R.id.inputAge);
        inputWaist  = view.findViewById(R.id.inputWaist);

        spinnerGender = view.findViewById(R.id.spinnerGender);

// What user sees
        String[] genderDisplay = {"Laki-laki", "Perempuan"};

// What is actually sent to API
        String[] genderValues = {"male", "female"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genderDisplay
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);



        // Fetch Data and Auto Fill
        fetchAndFill(adapter, genderDisplay);

        // Continue Button
        Button nextBtn = view.findViewById(R.id.confirmBtn);
        nextBtn.setOnClickListener(v -> updateHealthToApi(genderDisplay, genderValues));

        return view;
    }


    /* ============================================================
       FETCH AND AUTO-FILL USER DATA
       ============================================================ */
    private void fetchAndFill(ArrayAdapter genderAdapter, String[] genderValues) {

        HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);

        api.getHealth(userId).enqueue(new Callback<HealthResponse>() {
            @Override
            public void onResponse(Call<HealthResponse> call, Response<HealthResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    return;
                }

                HealthResponse res = response.body();
                HealthResponse.User user = res.data.user;

                if (user.height != null) inputHeight.setText(String.valueOf(user.height));
                if (user.weight != null) inputWeight.setText(String.valueOf(user.weight));
                if (user.age != null) inputAge.setText(String.valueOf(user.age));
                if (user.waistSize != null) inputWaist.setText(String.valueOf(user.waistSize));
                if (user.gender != null) {
                    int position = -1;

                    for (int i = 0; i < genderValues.length; i++) {
                        if (genderValues[i].equalsIgnoreCase(user.gender)) {
                            position = i;
                            break;
                        }
                    }

                    if (position != -1) {
                        spinnerGender.setSelection(position);
                    }
                }


            }

            @Override
            public void onFailure(Call<HealthResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHealthToApi(String[] genderDisplay, String[] genderValues) {

        String height = inputHeight.getText().toString();
        String weight = inputWeight.getText().toString();
        String age    = inputAge.getText().toString();
        String waist  = inputWaist.getText().toString();
        String selectedDisplay = spinnerGender.getSelectedItem().toString();
        String gender = null;

// Match display text to API value
        for (int i = 0; i < genderDisplay.length; i++) {
            if (genderDisplay[i].equals(selectedDisplay)) {
                gender = genderValues[i];  // "male" or "female"
                break;
            }
        }


        if (height.isEmpty() || weight.isEmpty() || age.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HealthApiService api = RetrofitClient.getInstance().create(HealthApiService.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("id", userId);
        map.put("height", height);
        map.put("weight", weight);
        map.put("age", age);
        map.put("waist_size", waist);
        map.put("gender", gender);
        // gender bisa ambil dari sharedPrefs jika disimpan

        api.updateHealth(map).enqueue(new Callback<UpdateUserHealthResponse>() {
            @Override
            public void onResponse(Call<UpdateUserHealthResponse> call, Response<UpdateUserHealthResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Failed updating data", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Jika sukses → next
                goToNext();
            }

            @Override
            public void onFailure(Call<UpdateUserHealthResponse> call, Throwable t) {
                String msg = t.getMessage();

                Toast.makeText(getContext(), "Error: " + msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    /* ============================================================
       GO TO NEXT SCREEN
       ============================================================ */
    private void goToNext() {
        BMIResultFragment next = new BMIResultFragment();

        Bundle b = new Bundle();
        b.putString("user_id", userId);
        next.setArguments(b);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, next)
                .addToBackStack(null)
                .commit();
    }
}
