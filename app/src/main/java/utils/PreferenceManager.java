package com.focusbloom.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "FocusBloomPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_HAS_SEEN_ONBOARDING = "hasSeenOnboarding";
    private static final String KEY_FOCUS_SESSIONS = "focusSessions";
    private static final String KEY_TOTAL_FOCUS_TIME = "totalFocusTime";
    private static final String KEY_CURRENT_STREAK = "currentStreak";
    private static final String KEY_BLOOM_PROGRESS = "bloomProgress";
    private static final String KEY_LAST_SESSION_DATE = "lastSessionDate";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    // User Authentication
    public void saveUserData(String name, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    // Onboarding
    public void setOnboardingSeen(boolean seen) {
        editor.putBoolean(KEY_HAS_SEEN_ONBOARDING, seen);
        editor.apply();
    }

    public boolean hasSeenOnboarding() {
        return preferences.getBoolean(KEY_HAS_SEEN_ONBOARDING, false);
    }

    // Focus Stats
    public void incrementFocusSessions() {
        int sessions = getFocusSessions();
        editor.putInt(KEY_FOCUS_SESSIONS, sessions + 1);
        editor.apply();
    }

    public int getFocusSessions() {
        return preferences.getInt(KEY_FOCUS_SESSIONS, 0);
    }

    public void addFocusTime(int minutes) {
        int totalTime = getTotalFocusTime();
        editor.putInt(KEY_TOTAL_FOCUS_TIME, totalTime + minutes);
        editor.apply();
    }

    public int getTotalFocusTime() {
        return preferences.getInt(KEY_TOTAL_FOCUS_TIME, 0);
    }

    public void updateStreak(String currentDate) {
        String lastDate = getLastSessionDate();

        if (!lastDate.equals(currentDate)) {
            // New day - increment streak
            int streak = getCurrentStreak();
            editor.putInt(KEY_CURRENT_STREAK, streak + 1);
            editor.putString(KEY_LAST_SESSION_DATE, currentDate);
            editor.apply();
        }
    }

    public int getCurrentStreak() {
        return preferences.getInt(KEY_CURRENT_STREAK, 0);
    }

    public String getLastSessionDate() {
        return preferences.getString(KEY_LAST_SESSION_DATE, "");
    }

    public void updateBloomProgress(float progress) {
        editor.putFloat(KEY_BLOOM_PROGRESS, Math.min(progress, 100f));
        editor.apply();
    }

    public float getBloomProgress() {
        return preferences.getFloat(KEY_BLOOM_PROGRESS, 0f);
    }

    public void resetBloomProgress() {
        editor.putFloat(KEY_BLOOM_PROGRESS, 0f);
        editor.apply();
    }
}