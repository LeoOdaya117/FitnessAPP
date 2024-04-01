package com.example.fitnessapplication;

public class DietPlan {
    private String title;
    private String details;
    private String id;

    public DietPlan(String title, String details) {
        this.title = title;
        this.details = details;

    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public String getID() {
        return id;
    }
}


