package com.example.arpitapplication;

public class Practical8Model {
    private String title;
    private String subtitle;
    private String description;
    private String badgeLetter;
    private int badgeColor;

    public Practical8Model(String title, String subtitle, String description, String badgeLetter, int badgeColor) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.badgeLetter = badgeLetter;
        this.badgeColor = badgeColor;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDescription() {
        return description;
    }

    public String getBadgeLetter() {
        return badgeLetter;
    }

    public int getBadgeColor() {
        return badgeColor;
    }
}
