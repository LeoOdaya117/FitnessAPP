package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupAge extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_age);

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
                EditText currentAgeEditText = findViewById(R.id.currentAgeEditText);

                // Get text from EditText fields
                String currentAgeText = currentAgeEditText.getText().toString().trim();
                int currentAge = 0; // Default value in case parsing fails

                try {
                    currentAge = Integer.parseInt(currentAgeText);
                } catch (NumberFormatException e) {
                    // Handle parsing error, if needed
                    e.printStackTrace();
                }


//                Toast.makeText(SignupAge.this, "Current Age: " + currentAgeText +" years old", Toast.LENGTH_SHORT).show();
                UserDataManager.getInstance(SignupAge.this).saveAge(currentAge);

                // Create an Intent to start the next activity
                Intent intent = new Intent(SignupAge.this, SignupWeight.class);
                startActivity(intent);
            }
        });
    }
}