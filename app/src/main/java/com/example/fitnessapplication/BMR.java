package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

                Animation animation = AnimationUtils.loadAnimation(BMR.this, R.xml.button_animation);
                backbutton.startAnimation(animation);
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
        if (TextUtils.isEmpty(editTextAge.getText())
                || TextUtils.isEmpty(editTextWeight.getText())
                || TextUtils.isEmpty(editTextHeight.getText())
                || radioGroupGender.getCheckedRadioButtonId() == -1
                || radioGroupActivityLevel.getCheckedRadioButtonId() == -1) {
            // Display toast message indicating all fields are required
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return; // Exit the method
        }

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
        if (activityLevelId == R.id.Sedentary) {
            activityMultiplier = 1.2;
        } else if (activityLevelId == R.id.lightlyactive) {
            activityMultiplier = 1.375;
        } else if (activityLevelId == R.id.moderateactive) {
            activityMultiplier = 1.55;
        } else if (activityLevelId == R.id.VeryActive) {
            activityMultiplier = 1.725;
        } else if (activityLevelId == R.id.ExtraActive) {
            activityMultiplier = 1.9;
        } else {
            activityMultiplier = 1.0; // Default to sedentary
        }

        bmr *= activityMultiplier;

        // Display result in a dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BMR.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.bmr_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        titleTextView.setText("Your Basal Metabolic Rate (BMR)");
        messageTextView.setText("Your BMR is: " + bmr + " calories per day");

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Dismiss the dialog when the OK button is clicked

            }
        });

        dialog.show();
    }
}
