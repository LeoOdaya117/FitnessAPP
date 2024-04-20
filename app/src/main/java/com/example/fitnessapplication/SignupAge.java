package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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


                if(currentAgeText == null || TextUtils.isEmpty(currentAgeText)){
                    showalert();
                }else{
                    UserDataManager.getInstance(SignupAge.this).saveAge(currentAge);

                    // Create an Intent to start the next activity
                    Intent intent = new Intent(SignupAge.this, SignupWeight.class);
                    startActivity(intent);
                }

//
            }
        });
    }

    public void showalert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupAge.this);
        builder.setTitle("Warning");
        builder.setMessage("Please fill out the Age first.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}