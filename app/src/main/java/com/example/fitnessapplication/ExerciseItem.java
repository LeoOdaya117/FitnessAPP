package com.example.fitnessapplication;

public class ExerciseItem {
    private String ExerciseID;
    private String exerciseId;
    private String image;
    private String name;
    private String set;
    private String reps;
    private String duration;
    private String difficulty;

    public ExerciseItem( String exerciseId, String image, String name, String set, String reps, String duration, String difficulty) {
        this.exerciseId = exerciseId;
        this.image = image;
        this.name = name;
        this.set = set;
        this.reps = reps;
        this.duration = duration;
        this.difficulty = difficulty;
    }



    public String getExerciseId() {
        return exerciseId;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getSet() {
        return set;
    }

    public String getReps() {
        return reps;
    }

    public String getDuration() {
        return duration;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
