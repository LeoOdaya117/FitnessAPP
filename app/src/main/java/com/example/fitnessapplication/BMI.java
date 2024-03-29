package com.example.fitnessapplication;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class BMI extends AppCompatActivity {

    private EditText editTextWeight, editTextHeight;
    private TextView textViewResult;
    private  ImageView backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        editTextWeight = findViewById(R.id.edit_text_weight);
        editTextHeight = findViewById(R.id.edit_text_height);
        backbutton = findViewById(R.id.bmi_back_button);

        Button calculateButton = findViewById(R.id.button_calculate);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }

        });



        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        ImageView details = findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BMI.this, About.class);
                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

    }

    private void calculateBMI() {
        String weightStr = editTextWeight.getText().toString();
        String heightStr = editTextHeight.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(BMI.this, "Please enter weight and height", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weightKg = Double.parseDouble(weightStr);
            double heightCm = Double.parseDouble(heightStr);

            double bmi = calculateBMIValue(weightKg, heightCm);

            AlertDialog.Builder builder = new AlertDialog.Builder(BMI.this);

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

            builder.setView(dialogView);

            TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
            TextView messageTextView = dialogView.findViewById(R.id.bmr_message);
            Button okButton = dialogView.findViewById(R.id.ok_button);



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


                }
            });

            dialog.show();
        } catch (NumberFormatException e) {
            Toast.makeText(BMI.this, "Invalid input. Please enter valid numbers.", Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateBMIValue(double weightKg, double heightCm) {
        double heightM = heightCm / 100; // Convert cm to m
        double bmi = weightKg / (heightM * heightM);
        // Round BMI to two decimal places
        return Math.round(bmi * 100.0) / 100.0;
    }





}