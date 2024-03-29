package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupHeight extends AppCompatActivity {

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
                messageTextView.setText("Your BMI is: " + bmi + "\nFitness Category: " + fitnessCategory + "\n\n" + fitnessGoal);

                AlertDialog dialog = builder.create();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); // Dismiss the dialog when the OK button is clicked

                        // Create an Intent to start the next activity
                        Intent intent = new Intent(SignupHeight.this, SignupGoal.class);
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

    public double bmi(float heightCm) {
        // Get weight in kilograms and height in centimeters
        float weightKg = UserDataManager.getInstance(SignupHeight.this).getWeight();

        // Convert height from centimeters to meters
        double heightM = heightCm / 100.0;

        // Calculate BMI
        double bmi = weightKg / (heightM * heightM);

        return  bmi = Math.round(bmi * 100.0) / 100.0;
    }
}