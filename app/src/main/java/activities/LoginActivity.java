package com.focusbloom.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.focusbloom.app.databinding.ActivityLoginBinding;
import com.focusbloom.app.utils.PreferenceManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        // Check if already logged in
        if (preferenceManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setupListeners();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> loginUser());

        binding.tvSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );

        binding.tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );

        binding.btnTogglePassword.setOnClickListener(v -> {
            if (binding.etPassword.getInputType() == 129) { // Password type
                binding.etPassword.setInputType(1); // Text type
                binding.btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                binding.etPassword.setInputType(129);
                binding.btnTogglePassword.setImageResource(android.R.drawable.ic_secure);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password required");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);

        // Simulate login (replace with actual authentication)
        new android.os.Handler().postDelayed(() -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);

            // Extract name from email
            String name = email.split("@")[0];
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            preferenceManager.saveUserData(name, email);

            Toast.makeText(this, "Welcome back, " + name + "! 🌸",
                    Toast.LENGTH_SHORT).show();

            navigateToMain();
        }, 1500);
    }

    private void navigateToMain() {
        if (preferenceManager.hasSeenOnboarding()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, OnboardingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }
}