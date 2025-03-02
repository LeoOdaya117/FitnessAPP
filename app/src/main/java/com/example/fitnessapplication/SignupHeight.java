package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupHeight extends AppCompatActivity {

    public double targetcal =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_height);

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
                EditText currentHeightEditText = findViewById(R.id.currentHeightEditText);

                // Get text from EditText fields
                String currentheightText = currentHeightEditText.getText().toString().trim();

                // Parse the text to float
                float currentHeight = 0;

                try {
                    currentHeight = Float.parseFloat(currentheightText);
                } catch (NumberFormatException e) {
                    // Handle parsing error
                    e.printStackTrace();
                }




                if(currentheightText == null || TextUtils.isEmpty(currentheightText) ){
                    showalert();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(SignupHeight.this);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

                builder.setView(dialogView);

                TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
                TextView messageTextView = dialogView.findViewById(R.id.bmr_message);
                Button okButton = dialogView.findViewById(R.id.ok_button);

                // Calculate BMI
                // Calculate BMI
                double bmi = bmi(currentHeight);




                float floatValue = (float)  bmr();

                UserDataManager.getInstance(SignupHeight.this).saveBMR(floatValue);
// Determine fitness category and recommendation
                String fitnessCategory;
                String fitnessGoal = "";
                if (bmi < 18.5) {
                    fitnessCategory = "Underweight";
                    fitnessGoal = "To improve your health, consider a fitness goal of building muscle mass and gaining weight.";
                } else if (bmi < 25) {
                    fitnessCategory = "Normal weight";
                    fitnessGoal = "Maintaining your current weight is important. Consider a fitness goal of maintaining a balanced diet and regular exercise.";
                } else if (bmi < 30) {
                    fitnessCategory = "Overweight";
                    fitnessGoal = "To achieve a healthy weight, consider a fitness goal of losing weight through a combination of diet and exercise.";
                } else {
                    fitnessCategory = "Obese";
                    fitnessGoal = "To reduce health risks associated with obesity, consider a fitness goal of significant weight loss through lifestyle changes, including diet and exercise.";
                }

// Display BMI, fitness category, and fitness goal recommendation
                titleTextView.setText("Your Basal Mass Index (BMI)");
                messageTextView.setText("Your BMI is: " + bmi + "\nBMR: "+floatValue+"\nFitness Category: " + fitnessCategory + "\n\n" + fitnessGoal);

                AlertDialog dialog = builder.create();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); // Dismiss the dialog when the OK button is clicked

                        // Create an Intent to start the next activity
                        Intent intent = new Intent(SignupHeight.this, signup1.class);
                        // Put the selected card ID as an extra to the intent

                        // Start the activity
                        startActivity(intent);
                    }
                });

                dialog.show();
                UserDataManager.getInstance(SignupHeight.this).saveHeight(currentHeight);


            }
        });
    }

    public void showalert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupHeight.this);
        builder.setTitle("Warning");
        builder.setMessage("Please fill out the Height first.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    public double bmi(float heightCm) {
        // Get weight in kilograms and height in centimeters
        float weightKg = UserDataManager.getInstance(SignupHeight.this).getWeight();

        // Convert height from centimeters to meters
        double heightM = heightCm / 100.0;

        // Calculate BMI
        double bmi = weightKg / (heightM * heightM);

        return  bmi = Math.round(bmi * 100.0) / 100.0;
    }

    public double bmr()
    {
        String goal = UserDataManager.getInstance(SignupHeight.this).getGoal();
        String gender = UserDataManager.getInstance(SignupHeight.this).getGender();
        int age = UserDataManager.getInstance(SignupHeight.this).getAge();
        float weight = UserDataManager.getInstance(SignupHeight.this).getWeight();
        float height = UserDataManager.getInstance(SignupHeight.this).getHeight();


        if (age == 0 || gender == null || weight == 0 || height == 0) {
//            showMissingDataDialog();

        }
        // Calculate BMR based on Harris-Benedict equation
        double bmr;
        if (gender.equalsIgnoreCase("Male")) { // Male
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else { // Female
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }


        double targetCalories = bmr;

        targetCalories = Math.round(targetCalories * 100.0) / 100.0;

        if(goal.equals("Loss Weight")){
            targetCalories = targetCalories - 500;
        } else if (goal.equals("Gain Weight")) {
            targetCalories = targetCalories + 500;
        }

        return targetCalories;

    }

}