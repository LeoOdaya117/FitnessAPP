package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SignupGender extends AppCompatActivity {

    public String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_gender);

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

                if(gender == null || TextUtils.isEmpty(gender)){
                    showalert();
                }else{
                    //                Toast.makeText(SignupGender.this, "Gender: " + gender, Toast.LENGTH_SHORT).show();
                    UserDataManager.getInstance(SignupGender.this).saveGender(gender);

                    Intent intent = new Intent(SignupGender.this, SignupAge.class);
                    // Put the selected card ID as an extra to the intent
                    intent.putExtra("gender", gender);

                    // Start the activity
                    startActivity(intent);
                }




            }
        });
    }

    public void showalert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupGender.this);
        builder.setTitle("Warning");
        builder.setMessage("Please choose Gender first.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


    public void onCardClicked(View view) {
        int viewId = view.getId();
        ImageView malecheck = findViewById(R.id.malecheck);
        ImageView femalecheck = findViewById(R.id.femalecheck);


        if (viewId == R.id.malecard) {

            malecheck.setVisibility(View.VISIBLE);
            femalecheck.setVisibility(View.INVISIBLE);


            gender = "Male";
        } else if (viewId == R.id.femalecard) {

            malecheck.setVisibility(View.INVISIBLE);
            femalecheck.setVisibility(View.VISIBLE);

            gender = "Female";

        }
    }
}