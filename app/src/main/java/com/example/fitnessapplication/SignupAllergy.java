package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class SignupAllergy extends AppCompatActivity {

    private CheckBox noneCheckBox, peanutCheckBox, milkCheckBox, shellfishCheckBox, fishCheckBox, glutenCheckBox, othersCheckBox;
    private EditText otherAllergiesEditText;
    private Button continueButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_allergy);

        // Initialize views
        peanutCheckBox = findViewById(R.id.peanutCheckBox);
        milkCheckBox = findViewById(R.id.milkCheckBox);
        shellfishCheckBox = findViewById(R.id.shellfishCheckBox);
        fishCheckBox = findViewById(R.id.fishCheckBox);
        glutenCheckBox = findViewById(R.id.glutenCheckBox);
        noneCheckBox = findViewById(R.id.noneCheck);
        othersCheckBox = findViewById(R.id.othersCheckBox);
        otherAllergiesEditText = findViewById(R.id.otherAllergiesEditText);
        continueButton = findViewById(R.id.continueButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Simulate back button press to return to the previous activity (Settings)
            }
        });
        // Set onClickListener for continueButton
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle continue button click
                saveAllergies();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle continue button click
                saveAllergies();
            }
        });


        // Set onCheckedChangeListener for othersCheckBox
        othersCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Toggle visibility of otherAllergiesEditText based on isChecked
                otherAllergiesEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        // Set onCheckedChangeListener for noneCheckBox

    }

    private void saveAllergies() {
        // Example method to save selected allergies
        StringBuilder selectedAllergies = new StringBuilder();

        if (peanutCheckBox.isChecked()) {
            selectedAllergies.append("Peanut, ");
        }
        if (milkCheckBox.isChecked()) {
            selectedAllergies.append("Milk, ");
        }
        if (shellfishCheckBox.isChecked()) {
            selectedAllergies.append("Shellfish, ");
        }
        if (fishCheckBox.isChecked()) {
            selectedAllergies.append("Fish, ");
        }
        if (glutenCheckBox.isChecked()) {
            selectedAllergies.append("Gluten, ");
        }
        if (noneCheckBox.isChecked()) {
            selectedAllergies.append("None");
            peanutCheckBox.setChecked(false);
            milkCheckBox.setChecked(false);
            shellfishCheckBox.setChecked(false);
            fishCheckBox.setChecked(false);
            glutenCheckBox.setChecked(false);
            othersCheckBox.setChecked(false);
        }
        if (othersCheckBox.isChecked()) {
            String otherAllergies = otherAllergiesEditText.getText().toString().trim();
            if (!otherAllergies.isEmpty()) {
                selectedAllergies.append(otherAllergies);
            }
        }

        // Display or save the selected allergies
        String allergiesList = selectedAllergies.toString();
        if (!allergiesList.isEmpty()) {
            // For example, show a toast with the selected allergies

            if(allergiesList.equals("None")){
                Toast.makeText(this, "Selected Allergies: " + allergiesList, Toast.LENGTH_LONG).show();
                allergiesList = "";

            }else{
                Toast.makeText(this, "Selected Allergies: " + allergiesList, Toast.LENGTH_LONG).show();

            }
            UserDataManager.getInstance(SignupAllergy.this).saveAllergy(allergiesList);

            // Create an Intent to start the next activity
            Intent intent = new Intent(SignupAllergy.this, SignupFitnessLevel.class);
            startActivity(intent);
        } else {
            // No allergies selected
            Toast.makeText(this, "Please select at least one allergy", Toast.LENGTH_SHORT).show();
        }

        // Here you can proceed with further actions like saving to database, etc.
    }
}
