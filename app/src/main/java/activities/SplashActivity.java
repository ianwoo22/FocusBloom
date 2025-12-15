package com.focusbloom.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.focusbloom.app.R;
import com.focusbloom.app.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            PreferenceManager preferenceManager = new PreferenceManager(this);

            Intent intent;
            if (preferenceManager.isLoggedIn()) {
                if (preferenceManager.hasSeenOnboarding()) {
                    intent = new Intent(this, MainActivity.class);
                } else {
                    intent = new Intent(this, OnboardingActivity.class);
                }
            } else {
                intent = new Intent(this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}