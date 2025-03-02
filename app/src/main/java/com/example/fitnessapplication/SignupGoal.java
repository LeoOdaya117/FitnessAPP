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
import android.widget.Toast;

public class SignupGoal extends AppCompatActivity {

    public  String goal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_goal);

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


                if(goal == null || TextUtils.isEmpty(goal)){
                    showalert();
                    return;
                }
//                Toast.makeText(SignupGoal.this, "Goal: " + goal, Toast.LENGTH_SHORT).show();
                UserDataManager.getInstance(SignupGoal.this).saveGoal(goal);

                Intent intent = new Intent(SignupGoal.this, SignupGender.class);
                // Put the selected card ID as an extra to the intent

                // Start the activity
                startActivity(intent);



            }
        });
    }
    public void showalert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupGoal.this);
        builder.setTitle("Warning");
        builder.setMessage("Please choose your Goal.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    public void onCardClicked(View view) {
        int viewId = view.getId();
        ImageView gainWeightcheck = findViewById(R.id.gainWeightcheck);
        ImageView lossWeightecheck = findViewById(R.id.lossWeightecheck);
        ImageView maintainWeightcheck = findViewById(R.id.maintainWeightcheck);
        ImageView gainMusclecheck = findViewById(R.id.gainMusclecheck);

        if (viewId == R.id.lossWeightCard) {

            lossWeightecheck.setVisibility(View.VISIBLE);
            gainWeightcheck.setVisibility(View.INVISIBLE);
            maintainWeightcheck.setVisibility(View.INVISIBLE);
            gainMusclecheck.setVisibility(View.INVISIBLE);

            goal = "Weight Loss";
        } else if (viewId == R.id.gainWeightCard) {

            lossWeightecheck.setVisibility(View.INVISIBLE);
            gainWeightcheck.setVisibility(View.VISIBLE);
            maintainWeightcheck.setVisibility(View.INVISIBLE);
            gainMusclecheck.setVisibility(View.INVISIBLE);

            goal = "Gain Weight";
        } else if (viewId == R.id.maitainWeightCard) {

            lossWeightecheck.setVisibility(View.INVISIBLE);
            gainWeightcheck.setVisibility(View.INVISIBLE);
            maintainWeightcheck.setVisibility(View.VISIBLE);
            gainMusclecheck.setVisibility(View.INVISIBLE);

            goal = "Maintain Weight";
        } else if (viewId == R.id.gainMuscleCard) {
            lossWeightecheck.setVisibility(View.INVISIBLE);
            gainWeightcheck.setVisibility(View.INVISIBLE);
            maintainWeightcheck.setVisibility(View.INVISIBLE);
            gainMusclecheck.setVisibility(View.VISIBLE);

            goal = "Muscle Gain";
        }
        // Add more conditions for other cards if needed
    }
}