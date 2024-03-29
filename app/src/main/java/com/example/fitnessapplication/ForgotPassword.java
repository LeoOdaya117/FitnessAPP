package com.example.fitnessapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassword extends AppCompatActivity {
    private   String SMTP_HOST = "smtp.gmail.com";
    private   String SMTP_PORT = "587"; // SMTP server port
    private   String SMTP_USERNAME = "odayaleo117@gmail.com";
    private   String SMTP_PASSWORD = "usfk chni dizw upfx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button sendEmailButton = findViewById(R.id.buttonResetPassword);
        EditText email = findViewById(R.id.editTextEmail);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientEmail = email.getText().toString().trim();
                String subject = "Password Reset";
                String message = "Click the following link to reset your password: www.example.com/reset-password";

                Log.d("ForgotPassword", "Sending email...");
                try {
                    sendEmail(recipientEmail, subject, message);
                    showAlertDialog("Email sent successfully");
                } catch (MessagingException e) {
                    showAlertDialog("Failed to send email: " + e.getMessage());
                }
                Log.d("ForgotPassword", "Email sending finished");
            }
        });
    }



//    private void sendEmail(String recipientEmail) {
//        String subject = "Password Reset";
//        String message = "Click the following link to reset your password: www.example.com/reset-password";
//        try {
//            EmailSender.sendEmail(recipientEmail, subject, message);
//            // Show success dialog
//            showAlertDialog("Email sent successfully");
//        } catch (MessagingException e) {
//            // Show failure dialog with error message
//            showAlertDialog("Failed to send email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void sendEmail(String recipientEmail, String subject, String messageBody) throws MessagingException {
        Log.d("EmailSender", "Sending email to: " + recipientEmail);
        try {
            // Existing code to send email
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);

            System.out.println("Email sent successfully");
            Log.d("EmailSender", "Email sent successfully");
        } catch (MessagingException e) {
            Log.e("EmailSender", "Failed to send email: " + e.getMessage(), e);
        }


    }





}
