package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Workout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // Retrieve the category name from the intent extras
        String category = getIntent().getStringExtra("category");

        // Now you have the category name and can use it as needed in your activity
        // For example, you can display it in a TextView
        TextView categoryTextView = findViewById(R.id.title);
        ImageView backImage = findViewById(R.id.actvityBackground);

        if (category.equals("Warm Up")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.warmupstwo));
        } else if (category.equals("Abs Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.abs));
        } else if (category.equals("Chest Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.chest));
        } else if (category.equals("Arm Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.arm));
        } else if (category.equals("Leg Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.leg));
        } else if (category.equals("Back Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.backphoto));
        } else if (category.equals("Abs Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.abs));
        } else if (category.equals("Chest Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.chest));
        } else if (category.equals("Arm Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.arm));
        } else if (category.equals("Leg Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.leg));
        } else if (category.equals("Back Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.backphoto));
        } else if (category.equals("Abs Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.abs));
        } else if (category.equals("Chest Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.chest));
        } else if (category.equals("Arm Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.arm));
        } else if (category.equals("Leg Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.leg));
        } else if (category.equals("Back Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.backphoto));
        }



        categoryTextView.setText(category.toUpperCase());
    }
}