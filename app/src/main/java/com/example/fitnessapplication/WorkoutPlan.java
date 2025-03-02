package com.example.fitnessapplication;
public class WorkoutPlan {
    private String title;
    private String details;
    private String id;
    private String date;

    public WorkoutPlan(String title, String details, String date) {
        this.title = title;
        this.details = details;
        this.date = date;

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
    public String getdate() {
        return date;
    }
}

