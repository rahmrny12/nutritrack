package com.example.nutritrack.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.nutritrack.ForgotPasswordActivity;
import com.example.nutritrack.R;
import com.example.nutritrack.ui.home.HomeFragment;
import com.example.nutritrack.ui.login.LoginActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvAddress;
    private Button btnReset, btnLogout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE);

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnReset = view.findViewById(R.id.btnForgotPassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Ambil data dari SharedPreferences
        String name = sharedPref.getString("name", "-");
        String email = sharedPref.getString("email", "-");
        String address = sharedPref.getString("address", "-");

        tvName.setText("Nama: " + name);
        tvEmail.setText("Email: " + email);
        tvAddress.setText("Alamat: " + address);

        btnReset.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Hapus data login
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("isLoggedIn"); // hanya hapus status login
            editor.apply();

            // Arahkan ke halaman Login
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Tutup activity sekarang
            requireActivity().finish();
        });

        return view;
    }
}
