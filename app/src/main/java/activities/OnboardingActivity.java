package com.focusbloom.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.focusbloom.app.R;
import com.focusbloom.app.adapters.OnboardingAdapter;
import com.focusbloom.app.databinding.ActivityOnboardingBinding;
import com.focusbloom.app.models.OnboardingItem;
import com.focusbloom.app.utils.PreferenceManager;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingAdapter adapter;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        setupViewPager();
        setupButtons();
    }

    private void setupViewPager() {
        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(
                "Welcome to FocusBloom",
                "Your personal digital well-being companion that helps you build healthier screen time habits",
                R.drawable.ic_flower,
                R.color.emerald_500
        ));
        items.add(new OnboardingItem(
                "Focus & Grow",
                "Set focus timers and watch your virtual flower bloom as you stay focused and productive",
                R.drawable.ic_target,
                R.color.purple_500
        ));
        items.add(new OnboardingItem(
                "Reflect & Learn",
                "Track your progress and reflect on your journey with daily reports and mindfulness prompts",
                R.drawable.ic_brain,
                R.color.blue_500
        ));
        items.add(new OnboardingItem(
                "Build Your Streak",
                "Earn achievements, maintain streaks, and celebrate your digital wellness milestones",
                R.drawable.ic_sparkles,
                R.color.orange_500
        ));

        adapter = new OnboardingAdapter(items);
        binding.viewPager.setAdapter(adapter);

        // Setup dots indicator
        new TabLayoutMediator(binding.dotsIndicator, binding.viewPager,
                (tab, position) -> {}).attach();

        // Handle page change
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateButtons(position);
            }
        });
    }

    private void setupButtons() {
        binding.btnSkip.setOnClickListener(v -> finishOnboarding());
        binding.btnNext.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                binding.viewPager.setCurrentItem(currentItem + 1);
            } else {
                finishOnboarding();
            }
        });
    }

    private void updateButtons(int position) {
        if (position == adapter.getItemCount() - 1) {
            binding.btnSkip.setVisibility(View.GONE);
            binding.btnNext.setText("Get Started");
        } else {
            binding.btnSkip.setVisibility(View.VISIBLE);
            binding.btnNext.setText("Next");
        }
    }

    private void finishOnboarding() {
        preferenceManager.setOnboardingSeen(true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}