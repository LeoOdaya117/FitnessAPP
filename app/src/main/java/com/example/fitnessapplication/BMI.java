package com.example.fitnessapplication;


import android.content.Intent;
import android.os.Bundle;
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
                // Check if weight or height fields are empty
                if (editTextWeight.getText().toString().isEmpty() || editTextHeight.getText().toString().isEmpty()) {
                    // Display a Toast message indicating that fields are empty
                    Toast.makeText(BMI.this, "Please enter weight and height", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform BMI calculation here
                    String result = calculateBMI();
                    // Show the result in the dialog
                    showResultDialog(result);
                }
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

    private String calculateBMI() {
        // Perform BMI calculation here
        // This is just a placeholder, replace it with your actual calculation logic
        String weightStr = editTextWeight.getText().toString();
        String heightStr = editTextHeight.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            return "Please enter weight and height";
        }

        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr) / 100; // Convert cm to m

        double bmi = weight / (height * height);

        // Format the result string to display both BMI and result
        return String.format("Your BMI is: %.2f\n%s", bmi, getBMIResult(bmi));
    }

    // Helper method to get BMI result interpretation
    private String getBMIResult(double bmi) {
        // You can define your own criteria here to interpret the BMI result
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }


    private void showResultDialog(String result) {
        BMIResultDialogFragment dialog = new BMIResultDialogFragment(result);
        dialog.show(getSupportFragmentManager(), "BMIResultDialogFragment");
    }




}