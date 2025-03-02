package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.text.DecimalFormat;

public class signupFinish extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView finishTextView;
    private TextView percentageTextView;
    private Button continueButton;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_finish);

        progressBar = findViewById(R.id.progressBar);
        finishTextView = findViewById(R.id.finishTextView);
        percentageTextView = findViewById(R.id.percentageTextView);
        continueButton = findViewById(R.id.continueButton);

        // Set click listener for continueButton
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear user preferences
                SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                // Navigate to WEB activity and clear task stack
                Intent intent = new Intent(signupFinish.this, WEB.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // Finish current activity
                finish();
            }
        });

        // Start loading animation (assuming startLoading() method is defined elsewhere)
        startLoading();
    }


    private void startLoading() {
        new Thread(new Runnable() {
            public void run() {
                // Text messages for each loading stage
                final String[] loadingMessages = {
                        "Creating Workout Plan...",
                        "Creating Diet Plan...",
                        "Setting up Your Account..."
                };

                for (int i = 0; i < loadingMessages.length; i++) {
                    final String message = loadingMessages[i];

                    // Update the text message
                    handler.post(new Runnable() {
                        public void run() {
                            finishTextView.setText(message);
                        }
                    });

                    // Sleep for 1.5 seconds before proceeding to the next message
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Increment progress after displaying the message
                    progressStatus += 33;
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            percentageTextView.setText(progressStatus + "%");
                        }
                    });
                }

                // After all messages are displayed and progress reaches 100%
                handler.post(new Runnable() {
                    public void run() {
                        updateUserData();
                        createDietPlan();
                        createWorkoutPlan();

                        if(success == true){
                            showSuccessDialog("Success");
                        }
                        finishTextView.setText("Congratulations! You have successfully set up your Account");
                        progressBar.setProgress(100);
                        percentageTextView.setText("100%");
                        continueButton.setVisibility(Button.VISIBLE); // Make the button visible
                    }
                });
            }
        }).start();
    }

    // Method to update user data using OkHttp
    private void updateUserData() {
        // Get user data from UserDataManager
        String websiteurl = URLManager.MY_URL +"/User/api/update_user_account.php";
        UserDataManager userDataManager = UserDataManager.getInstance(getApplicationContext());
        String email = userDataManager.getEmail();
        int age = userDataManager.getAge();
        String gender = userDataManager.getGender();
        float weight = userDataManager.getWeight();
        float goalWeight = userDataManager.getTargetWeight();
        float height = userDataManager.getHeight();
        float bmr = userDataManager.getSBMR();
        String fitnessGoal = userDataManager.getGoal();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String weightString = decimalFormat.format(weight);
        String goalWeightString = decimalFormat.format(goalWeight);
        String heightString = decimalFormat.format(height);
        String bmrString = decimalFormat.format(bmr);

        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create request body with user data
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("Age", String.valueOf(age))
                .add("Gender", gender)
                .add("Weight", weightString)
                .add("goalWeight", goalWeightString)
                .add("Height", heightString)
                .add("BMR", bmrString)
                .add("fitnessGoal", fitnessGoal)
                .build();

        // Create POST request with URL and request body
        Request request = new Request.Builder()
                .url(websiteurl) // Replace with your server URL
                .post(requestBody)
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                // Handle response here
                String responseBody = response.body().string().trim();

                if (response.isSuccessful()) {
                    // Update successful
                    // Handle successful response
                    // For example, show a Toast message
                    if(responseBody.equals("Success")){
//                        showSuccessDialog();

                    }
                    else{
                        showFailureDialog(websiteurl + " \n Error: " + responseBody);

                    }
                } else {
                    // Update failed
                    // Handle unsuccessful response
                    // For example, show a Toast message
                    String errorMessage = response.body().string(); // Get error message from the server
                    showFailureDialog(websiteurl + " \nFailed to update user data. Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                // Show failure Toast message
                showFailureDialog(e.getMessage().toString());

            }
        });
    }

    private void createDietPlan() {
        // Get user data from UserDataManager
        String websiteurl = URLManager.MY_URL +"/User/api/gen-dietplan.php";
        UserDataManager userDataManager = UserDataManager.getInstance(getApplicationContext());

        String email = userDataManager.getEmail();
        float bmr = userDataManager.getSBMR();
        String fitnessGoal = userDataManager.getFL();
        String diettype = userDataManager.getSDiet();
        String allergies = userDataManager.getAllergy();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String bmrString = decimalFormat.format(bmr);

        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create request body with user data
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("targetCalorie", bmrString)
                .add("goal", fitnessGoal)
                .add("diettype", diettype)
                .add("allergies", allergies)
                .build();

        // Create POST request with URL and request body
        Request request = new Request.Builder()
                .url(websiteurl) // Replace with your server URL
                .post(requestBody)
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                // Handle response here
                String responseBody = response.body().string().trim();

                if (response.isSuccessful()) {
                    // Update successful
                    // Handle successful response
                    // For example, show a Toast message
                    if(responseBody.equals("Success")){
//                        showSuccessDialog();
                        success = true;

                    }
                    else{
                        showFailureDialog( " \n Error: " + responseBody);

                    }
                } else {
                    // Update failed
                    // Handle unsuccessful response
                    // For example, show a Toast message
                    String errorMessage = response.body().string(); // Get error message from the server
                    showFailureDialog( " \nFailed to update user data. Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                // Show failure Toast message
                String errorMessage = e.getMessage().toString();
                showFailureDialog(errorMessage);

            }
        });
    }


    private void createWorkoutPlan() {
        // Get user data from UserDataManager
        String websiteurl = URLManager.MY_URL + "/User/api/gen-workoutplan.php";
        UserDataManager userDataManager = UserDataManager.getInstance(getApplicationContext());

        String email = userDataManager.getEmail();
        String fitnessGoal = userDataManager.getGoal();
        String fitnesslevel = userDataManager.getFL();
        String[] workoutPlan = userDataManager.getWorkoutPlan();


        // Convert workout plan array to a single string
        StringBuilder workoutPlanString = new StringBuilder();
        for (String workout : workoutPlan) {
            workoutPlanString.append(workout).append(",");
        }
        // Remove the trailing comma
        workoutPlanString.deleteCharAt(workoutPlanString.length() - 1);

        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create request body with user data and workout plan
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("fitnesslevel", fitnesslevel)
                .add("goal", fitnessGoal)
                .add("workoutPlan", workoutPlanString.toString()) // Add the workout plan string
                .build();

        // Create POST request with URL and request body
        Request request = new Request.Builder()
                .url(websiteurl) // Replace with your server URL
                .post(requestBody)
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                // Handle response here
                String responseBody = response.body().string().trim();

                if (response.isSuccessful()) {
                    // Update successful
                    // Handle successful response
                    // For example, show a Toast message
                    if (responseBody.equals("Success")) {
//                        showSuccessDialog(responseBody);
                        success = true;
                    } else {
                        showFailureDialog("\n Error: " + responseBody);
                    }
                } else {
                    // Update failed
                    // Handle unsuccessful response
                    // For example, show a Toast message
                    String errorMessage = response.body().string(); // Get error message from the server
                    showFailureDialog("\nFailed to update user data. Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                // Show failure Toast message
                String errorMessage = e.getMessage().toString();
                showFailureDialog(errorMessage);
            }
        });
    }


    // Method to show Toast message for successful update
    // Method to show success dialog
    private void showSuccessDialog(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new androidx.appcompat.app.AlertDialog.Builder(signupFinish.this)
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    // Method to show failure dialog
// Method to show failure dialog with error message
    private void showFailureDialog(final String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new androidx.appcompat.app.AlertDialog.Builder(signupFinish.this)
                        .setMessage("Failed to update user data. Error: " + errorMessage)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }


}
