package com.example.fitnessapplication;


import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;


import android.Manifest;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public class Account extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName, editTextEmail, editTextPassword;
    public String url = URLManager.MY_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);



        String username = UserDataManager.getInstance(Account.this).getEmail();

        // Inflate the dialog layout inside the onCreate method
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        fetchData(username);
        // Find views from the inflated dialog layout

        ImageView btnback = findViewById(R.id.backButton);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(Account.this, R.xml.button_animation);
                btnback.startAnimation(animation);
                onBackPressed(); // Simulate back button press to return to the previous activity (Settings)
            }
        });


        ImageView showQRCodeTextView = findViewById(R.id.textView_qrCode);
        showQRCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showQRCodeDialog();
            }
        });

        // Find views from the inflated dialog layout
        ImageView editProfileImageView = findViewById(R.id.imageView_editProfile);
        editProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });


        ImageView details = findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Account.this, About.class);
//                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
//                startActivity(intent);
                showPopupMenu(v);
            }
        });



    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_layout, popupMenu.getMenu());


        // Set item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "Change Password":
                        Intent intent = new Intent(Account.this, ChangePassword.class);
                        intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                        startActivity(intent);
                        return true;
                    default:
                        return true;
                }

            }
        });

        // Show the popup menu
        popupMenu.show();
    }

    private void showQRCodeDialog(String qrCodeImageUrl) {
        QRCodeDialogFragment dialog = QRCodeDialogFragment.newInstance(qrCodeImageUrl);
        dialog.show(getSupportFragmentManager(), "QRCodeDialogFragment");
    }


    private void showEditProfileModal(String firstName, String lastName, String email, String password, String age, String gender,  String height , String weight) {
        Dialog editProfileDialog = new Dialog(this);
        editProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editProfileDialog.setContentView(R.layout.dialog_edit_profile);

        // Find views from the inflated dialog layout
        EditText editTextFirstName = editProfileDialog.findViewById(R.id.editText_first_name);
        EditText editTextLastName = editProfileDialog.findViewById(R.id.editText_last_name);
        EditText editTextEmail = editProfileDialog.findViewById(R.id.editText_email);
        EditText editTextPassword = editProfileDialog.findViewById(R.id.editText_password);
        EditText editTextWeight = editProfileDialog.findViewById(R.id.editText_weight);
        EditText editTextHeight = editProfileDialog.findViewById(R.id.editText_height);
        EditText editTextAge = editProfileDialog.findViewById(R.id.editText_age);
        EditText editTextGender = editProfileDialog.findViewById(R.id.editText_gender);
        Button btnSave = editProfileDialog.findViewById(R.id.button_save);
        Button btnCancel = editProfileDialog.findViewById(R.id.button_cancel);

        // Set values to EditText views
        editTextFirstName.setText(firstName);
        editTextLastName.setText(lastName);
        editTextEmail.setText(email);
        editTextPassword.setText(password);
        editTextWeight.setText(weight);
        editTextHeight.setText(height);
        editTextAge.setText(age);
        editTextGender.setText(gender);

        // Set click listeners for buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve text from EditText views
                String updatedFirstName = editTextFirstName.getText().toString();
                String updatedLastName = editTextLastName.getText().toString();
                String updatedEmail = editTextEmail.getText().toString();
                String updatedPassword = editTextPassword.getText().toString();
                String updatedAge = editTextAge.getText().toString();
                String updatedGender = editTextGender.getText().toString();
                String updatedHeight = editTextHeight.getText().toString();
                String updatedWeight = editTextWeight.getText().toString();

                // Perform save operation
                saveProfile(updatedFirstName, updatedLastName, updatedEmail, updatedPassword, updatedAge, updatedGender, updatedHeight, updatedWeight);

                // Dismiss the dialog after saving
                editProfileDialog.dismiss();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog without saving
                editProfileDialog.dismiss();
            }
        });

        // Show the dialog
        editProfileDialog.show();
    }



    private void saveProfile(String updatedFirstName, String updatedLastName, String updatedEmail, String updatedPassword, String updatedAge, String updatedGender, String updatedHeight, String updatedWeight) {
        // Perform validation if needed
        // Example: Check if fields are empty
        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty() || updatedEmail.isEmpty() || updatedPassword.isEmpty() || updatedAge.isEmpty() || updatedGender.isEmpty() || updatedHeight.isEmpty() || updatedWeight.isEmpty()) {
            Toast.makeText(Account.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Create form body with the parameters
        RequestBody formBody = new FormBody.Builder()
                .add("FirstName", updatedFirstName)
                .add("LastName", updatedLastName)
                .add("Email", updatedEmail)
                .add("Password", updatedPassword)
                .add("Age", updatedAge)
                .add("Gender", updatedGender)
                .add("Height", updatedHeight)
                .add("Weight", updatedWeight)
                .build();

        // Create request
        Request request = new Request.Builder()
                .url("YOUR_SERVER_URL")
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
                        Toast.makeText(Account.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Account.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Error saving profile
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Account.this, "Failed to save profile: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private  void fetchData(String username) {
        OkHttpClient client = new OkHttpClient();

        // Construct the URL with the username parameter
        String fetchUrl = url+ "/Gym_Website/user/fetch_user_data.php?Username=" + username;

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
                        Toast.makeText(Account.this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("ResponseData", responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);

                        // Extract data from JSON object
                        final String fullName = jsonObject.getString("Firstname") + " " + jsonObject.getString("Lastname");
                        final String position = "Gym Member";
                        final String profileImageUrl = url +"/Gym_Website/" + jsonObject.getString("photo"); // Assuming "photo" is the URL of the profile image
                        final String qrCodeImageUrl = url +"/Gym_Website/" + jsonObject.getString("qr").substring(3); // Assuming "qr" is the URL of the QR code image
                        final String firstName = jsonObject.getString("Firstname");
                        final String lastName = jsonObject.getString("Lastname");
                        final String email = jsonObject.getString("Username");
                        final String password = jsonObject.getString("Password");
                        final String weight = jsonObject.getString( "Weight");
                        final String height = jsonObject.getString("Height");
                        final String age = jsonObject.getString("Age");
                        final String gender = jsonObject.getString("Gender");
                        final String bmrval = jsonObject.getString("BMR");
                        final String heightval = jsonObject.getString("Height");
                        final String weightval = jsonObject.getString("Weight");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Find views by their IDs
                                TextView textViewFullName = findViewById(R.id.textView_fullname);
                                TextView textViewPosition = findViewById(R.id.textView_position);
                                ImageView imageViewProfile = findViewById(R.id.imageView_profile);
                                TextView bmrvaltv = findViewById(R.id.bmrval);
                                TextView heightvaltv = findViewById(R.id.heightval);
                                TextView weightvaltv = findViewById(R.id.weightval);

                                bmrvaltv.setText(bmrval + " kcal");
                                heightvaltv.setText(heightval + " cm");
                                weightvaltv.setText(weightval + " kg");

                                // Set the retrieved data to UI components
                                textViewFullName.setText(fullName);
                                textViewPosition.setText(position);
                                // Load profile image using Picasso
                                Picasso.get().load(profileImageUrl).placeholder(R.drawable.loading).into(imageViewProfile);

                                ImageView showQRCodeTextView = findViewById(R.id.textView_qrCode);
                                showQRCodeTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Show QR code dialog when the QR code image is clicked
                                        showQRCodeDialog(qrCodeImageUrl);
                                    }
                                });

                                Button editProfileButton = findViewById(R.id.button_editProfile);
                                editProfileButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        showEditProfileModal(firstName, lastName, email, password, age, gender, height, weight);
                                        Intent intent = new Intent(Account.this, EditProfile.class);
                                        intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                                        startActivity(intent);
                                    }
                                });


                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Account.this, "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Account.this, "Failed to fetch data: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
            String username = UserDataManager.getInstance(Account.this).getEmail();


            uploadImage(selectedImageUri, username);
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
                .url(url+"/Gym_Website/user/update-profile.php")
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
                        Toast.makeText(Account.this, "Failed to upload image 1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Account.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Account.this, "Failed to upload image 2: " + response.message(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();

        String username = UserDataManager.getInstance(Account.this).getEmail();

        fetchData(username);
    }



}
