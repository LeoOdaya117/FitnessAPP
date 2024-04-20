package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent;

        // Get references to your LinearLayout containers
        LinearLayout accountContainer = findViewById(R.id.account_container);
        LinearLayout subscriptionContainer = findViewById(R.id.subscription_container);
        LinearLayout pendingContainer = findViewById(R.id.pending_container);
        LinearLayout historyContainer = findViewById(R.id.history_container);
        LinearLayout logoutContainer = findViewById(R.id.logout_container);
        LinearLayout bmiContainer = findViewById(R.id.bmi_container);
        LinearLayout bmrContainer = findViewById(R.id.bmr_container);

        // Set click listeners for each container
        accountContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, Account.class);
                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);

            }
        });

        subscriptionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Settings.this, Subscription.class);
                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        pendingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Pending container
                //Toast.makeText(Settings.this, "Pending Transactions clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.this, Pending.class);
                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        historyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for History container
                //Toast.makeText(Settings.this, "Transaction History clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.this, WeightLogs.class);
                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        logoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Logout container
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to Logout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        WEB.logout();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing or handle as needed
                        dialog.dismiss(); // Dismiss the dialog
                        finish(); // Finish the current activity
                    }
                });
                builder.setCancelable(false); // Prevent dismissing dialog by tapping outside
                builder.show();
//                Toast.makeText(Settings.this, "Logout clicked", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Settings.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish(); // Finish the current activity to prevent going back to it
                // Add your action here
            }
        });

        bmiContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, BMI.class);
                startActivity(intent);
            }
        });

        bmrContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for Logout container


                Intent intent = new Intent(Settings.this, BMR.class);
                startActivity(intent);
            }
        });
    }

}
