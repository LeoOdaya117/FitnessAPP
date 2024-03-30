package com.example.fitnessapplication;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    // SMTP server configuration (change according to your email provider)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // SMTP server port
    private static final String SMTP_USERNAME = "fitnessapp96@gmail.com"; // Your email address
    private static final String SMTP_PASSWORD = "xxesgxqubzucxyco"; // Your email password //usfkchnidizwupfx


    public static void sendEmail(Context context, String recipientEmail, String subject, String messageBody) {
        Log.d("EmailSender", "Sending email to: " + recipientEmail);

        // Create properties object to configure the SMTP connection
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Create a Session object using the properties and an Authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
        String senderName = "Fitness App Support";

        Message message = new MimeMessage(session);
        try {
            // Create a MimeMessage object

            // Set the sender's email address
            message.setFrom(new InternetAddress(SMTP_USERNAME, senderName));


            // Set the recipient's email address
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(recipientEmail));
            // Set the email subject
            message.setSubject(subject);
            // Set the email message body
            message.setContent(messageBody, "text/html");

            Thread thread = new Thread(){
                @Override
                public  void run(){
                    try {
                        Transport.send(message);
                        Log.d("EmailSender", "Email sent successfully");
                    }catch(MessagingException e){
                        e.printStackTrace();
                        Log.d("Email ERROR: ", e.getMessage());
                    }
                }

            };
            thread.start();


            // Send the email


        } catch (MessagingException e) {
            // Log detailed error information
            Log.e("EmailSender", "Failed to send email: " + e.getMessage(), e);
            // Log additional information if available
            if (e.getNextException() != null) {
                Log.e("EmailSender", "Next Exception: " + e.getNextException().getMessage(), e.getNextException());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
