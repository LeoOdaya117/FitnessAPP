package com.example.fitnessapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataManager {
    private static final String PREF_NAME = "UserData";
    private SharedPreferences sharedPreferences;

    private static UserDataManager instance;

    private UserDataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserDataManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveGender(String gender) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gender", gender);
        editor.apply();
    }

    public String getGender() {
        return sharedPreferences.getString("gender", "");
    }

    public void saveAge(int age) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("age", age);
        editor.apply();
    }

    public int getAge() {
        return sharedPreferences.getInt("age", 0);
    }

    public void saveTargetWeight(float targetWeight) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("targetWeight", targetWeight);
        editor.apply();
    }

    public float getTargetWeight() {
        return sharedPreferences.getFloat("targetWeight", 0);
    }

    public void saveWeight(float weight) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("weight", weight);
        editor.apply();
    }

    public float getWeight() {
        return sharedPreferences.getFloat("weight", 0);
    }

    public void saveHeight(float height) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("height", height);
        editor.apply();
    }

    public float getHeight() {
        return sharedPreferences.getFloat("height", 0);
    }

    public void saveGoal(String goal) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("goal", goal);
        editor.apply();
    }

    public String getGoal() {
        return sharedPreferences.getString("goal", "");
    }

    public void saveDiet(String diet) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("diet", diet);
        editor.apply();
    }

    public String getSDiet() {
        return sharedPreferences.getString("diet", "");
    }

    public void saveBMR(float bmr) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("bmr", bmr);
        editor.apply();
    }

    public float getSBMR() {
        return sharedPreferences.getFloat("bmr", 0);
    }

    public void saveFL(String fitnesslevel) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fitnesslevel", fitnesslevel);
        editor.apply();
    }

    public String getFL() {
        return sharedPreferences.getString("fitnesslevel", "");
    }

    public void saveEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public String getEmail() {
        return sharedPreferences.getString("email", "");
    }

    public void savePassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", password);
        editor.apply();
    }

    public String getPassword() {
        return sharedPreferences.getString("password", "");
    }
    public void saveWorkoutPlan(String[] workoutPlan) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < workoutPlan.length; i++) {
            editor.putString("workout_" + i, workoutPlan[i]);
        }
        editor.apply();
    }

    public String[] getWorkoutPlan() {
        String[] workoutPlan = new String[7];
        for (int i = 0; i < workoutPlan.length; i++) {
            workoutPlan[i] = sharedPreferences.getString("workout_" + i, "");
        }
        return workoutPlan;
    }

    public void saveWorkoutPlanId(String workoutPlanId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("workoutPlanId", workoutPlanId);
        editor.apply();
    }

    public String getWorkoutPlanId() {
        return sharedPreferences.getString("workoutPlanId", "");
    }

    public void saveWorkoutPlanDay(String workoutPlanDay) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("workoutPlanDay", workoutPlanDay);
        editor.apply();
    }

    public String getWorkoutPlanDay() {
        return sharedPreferences.getString("workoutPlanDay", "");
    }

    public void saveDietPlanId(String dietPlanId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dietPlanId", dietPlanId);
        editor.apply();
    }

    public String getDietPlanId() {
        return sharedPreferences.getString("dietPlanId", "");
    }

    public void saveDietPlanDay(String dietPlanDay) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dietPlanDay", dietPlanDay);
        editor.apply();
    }

    public String getDietPlanDay() {
        return sharedPreferences.getString("dietPlanDay", "");
    }

    public void saveWorkoutType(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.apply();
    }

    public String getWorkoutType() {
        return sharedPreferences.getString("name", "");
    }

    public void saveAllergy(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.apply();
    }

    public String getAllergy() {
        return sharedPreferences.getString("name", "");
    }


}

