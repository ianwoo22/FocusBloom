package com.focusbloom.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.focusbloom.app.R;
import com.focusbloom.app.activities.LoginActivity;
import com.focusbloom.app.databinding.FragmentProfileBinding;
import com.focusbloom.app.utils.PreferenceManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceManager = new PreferenceManager(requireContext());

        loadUserProfile();
        loadStats();
        setupAchievements();
        setupLogoutButton();
    }

    private void loadUserProfile() {
        String name = preferenceManager.getUserName();
        String email = preferenceManager.getUserEmail();

        binding.tvUserName.setText(name);
        binding.tvUserEmail.setText(email);

        // Set initials
        String initials = getInitials(name);
        binding.tvInitials.setText(initials);
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    private void loadStats() {
        int sessions = preferenceManager.getFocusSessions();
        int totalTime = preferenceManager.getTotalFocusTime();
        int streak = preferenceManager.getCurrentStreak();

        binding.tvSessionsCount.setText(String.valueOf(sessions));
        binding.tvStreakCount.setText(String.valueOf(streak));
        binding.tvFocusTimeCount.setText((totalTime / 60) + "h");

        // Calculate achievements unlocked
        int achievements = calculateAchievements(sessions, totalTime, streak);
        binding.tvAchievementsCount.setText(String.valueOf(achievements));
    }

    private int calculateAchievements(int sessions, int totalTime, int streak) {
        int count = 1; // First Focus (always unlocked)

        if (streak >= 7) count++; // Week Warrior
        if (sessions >= 50) count++; // Focus Master
        if (totalTime >= 600) count++; // Time Lord
        if (sessions >= 100) count++; // Mindful Monk
        if (sessions >= 20) count++; // Bloom Expert

        return count;
    }

    private void setupAchievements() {
        int sessions = preferenceManager.getFocusSessions();
        int totalTime = preferenceManager.getTotalFocusTime();
        int streak = preferenceManager.getCurrentStreak();

        // Achievement cards (you can create custom views for these)
        updateAchievement(binding.achievement1, true); // First Focus - always unlocked
        updateAchievement(binding.achievement2, streak >= 7); // Week Warrior
        updateAchievement(binding.achievement3, sessions >= 50); // Focus Master
        updateAchievement(binding.achievement4, totalTime >= 600); // Time Lord
        updateAchievement(binding.achievement5, sessions >= 100); // Mindful Monk
        updateAchievement(binding.achievement6, sessions >= 20); // Bloom Expert
    }

    private void updateAchievement(View achievementView, boolean unlocked) {
        if (unlocked) {
            achievementView.setAlpha(1.0f);
            achievementView.setBackgroundResource(R.drawable.bg_achievement_unlocked);
        } else {
            achievementView.setAlpha(0.5f);
            achievementView.setBackgroundResource(R.drawable.bg_achievement_locked);
        }
    }

    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        preferenceManager.logout();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}