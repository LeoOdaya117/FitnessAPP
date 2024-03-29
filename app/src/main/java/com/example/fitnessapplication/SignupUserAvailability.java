package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SignupUserAvailability extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user_availability);

        // Define your options
        String[] options = {"Core", "Legs", "Upper Body", "Cardio", "Lower Body", "Full Body", "Rest Day"};

// Find your spinners
        Spinner mondaySpinner = findViewById(R.id.mondaySpinner);
        Spinner tuesdaySpinner = findViewById(R.id.tuesdaySpinner);
        Spinner wednesdaySpinner = findViewById(R.id.wednesdaySpinner);
        Spinner thursdaySpinner = findViewById(R.id.thursdaySpinner);
        Spinner fridaySpinner = findViewById(R.id.fridaySpinner);
        Spinner saturdaySpinner = findViewById(R.id.saturdaySpinner);
        Spinner sundaySpinner = findViewById(R.id.sundaySpinner);

// Create an adapter for the spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Set the adapter to each spinner
        mondaySpinner.setAdapter(adapter);
        tuesdaySpinner.setAdapter(adapter);
        wednesdaySpinner.setAdapter(adapter);
        thursdaySpinner.setAdapter(adapter);
        fridaySpinner.setAdapter(adapter);
        saturdaySpinner.setAdapter(adapter);
        sundaySpinner.setAdapter(adapter);

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

                // Create an array to store the selected values
                String[] selectedValues = new String[7];
                selectedValues[0] = mondaySpinner.getSelectedItem().toString();
                selectedValues[1] = tuesdaySpinner.getSelectedItem().toString();
                selectedValues[2] = wednesdaySpinner.getSelectedItem().toString();
                selectedValues[3] = thursdaySpinner.getSelectedItem().toString();
                selectedValues[4] = fridaySpinner.getSelectedItem().toString();
                selectedValues[5] = saturdaySpinner.getSelectedItem().toString();
                selectedValues[6] = sundaySpinner.getSelectedItem().toString();

                // Save the workout plan
                UserDataManager.getInstance(SignupUserAvailability.this).saveWorkoutPlan(selectedValues);

                // Create a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupUserAvailability.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure all the data is correct?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Start the next activity
                        Intent intent = new Intent(SignupUserAvailability.this, signupFinish.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog if user cancels
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });


    }
}