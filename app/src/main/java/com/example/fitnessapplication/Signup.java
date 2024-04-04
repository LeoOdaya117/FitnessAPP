package com.example.fitnessapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Signup extends AppCompatActivity {

    public String websiteurl = URLManager.MY_URL;
    private OkHttpClient client;
    private String posURL = websiteurl+ "/gym_website/user/signup.php";

    private EditText editTextFName;
    private EditText editTextLname;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button buttonregister;
    private TextView edittextviewResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        client = new OkHttpClient(); // Initialize OkHttpClient

        editTextFName = findViewById(R.id.editTextFName);
        editTextLname = findViewById(R.id.editTextLname);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonregister = findViewById(R.id.buttonSignup);

        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstname = editTextFName.getText().toString().trim();
                String lastname = editTextLname.getText().toString().trim();
                String username = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
                    //edittextviewResponse.setText("All fields are required!");
                    Toast.makeText(Signup.this, "All fields are required!", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_LONG).show();
                    showConfirmationDialog(firstname,lastname,username, password);
                }
            }
        });

        // Find the textViewLogin by its id
        TextView textViewLogin = findViewById(R.id.textViewLogin);

        // Set OnClickListener to textViewLogin
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MainActivity
                textViewLogin.setTextColor(Color.BLUE);

// After some time or under certain conditions, revert back to black
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewLogin.setTextColor(Color.BLACK); // Revert back to black
                    }
                }, 500); // Change back to black after 2000 milliseconds (2 seconds), adjust as needed

                onBackPressed();
            }
        });


    }


    public void signup(String fistname,String lastname,String username, String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("signup-FirstName", fistname)
                .add("signup-LastName", lastname)
                .add("signup-username", username)
                .add("signup-password", password)
                .build();
        Request request = new Request.Builder().url(posURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                // Log.e(TAG, "Failed to make network request: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //edittextviewResponse.setText("Failed to connect to server.");
                        Toast.makeText(Signup.this, "Failed to connect to server.", Toast.LENGTH_LONG).show();

                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String message = response.body().string().trim();
                //Log.d(TAG, "Response from server: " + message);

                if ("Success".equals(message)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //edittextviewResponse.setText("LOGIN SUCCESS");
                            Toast.makeText(Signup.this, "SIGNUP SUCCESS", Toast.LENGTH_LONG).show();
                            // After successful SIGNUP
                            AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this, R.style.AlertDialogCustomStyle);
                            builder.setMessage("Signup Success")
                                    .setTitle("Success")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(Signup.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();


                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!message.equals("Admin")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                                builder.setMessage(message)
                                        .setTitle("Message")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User clicked OK button
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showConfirmationDialog(String firstname,String lastname,String username, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to signup?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signup(firstname,lastname,username, password);
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
    }
}
