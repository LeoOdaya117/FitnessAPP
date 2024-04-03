package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

//        ImageView details = findViewById(R.id.details);
//        details.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TransactionHistory.this, About.class);
//                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
//                startActivity(intent);
//            }
//        });
    }
}