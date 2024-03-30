package com.example.fitnessapplication;

public class Exercise {
    private String exerciseId;
    private String exerciseName;
    private String description;
    private String equipmentId;
    private String difficulty;
    private String imageUrl;

    // Constructor
    public Exercise(String exerciseId, String exerciseName, String description, String equipmentId, String difficulty, String imageUrl) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.description = description;
        this.equipmentId = equipmentId;
        this.difficulty = difficulty;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getExerciseId() {
        return exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getDescription() {
        return description;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
