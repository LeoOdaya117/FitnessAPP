package com.example.fitnessapplication;

public class WorkoutPlanExercise {
    private int workoutExerciseID;
    private String status;
    private String exerciseID;
    private String imageUrl;
    private String name;
    private String set;
    private String reps;
    private String duration;
    private String difficulty;

    public WorkoutPlanExercise(int workoutExerciseID, String status, String exerciseID, String imageUrl, String name, String set, String reps, String duration, String difficulty) {
        this.workoutExerciseID = workoutExerciseID;
        this.status = status;
        this.exerciseID = exerciseID;
        this.imageUrl = imageUrl;
        this.name = name;
        this.set = set;
        this.reps = reps;
        this.duration = duration;
        this.difficulty = difficulty;
    }

    public int getWorkoutExerciseID() {
        return workoutExerciseID;
    }

    public void setWorkoutExerciseID(int workoutExerciseID) {
        this.workoutExerciseID = workoutExerciseID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(String exerciseID) {
        this.exerciseID = exerciseID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
