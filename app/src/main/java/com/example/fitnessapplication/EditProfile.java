package com.example.fitnessapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfile extends AppCompatActivity {
    private JSONObject userData;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        TextView editText_first_name = findViewById(R.id.editTextFName);
        TextView editText_last_name = findViewById(R.id.editTextLName);
        TextView editTextUsername = findViewById(R.id.editTextEmail);
        TextView editText_age = findViewById(R.id.editTextAge);
        TextView editText_height = findViewById(R.id.editTextHeight);
        TextView editText_gender = findViewById(R.id.editTextGender); // Corrected ID
        TextView editText_weight = findViewById(R.id.editTextWeight);
        ImageView imageViewProfile = findViewById(R.id.imageView_profile);


        ImageView backbtn = findViewById(R.id.backButton);
        TextView changeprof = findViewById(R.id.changeprof);
        TextView textViewSave = findViewById(R.id.textViewSave);

        String username = UserDataManager.getInstance(EditProfile.this).getEmail();
        fetchData(username);



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(EditProfile.this, R.xml.button_animation);
                backbtn.startAnimation(animation);
                onBackPressed();
            }
        });
        textViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(EditProfile.this, R.xml.button_animation);
                textViewSave.startAnimation(animation);
                textViewSave.setTextColor(Color.GRAY);

// After some time or under certain conditions, revert back to black
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewSave.setTextColor(Color.WHITE); // Revert back to black
                    }
                }, 100); // Change back to black after 2000 milliseconds (2 seconds), adjust as needed
                String updatedFirstName = editText_first_name.getText().toString();
                String updatedLastName = editText_last_name.getText().toString();
                String updatedEmail = editTextUsername.getText().toString();
                String updatedAge = editText_age.getText().toString();
                String updatedGender = editText_gender.getText().toString();
                String updatedHeight = editText_height.getText().toString();
                String updatedWeight = editText_weight.getText().toString();

                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this, R.style.AlertDialogCustomStyle);

                // Set the dialog title and message
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to save the changes?");

                // Set the positive button and its listener
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked Yes, save profile
                        if (imageurl != null) {
                            // Call the uploadImage function
                            uploadImage(imageurl, username);
//                            Toast.makeText(EditProfile.this, "Image Not Empty" , Toast.LENGTH_SHORT).show();

                        }
//                        Toast.makeText(EditProfile.this, "Image Empty" , Toast.LENGTH_SHORT).show();

                        saveProfile(updatedFirstName, updatedLastName, updatedEmail, updatedAge, updatedGender, updatedHeight, updatedWeight);
                    }
                });

                // Set the negative button and its listener
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing or dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();

                // Set the background color of the dialog window
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

                // Show the dialog
                dialog.show();
            }
        });

        changeprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeprof.setTextColor(Color.BLUE);

// After some time or under certain conditions, revert back to black
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeprof.setTextColor(Color.WHITE); // Revert back to black
                    }
                }, 500); // Change back to black after 2000 milliseconds (2 seconds), adjust as needed

                openImagePicker();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String username = UserDataManager.getInstance(EditProfile.this).getEmail();

            ImageView profileImageView = findViewById(R.id.imageView_profile);
            profileImageView.setImageURI(selectedImageUri);
            imageurl = selectedImageUri;
//            uploadImage(selectedImageUri, username);
        }
    }
    private void uploadImage(Uri imageUri,String username) {
        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("myprofile-photo", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                .addFormDataPart("myprofile-username", username) // Assuming "117@gmail.com" is the username
                .build();


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLManager.MY_URL+"/Gym_Website/user/update-profile.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog("Failed to upload image", e.getMessage());
                        Toast.makeText(EditProfile.this, "Failed to upload image 1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditProfile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            // Update the profile image view with the uploaded image
                            ImageView profileImageView = findViewById(R.id.imageView_profile);
                            profileImageView.setImageURI(imageUri);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialog("Failed to upload image", response.message());
                            Toast.makeText(EditProfile.this, "Failed to upload image 2: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String filePath = "";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(contentUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }


    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void saveProfile(String updatedFirstName, String updatedLastName, String updatedEmail, String updatedAge, String updatedGender, String updatedHeight, String updatedWeight) {
        // Perform validation if needed
        // Example: Check if fields are empty
//        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty() || updatedEmail.isEmpty() || updatedPassword.isEmpty() || updatedAge.isEmpty() || updatedGender.isEmpty() || updatedHeight.isEmpty() || updatedWeight.isEmpty()) {
//            Toast.makeText(EditProfile.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Create OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Create form body with the parameters
        RequestBody formBody = new FormBody.Builder()
                .add("FirstName", updatedFirstName)
                .add("LastName", updatedLastName)
                .add("Email", updatedEmail)
                .add("Age", updatedAge)
                .add("Gender", updatedGender)
                .add("Height", updatedHeight)
                .add("Weight", updatedWeight)
                .build();

        // Create request
        Request request = new Request.Builder()
                .url(URLManager.MY_URL+"/Gym_Website/user/api/update_user_details.php")
                .post(formBody)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditProfile.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle response
                if (response.isSuccessful()) {
                    // Profile saved successfully
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditProfile.this, "Profile Updated successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();

                        }
                    });
                } else {
                    // Error saving profile
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditProfile.this, "Failed to save profile: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
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
                                initializeUI();
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
//                            Toast.makeText(EditProfile.this, "Failed to fetch data: " + response.message(), Toast.LENGTH_SHORT).show();
                            Log.e("EditProfile", "Failed to fetch data: " + response.message()); // Log the error for debugging
                        }
                    });
                }
            }
        });
    }



    private void initializeUI() {
        if (userData != null) {
            // Extract data from the JSON object
            try {
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
                TextView editText_gender = findViewById(R.id.editTextGender); // Corrected ID
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
            // Show server response after initializing UI
//            showserverResponse(userData.toString());
        }
    }


    private void showserverResponse(String response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setTitle("Details")
                .setMessage("Server Response: \n" + response )
                .setPositiveButton("OK", null)
                .show();
    }
}