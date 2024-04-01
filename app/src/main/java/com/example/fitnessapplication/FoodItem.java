package com.example.fitnessapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FoodItem {
    private int id;
    private String name;
    private String serving;
    private String imageUrl;
    private String mealType;

    public FoodItem(int id, String name, String serving, String imageUrl, String mealType) {
        this.id = id;
        this.name = name;
        this.serving = serving;
        this.imageUrl = imageUrl;
        this.mealType = mealType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getServing() {
        return serving;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMealType() {
        return mealType;
    }


}



