package com.focusbloom.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.focusbloom.app.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private boolean emailSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSendReset.setOnClickListener(v -> sendResetEmail());
        binding.btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void sendResetEmail() {
        String email = binding.etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Please enter your email");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSendReset.setEnabled(false);

        // Simulate sending email
        new android.os.Handler().postDelayed(() -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSendReset.setEnabled(true);

            emailSent = true;
            showSuccessView(email);

            Toast.makeText(this, "Reset link sent! Check your email.",
                    Toast.LENGTH_SHORT).show();
        }, 1500);
    }

    private void showSuccessView(String email) {
        binding.layoutForm.setVisibility(View.GONE);
        binding.layoutSuccess.setVisibility(View.VISIBLE);
        binding.tvEmailSent.setText("We've sent a password reset link to\n" + email);
    }
}