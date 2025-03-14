package com.example.fitnessapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePassword extends AppCompatActivity {

    private OkHttpClient client;
    public String url = URLManager.MY_URL + "/User/api/update-password.php"; // Accessing the URL
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button changePasswordButton;
    private ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        client = new OkHttpClient();
        String username = UserDataManager.getInstance(ChangePassword.this).getEmail();

        currentPasswordEditText = findViewById(R.id.currentpassword);
        newPasswordEditText = findViewById(R.id.newpassword);
        confirmPasswordEditText = findViewById(R.id.confirmpassword);
        changePasswordButton = findViewById(R.id.btn);
        backbtn = findViewById(R.id.backButton);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from EditText views
                onBackPressed();
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from EditText views
                String currentPassword = currentPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Perform input validation
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    // Show dialog with error message for empty fields
                    showDialog("Error", "Please fill in all fields.");
                    return; // Exit onClick method if any field is empty
                }

                if (!newPassword.equals(confirmPassword)) {
                    // Show dialog with error message for password mismatch
                    showDialog("Error", "Passwords do not match.");
                    return; // Exit onClick method if passwords do not match
                }

                // Confirm password change with an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                builder.setTitle("Confirm Password Change");
                builder.setMessage("Are you sure you want to change your password?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the changepass method with validated inputs
                        changepass(username, currentPassword, newPassword, confirmPassword);
                        dialog.dismiss(); // Dismiss the dialog after confirming
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismiss the dialog if user cancels
                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });





    }
    // Method to display a dialog with a custom error message
    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismiss the dialog when OK is clicked
                    }
                })
                .show();
    }

    public void changepass(String username, String currentpassword, String newpassword, String confirmpassword) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", username)
                .add("edit_password", newpassword)
                .add("edit_confirm_password", confirmpassword)
                .add("current_password", currentpassword)

                .build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePassword.this, "Failed to connect to server.", Toast.LENGTH_SHORT).show();
                        showAlert("Message", "Failed to connect to server. No Internet Connection");
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String message = response.body().string().trim();
                Log.d("SERVER RESPONSE", message);
                if ("Success".equals(message)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            showAlert("Message", "Password Updated Successfully");


                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //edittextviewResponse.setText(message);
                            showAlert("Message", message);
//                            Toast.makeText(ChangePassword.this, message, Toast.LENGTH_SHORT).show();


                        }
                    });
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this, R.style.AlertDialogCustomStyle);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Finish the activity if the user confirms
                        if(message.equals("Password Updated Successfully")){

                            onBackPressed();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}