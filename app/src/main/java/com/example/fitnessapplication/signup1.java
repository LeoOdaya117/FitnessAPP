package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class signup1 extends AppCompatActivity {

    public String diet =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

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

                if(diet == null || TextUtils.isEmpty(diet)){
                    showalert();
                }else{
                    UserDataManager.getInstance(signup1.this).saveDiet(diet);
                    Intent intent = new Intent(signup1.this, SignupAllergy.class);
                    // Put the selected card ID as an extra to the intent

                    // Start the activity
                    startActivity(intent);
                }


            }
        });


    }

    public void showalert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(signup1.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Please choose your diet.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    public void onCardClicked(View view) {
        int viewId = view.getId();
        ImageView vegetarianCheck = findViewById(R.id.vegancheck);
        ImageView nonVegetarianCheck = findViewById(R.id.nonvegancheck);
        ImageView noneOfTheAboveCheck = findViewById(R.id.noneoftheabovecheck);

        if (viewId == R.id.vegetarianCard) {

            vegetarianCheck.setVisibility(View.VISIBLE);
            nonVegetarianCheck.setVisibility(View.INVISIBLE);
            noneOfTheAboveCheck.setVisibility(View.INVISIBLE);
            diet = "Vegetarian";
        } else if (viewId == R.id.nonVegetarianCard) {

            nonVegetarianCheck.setVisibility(View.VISIBLE);
            vegetarianCheck.setVisibility(View.INVISIBLE);
            noneOfTheAboveCheck.setVisibility(View.INVISIBLE);
            diet = "Non-Vegetarian";
        } else if (viewId == R.id.noneOfTheAboveCard) {
            noneOfTheAboveCheck.setVisibility(View.VISIBLE);
            nonVegetarianCheck.setVisibility(View.INVISIBLE);
            vegetarianCheck.setVisibility(View.INVISIBLE);
            diet = "None";
        }
        // Add more conditions for other cards if needed
    }


}
