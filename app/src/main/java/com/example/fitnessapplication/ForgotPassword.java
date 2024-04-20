package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPassword extends AppCompatActivity {

    private String Url = URLManager.MY_URL;
    private boolean existing =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button sendEmailButton = findViewById(R.id.buttonResetPassword);
        TextView logintext = findViewById(R.id.textViewLogin);
        EditText email = findViewById(R.id.editTextEmail);


        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logintext.setTextColor(Color.BLUE);

// After some time or under certain conditions, revert back to black
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        logintext.setTextColor(Color.BLACK); // Revert back to black
                    }
                }, 500); // Change back to black after 2000 milliseconds (2 seconds), adjust as needed

                onBackPressed();
            }
        });
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = generateToken();


                String recipientEmail = email.getText().toString().trim();


                String forgotPassUrl = Url + "/forgotpassword.php?email=" + recipientEmail + "&token=" +token;

                String subject = "Password Reset";
                String message = "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Reset Password</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;\">\n" +
                        "        <p>Dear User,</p>\n" +
                        "        <p>To reset your password, please click on the following button:</p>\n" +
                        "        <p>\n" +
                        "            <a href=\"" + forgotPassUrl + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #ff8000; color: white; text-decoration: none;\">Reset Password</a>\n" +
                        "        </p>\n" +
                        "        <p>If you did not request a password reset, please ignore this email.</p>\n" +
                        "        <p>Thank you,</p>\n" +
                        "        <p>Fitness Application Team</p>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>";

                checkEmail(recipientEmail, subject, message);

                showConfirmationDialog(recipientEmail, subject, message, token);







            }
        });
    }

    private void saveToken(String token, String email) {
        OkHttpClient client = new OkHttpClient();

        // Prepare the request body (email and token)
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("token", token)
                .build();

        // Create the HTTP request to send the email with the token
        Request request = new Request.Builder()
                .url(Url + "/User/api/save_token.php")
                .post(requestBody)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle network failure
                Log.e("ForgotPassword", "Failed to save token: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(ForgotPassword.this, "Failed to save token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Handle the response from the server (e.g., show success message)
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPassword.this, "Token Save!: " + responseBody, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Handle HTTP error
                    Log.e("ForgotPassword", "Failed to save token, HTTP error: " + response.code());
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPassword.this, "Failed to save token: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }




    private void showConfirmationDialog(String recipientEmail, String subject, String message, String token) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to send the email?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        checkEmail(recipientEmail, subject, message);

                        if(existing){
                            sendEmail(recipientEmail, subject, message);

                            saveToken(token,recipientEmail);
                            existing = false;
                        }else{
                            showAlertDialog("No account found for this email address.", "Not Found");
                        }


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button, dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void showAlertDialog(String message, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.bmr_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);


        titleTextView.setText(title);
        messageTextView.setText(message);

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Dismiss the dialog when the OK button is clicked
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    // Replace the existing sendEmail method in ForgotPassword activity with this one
    private void sendEmail(String recipientEmail, String subject, String messageBody) {
        Log.d("ForgotPassword", "Sending email to: " + recipientEmail);
        try {
            String message = "An email with instructions on how to reset your password has been sent to "+ recipientEmail +". Please check your spam or junk folder if you don’t see the email in your inbox.";
            EmailSender.sendEmail(this, recipientEmail, subject, messageBody);
            showAlertDialog(message, "Email Sent");
        } catch (Exception e) {
            String errorMessage = "Failed to send email:";
            if (e.getMessage() != null) {
                errorMessage += ": " + e.getMessage();
            }
            showAlertDialog(errorMessage, "System Error");
        }
    }


    private void checkEmail(String username, String subject, String message) {
        OkHttpClient client = new OkHttpClient();

        // Construct the URL with the username
        String url = URLManager.MY_URL + "/User/api/check_email.php?email=" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle failure appropriately
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();

                    if(result.equals("Found")){
                        existing = true;
//                        showConfirmationDialog(username, subject, message);

                    }
                    else{
//                        showAlertDialog("No account found for this email address.", "Not Found");

                        existing = false;
                    }




                } else {
                    // Handle unsuccessful response
                    System.out.println("Error: " + response.code());
                }
            }
        });
    }

    private String generateToken() {
        // Implement your token generation logic (e.g., using UUID)
        return UUID.randomUUID().toString();
    }









}
