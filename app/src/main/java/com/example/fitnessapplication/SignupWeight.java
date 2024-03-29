package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupWeight extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_weight);


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
                EditText currentWeightEditText = findViewById(R.id.currentWeightEditText);
                EditText targetWeightEditText = findViewById(R.id.targetWeightEditText);

                // Get text from EditText fields
                String currentWeightText = currentWeightEditText.getText().toString().trim();
                String targetWeightText = targetWeightEditText.getText().toString().trim();

                float currentWeight = 0.0f;
                float targetWeight = 0.0f;

                try {
                    currentWeight = Float.parseFloat(currentWeightText);
                } catch (NumberFormatException e) {
                    // Handle parsing error
                    e.printStackTrace();
                }

                try {
                    targetWeight = Float.parseFloat(targetWeightText);
                } catch (NumberFormatException e) {
                    // Handle parsing error
                    e.printStackTrace();
                }
//                Toast.makeText(SignupWeight.this, "Current Weight: " + currentWeight + " kg\nTarget Weight: " + targetWeight + " kg", Toast.LENGTH_SHORT).show();

                UserDataManager.getInstance(SignupWeight.this).saveTargetWeight(targetWeight);
                UserDataManager.getInstance(SignupWeight.this).saveWeight(currentWeight);

                // Create an Intent to start the next activity
                Intent intent = new Intent(SignupWeight.this, SignupHeight.class);

                // Start the activity
                startActivity(intent);
            }
        });
    }
}
