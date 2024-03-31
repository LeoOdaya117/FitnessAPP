package com.example.fitnessapplication;
public class Exercise {
    private String exerciseId;
    private String exerciseName;
    private String description;
    private String equipmentId;
    private String difficulty;
    private String imageUrl;

    // Attributes for food
    private String id;
    private String name;
    private String serving;
    private String photo;

    // Attributes for equipment
    private String equipmentID;
    private String equipmentName;
    private String image;

    // Constructor for exercise
    public Exercise(String exerciseId, String exerciseName, String description, String equipmentId, String difficulty, String imageUrl) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.description = description;
        this.equipmentId = equipmentId;
        this.difficulty = difficulty;
        this.imageUrl = imageUrl;
    }

    // Constructor for food
    public Exercise(String id, String name, String serving, String photo) {
        this.id = id;
        this.name = name;
        this.serving = serving;
        this.photo = photo;
    }

    // Constructor for equipment
    public Exercise(String equipmentID, String equipmentName, String image) {
        this.equipmentID = equipmentID;
        this.equipmentName = equipmentName;
        this.image = image;
    }

    // Getters and Setters for exercise attributes
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

    // Getters and Setters for food attributes
    public String getFoodID() {
        return id;
    }

    public String getFoodName() {
        return name;
    }

    public String getServing() {
        return serving;
    }

    public String getFoodImage() {
        return photo;
    }

    public void setDFoodId(String id) {
        this.id = id;
    }

    public void setFoodName(String name) {
        this.name = name;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }

    public void setFoodPhoto(String photo) {
        this.photo = photo;
    }

    // Getters and Setters for equipment attributes
    public String getEquipmentID() {
        return equipmentID;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getEquipmentImage() {
        return image;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public void setEquipmentImage(String image) {
        this.image = image;
    }
}
