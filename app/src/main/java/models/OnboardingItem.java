package com.focusbloom.app.models;

public class OnboardingItem {
    private String title;
    private String description;
    private int iconResId;
    private int colorResId;

    public OnboardingItem(String title, String description, int iconResId, int colorResId) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.colorResId = colorResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getColorResId() {
        return colorResId;
    }
}