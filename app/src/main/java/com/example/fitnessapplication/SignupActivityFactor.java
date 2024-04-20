package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivityFactor extends AppCompatActivity {

    public double targetcal =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_factor);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Simulate back button press to return to the previous activity (Settings)
            }
        });

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(SignupActivityFactor.this, "BMR: " + targetcal +" kcal", Toast.LENGTH_SHORT).show();

                if(targetcal <= 0){
                    showalert();
                    return;
                }

                float floatValue = (float) targetcal; // Casting double to float

                UserDataManager.getInstance(SignupActivityFactor.this).saveBMR(floatValue);
//                showDataDialog(gender, age, targetweight, weight, height, goal);

                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivityFactor.this);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

                builder.setView(dialogView);

                TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
                TextView messageTextView = dialogView.findViewById(R.id.bmr_message);
                Button okButton = dialogView.findViewById(R.id.ok_button);

                titleTextView.setText("Your Basal Metabolic Rate (BMR)");
                messageTextView.setText("Your BMR is: " + targetcal + " calories per day");

                AlertDialog dialog = builder.create();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); // Dismiss the dialog when the OK button is clicked
                        Intent intent = new Intent(SignupActivityFactor.this, signup1.class);
                        startActivity(intent);
                    }
                });

                dialog.show();


            }
        });

    }

    public void showalert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivityFactor.this);
        builder.setTitle("Warning");
        builder.setMessage("Please choose your Activity level.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    public double bmr(double activityFactor)
    {
        String goal = UserDataManager.getInstance(SignupActivityFactor.this).getGoal();
        String gender = UserDataManager.getInstance(SignupActivityFactor.this).getGender();
        int age = UserDataManager.getInstance(SignupActivityFactor.this).getAge();
        float weight = UserDataManager.getInstance(SignupActivityFactor.this).getWeight();
        float height = UserDataManager.getInstance(SignupActivityFactor.this).getHeight();


        if (age == 0 || gender == null || weight == 0 || height == 0) {
            showMissingDataDialog();

        }
        // Calculate BMR based on Harris-Benedict equation
        double bmr;
        if (gender.equalsIgnoreCase("Male")) { // Male
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else { // Female
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }


        double targetCalories = bmr * activityFactor;

        targetCalories = Math.round(targetCalories * 100.0) / 100.0;

        if(goal.equals("Loss Weight")){
            targetCalories = targetCalories - 500;
        } else if (goal.equals("Gain Weight")) {
            targetCalories = targetCalories + 500;
        }

        return targetCalories;

    }

    public double bmr() {
        // Get weight in kilograms and height in centimeters
        float weightKg = UserDataManager.getInstance(SignupActivityFactor.this).getWeight();
        float heightCm = UserDataManager.getInstance(SignupActivityFactor.this).getHeight();

        // Convert height from centimeters to meters
        double heightM = heightCm / 100.0;

        // Calculate BMI
        double bmi = weightKg / (heightM * heightM);

        return bmi;
    }



    public void onCardClicked(View view) {
        int viewId = view.getId();
        ImageView sedentaryCheck = findViewById(R.id.sedentaryCheck);
        ImageView lightlyCheck = findViewById(R.id.lightlyCheck);
        ImageView ModeratelyCheck = findViewById(R.id.ModeratelyCheck);
        ImageView VeryCheck = findViewById(R.id.VeryCheck);
        ImageView extraCheck = findViewById(R.id.extraCheck);

        if (viewId == R.id.sedentarycard) {

            sedentaryCheck.setVisibility(View.VISIBLE);
            lightlyCheck.setVisibility(View.INVISIBLE);
            ModeratelyCheck.setVisibility(View.INVISIBLE);
            VeryCheck.setVisibility(View.INVISIBLE);
            extraCheck.setVisibility(View.INVISIBLE);
            targetcal = bmr(1.2);
        } else if (viewId == R.id.lightlycard) {

            sedentaryCheck.setVisibility(View.INVISIBLE);
            lightlyCheck.setVisibility(View.VISIBLE);
            ModeratelyCheck.setVisibility(View.INVISIBLE);
            VeryCheck.setVisibility(View.INVISIBLE);
            extraCheck.setVisibility(View.INVISIBLE);
            targetcal = bmr(1.375);
        } else if (viewId == R.id.Moderatelycard) {

            sedentaryCheck.setVisibility(View.INVISIBLE);
            lightlyCheck.setVisibility(View.INVISIBLE);
            ModeratelyCheck.setVisibility(View.VISIBLE);
            VeryCheck.setVisibility(View.INVISIBLE);
            extraCheck.setVisibility(View.INVISIBLE);
            targetcal = bmr(1.55);
        }else if (viewId == R.id.Verycard) {

            sedentaryCheck.setVisibility(View.INVISIBLE);
            lightlyCheck.setVisibility(View.INVISIBLE);
            ModeratelyCheck.setVisibility(View.INVISIBLE);
            VeryCheck.setVisibility(View.VISIBLE);
            extraCheck.setVisibility(View.INVISIBLE);
            targetcal = bmr(1.725);
        }else if (viewId == R.id.extracard) {

            sedentaryCheck.setVisibility(View.INVISIBLE);
            lightlyCheck.setVisibility(View.INVISIBLE);
            ModeratelyCheck.setVisibility(View.INVISIBLE);
            VeryCheck.setVisibility(View.INVISIBLE);
            extraCheck.setVisibility(View.VISIBLE);
            targetcal = bmr(1.9);
        }
        // Add more conditions for other cards if needed






    }
    public void showMissingDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Missing Data");
        builder.setMessage("Some required data is missing. Please provide all necessary information.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle OK button click if needed
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
