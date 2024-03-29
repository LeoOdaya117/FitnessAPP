package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SignupFitnessLevel extends AppCompatActivity {

    public String fitnesslevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_fitness_level);

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


                UserDataManager.getInstance(SignupFitnessLevel.this).saveFL(fitnesslevel);

                Intent intent = new Intent(SignupFitnessLevel.this, SignupUserAvailability.class);
                // Put the selected card ID as an extra to the intent

                // Start the activity
                startActivity(intent);



            }
        });
    }
    public void onCardClicked(View view) {
        int viewId = view.getId();

        ImageView beginnerCheck = findViewById(R.id.beginnerCheck);
        ImageView intermediateCheck = findViewById(R.id.intermediateCheck);
        ImageView advancedCheck = findViewById(R.id.advancedCheck);


        if (viewId == R.id.beginnercard) {

            beginnerCheck.setVisibility(View.VISIBLE);
            intermediateCheck.setVisibility(View.INVISIBLE);
            advancedCheck.setVisibility(View.INVISIBLE);

            fitnesslevel = "BEGINNER";
        } else if (viewId == R.id.intermediatecard) {

            beginnerCheck.setVisibility(View.INVISIBLE);
            intermediateCheck.setVisibility(View.VISIBLE);
            advancedCheck.setVisibility(View.INVISIBLE);

            fitnesslevel = "INTERMEDIATE";
        } else if (viewId == R.id.advancedcard) {

            beginnerCheck.setVisibility(View.INVISIBLE);
            intermediateCheck.setVisibility(View.INVISIBLE);
            advancedCheck.setVisibility(View.VISIBLE);

            fitnesslevel = "ADVANCED";
        }
        // Add more conditions for other cards if needed






    }

}