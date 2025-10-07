package com.example.nutritrack.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nutritrack.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler handler = new Handler();
    private Runnable timeRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView tvName = binding.tvName;
        TextView tvTopTimestamp = binding.tvTopTimestamp;

        // Ambil nama user dari SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        String name = prefs.getString("name", "User");
        tvName.setText(name);

        // Update jam setiap detik
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                SimpleDateFormat timestampFormat = new SimpleDateFormat("EEEE, dd MMM yyyy | HH:mm", new Locale("id", "ID"));
                tvTopTimestamp.setText(timestampFormat.format(now));
                handler.postDelayed(this, 1000); // update tiap 1 detik
            }
        };
        handler.post(timeRunnable);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && timeRunnable != null) {
            handler.removeCallbacks(timeRunnable);
        }
        binding = null;
    }
}
