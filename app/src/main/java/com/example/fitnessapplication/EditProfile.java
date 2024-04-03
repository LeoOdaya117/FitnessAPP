package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditProfile extends AppCompatActivity {
    private JSONObject userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        String username = UserDataManager.getInstance(EditProfile.this).getEmail();
        fetchData(username);

        showserverResponse(userData.toString());

        if (userData != null) {
            try {
                // Extract data from the JSON object
                String profileImageUrl = URLManager.MY_URL + "/Gym_Website/" + userData.getString("photo");
                String firstName = userData.getString("Firstname");
                String lastName = userData.getString("Lastname");
                String email = userData.getString("Username");
                String weight = userData.getString("Weight");
                String height = userData.getString("Height");
                String age = userData.getString("Age");
                String gender = userData.getString("Gender");

                // Find views by their IDs
                TextView editText_first_name = findViewById(R.id.editTextFName);
                TextView editText_last_name = findViewById(R.id.editTextLName);
                TextView editTextUsername = findViewById(R.id.editTextEmail);
                TextView editText_age = findViewById(R.id.editTextAge);
                TextView editText_height = findViewById(R.id.editTextHeight);
                TextView editText_gender = findViewById(R.id.editText_gender);
                TextView editText_weight = findViewById(R.id.editTextWeight);
                ImageView imageViewProfile = findViewById(R.id.imageView_profile);

                // Load profile image using Picasso
                Picasso.get().load(profileImageUrl).placeholder(R.drawable.loading).into(imageViewProfile);

                // Set data to UI elements
                editText_first_name.setText(firstName);
                editText_last_name.setText(lastName);
                editTextUsername.setText(email);
                editText_age.setText(age);
                editText_height.setText(height);
                editText_gender.setText(gender);
                editText_weight.setText(weight);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(EditProfile.this, "Failed to extract data from JSON", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchData(String username) {
        OkHttpClient client = new OkHttpClient();

        // Construct the URL with the username parameter
        String fetchUrl = URLManager.MY_URL + "/Gym_Website/user/fetch_user_data.php?Username=" + username;

        Request request = new Request.Builder()
                .url(fetchUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditProfile.this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        Log.d("ResponseData", responseData);
                        // Parse the response data to JSONObject and store it in the global variable
                        userData = new JSONObject(responseData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    userData = new JSONObject(responseData);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditProfile.this, "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditProfile.this, "Failed to fetch data: " + response.message(), Toast.LENGTH_SHORT).show();
                            Log.e("EditProfile", "Failed to fetch data: " + response.message()); // Log the error for debugging
                        }
                    });
                }
            }
        });
    }





    private void showserverResponse(String response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setTitle("Details")
                .setMessage("Server Response: \n" + response )
                .setPositiveButton("OK", null)
                .show();
    }
}