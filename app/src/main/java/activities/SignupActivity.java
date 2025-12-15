package com.focusbloom.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.focusbloom.app.databinding.ActivitySignupBinding;
import com.focusbloom.app.utils.PreferenceManager;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        setupListeners();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSignup.setOnClickListener(v -> signupUser());

        binding.tvLogin.setOnClickListener(v -> finish());

        binding.btnTogglePassword.setOnClickListener(v -> {
            if (binding.etPassword.getInputType() == 129) {
                binding.etPassword.setInputType(1);
                binding.btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                binding.etPassword.setInputType(129);
                binding.btnTogglePassword.setImageResource(android.R.drawable.ic_secure);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });
    }

    private void signupUser() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.etName.setError("Name required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password required");
            return;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSignup.setEnabled(false);

        // Simulate signup
        new android.os.Handler().postDelayed(() -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSignup.setEnabled(true);

            preferenceManager.saveUserData(name, email);

            Toast.makeText(this, "Account created! Welcome, " + name + "! 🎉",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, OnboardingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500);
    }
}