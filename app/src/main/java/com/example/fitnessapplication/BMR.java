package com.example.fitnessapplication;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class BMR extends AppCompatActivity {
    private ImageView backbutton;
    private EditText editTextAge, editTextWeight, editTextHeight;
    private RadioGroup radioGroupGender, radioGroupActivityLevel;
    private Button buttonCalculate;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmr);

        backbutton = findViewById(R.id.bmr_backbtn);
        editTextAge = findViewById(R.id.editTextAge);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextHeight = findViewById(R.id.editTextHeight);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioGroupActivityLevel = findViewById(R.id.radioGroupActivityLevel);
        buttonCalculate = findViewById(R.id.buttonCalculate);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMR();
            }
        });

        ImageView details = findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BMR.this, About.class);
                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });
    }

    private void calculateBMR() {
        // Retrieve input values
        int age = Integer.parseInt(editTextAge.getText().toString());
        double weight = Double.parseDouble(editTextWeight.getText().toString());
        double height = Double.parseDouble(editTextHeight.getText().toString());
        int genderId = radioGroupGender.getCheckedRadioButtonId();
        int activityLevelId = radioGroupActivityLevel.getCheckedRadioButtonId();

        // Calculate BMR based on formulas
        double bmr;
        if (genderId == R.id.radioButtonMale) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        // Adjust BMR based on activity level
        double activityMultiplier;
        if (activityLevelId == R.id.radioButtonLow) {
            activityMultiplier = 1.2;
        } else if (activityLevelId == R.id.radioButtonModerate) {
            activityMultiplier = 1.55;
        } else if (activityLevelId == R.id.radioButtonHigh) {
            activityMultiplier = 1.9;
        } else {
            activityMultiplier = 1.0; // Default to sedentary
        }

        bmr *= activityMultiplier;

        // Display result in a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("BMR Calculation Result");
        builder.setMessage(String.format("Your BMR is %.2f calories per day.", bmr));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, dialog will close
            }
        });
        builder.show();
    }
}
