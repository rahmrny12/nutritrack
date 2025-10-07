package com.example.nutritrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class BMIResultFragment extends Fragment {

    public BMIResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bmi_result, container, false);

        Button confirmBtn = view.findViewById(R.id.confirmBtn);

        confirmBtn.setOnClickListener(v -> {
            // Intent ke MainActivity
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);

            requireActivity().finish();
        });

        return view;
    }
}
