package com.example.fitnessapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private OkHttpClient client;
    //    public String posURL = "https://10fc-180-190-129-27.ngrok-free.app/gym_website/user/login.php";
    public String url = URLManager.MY_URL + "/User/login.php"; // Accessing the URL
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView edittextviewResponse;

    private ImageView imageViewEye;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permission is already granted, proceed with your operations that require this permission
            // For example, you can call uploadImage() method here
        }

        client = new OkHttpClient(); // Initialize OkHttpClient

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        edittextviewResponse = findViewById(R.id.response);

        imageViewEye = findViewById(R.id.imageViewEye);

        imageViewEye.setOnClickListener(new View.OnClickListener() {
            boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide the password
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isPasswordVisible = false;
                    imageViewEye.setImageResource(R.drawable.eyeropen); // Change to closed eye icon
                } else {
                    // Show the password
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isPasswordVisible = true;
                    imageViewEye.setImageResource(R.drawable.eyeclose); // Change to open eye icon
                }

                // Move cursor to the end of the input to maintain cursor position
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        });
//        editTextUsername.setEnabled(false);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    //edittextviewResponse.setText("All fields are required!");
                    Toast.makeText(MainActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_LONG).show();
                    login(username, password);
                }
            }
        });

        TextView textViewRegister = findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

// Set text color to blue
                textViewRegister.setTextColor(Color.BLUE);

// After some time or under certain conditions, revert back to black
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewRegister.setTextColor(Color.BLACK); // Revert back to black
                    }
                }, 500); // Change back to black after 2000 milliseconds (2 seconds), adjust as needed

                // Start the Signup activity
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });

        TextView forgotpass = findViewById(R.id.forgotpass);
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

// Set text color to blue
                forgotpass.setTextColor(Color.BLUE);

// After some time or under certain conditions, revert back to black
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        forgotpass.setTextColor(Color.BLACK); // Revert back to black
                    }
                }, 500); // Change back to black after 2000 milliseconds (2 seconds), adjust as needed

                // Start the Signup activity
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

    }

    public void login(String username, String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("login-username", username)
                .add("login-password", password)
                .build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to make network request: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edittextviewResponse.setText("Failed to connect to server.");
                        Toast.makeText(MainActivity.this, "Failed to connect to server.", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String message = response.body().string().trim();
                Log.d(TAG, "Response from server: " + message);

                if ("User".equals(message)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //edittextviewResponse.setText("LOGIN SUCCESS");
                            UserDataManager.getInstance(MainActivity.this).saveEmail(username);
                            UserDataManager.getInstance(MainActivity.this).savePassword(password);

                            Toast.makeText(MainActivity.this, "LOGIN SUCCESS", Toast.LENGTH_SHORT).show();
                            // After successful login
                            Intent intent = new Intent(MainActivity.this, WEB.class);
                            intent.putExtra("username", username);
                            intent.putExtra("password", password);
                            intent.putExtra("posURL", url);
                            startActivity(intent);



                            //intent = new Intent(MainActivity.this, WEB.class);
                            //startActivity(intent);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //edittextviewResponse.setText(message);

                            if (message.trim() == "Admin"){
                                Toast.makeText(MainActivity.this, "Admin Account is not Allowed", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });
    }
}
