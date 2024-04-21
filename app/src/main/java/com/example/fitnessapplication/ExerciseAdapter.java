package com.example.fitnessapplication;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.text.LineBreaker;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exercises;

    private static final OkHttpClient client = new OkHttpClient();

    public ExerciseAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        // Reset views to default or empty values
        holder.textViewExerciseName.setText("");
        holder.text_view_equipment.setText("");
        holder.exercise_image_view.setImageResource(R.drawable.notfound);
        holder.info_icon.setTag(null); // Clear tag
        holder.discoverExerciseID.setTag(null); // Clear tag
        String category = Discover.getCategory();

        String name = "";
        String secondtext = "";
        String image = "";
        String tag = "";

        if(Discover.getCategory().equals("exercise")){
            name = exercise.getExerciseName();
            secondtext = "Equipment: " + exercise.getEquipmentId();
            image = exercise.getImageUrl();
            tag = exercise.getExerciseId();

        } else if (Discover.getCategory().equals("food")) {
            name = exercise.getFoodName();
            secondtext = "Serving: " + exercise.getServing() + " g";
            image = exercise.getFoodImage();
            tag = exercise.getFoodID();

        } else if (Discover.getCategory().equals("equipment")) {
            name = exercise.getEquipmentName();
            secondtext = "";
            image = exercise.getEquipmentImage();
            tag = exercise.getEquipmentID();
            holder.text_view_equipment.setVisibility(View.GONE);
        } else if(Discover.getCategory().equals("warmup")){
            name = exercise.getExerciseName();
            secondtext = "Equipment: " + exercise.getEquipmentId();
            image = exercise.getImageUrl();
            tag = exercise.getExerciseId();

        }

        holder.textViewExerciseName.setText(name);
        holder.text_view_equipment.setText(secondtext);
        if(!image.isEmpty()){
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.loading) // Optional: Placeholder image while loading
                    .error(R.drawable.notfound) // Optional: Error image if the image fails to load
                    .into(holder.exercise_image_view);
        }

        holder.discoverExerciseID.setTag(tag);
        holder.info_icon.setTag(tag); // Assuming you want to set the Exercise object as the tag
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewExerciseName;
        TextView text_view_equipment;
        ImageView exercise_image_view, info_icon;
        CardView discoverExerciseID;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.xml.button_animation);

            textViewExerciseName = itemView.findViewById(R.id.text_view_exercise_name);
            text_view_equipment = itemView.findViewById(R.id.text_view_equipment);
            exercise_image_view = itemView.findViewById(R.id.exercise_image_view);
            info_icon = itemView.findViewById(R.id.info_icon);
            discoverExerciseID = itemView.findViewById(R.id.discoverExerciseID);
            // Set OnClickListener for the info_icon

            discoverExerciseID.setOnClickListener(new View.OnClickListener() {



                @Override
                public void onClick(View v) {

//                    discoverExerciseID.startAnimation(animation);
                    // Get the tag associated with the info_icon
                    Object tag = discoverExerciseID.getTag();
                    if (tag != null && tag instanceof String) {
                        String exerciseId = (String) tag;
                        // Handle the click event for the info_icon
                        // For example, you can display a dialog with exercise details
                        if(Discover.getCategory().equals("exercise")){
                            showExerciseModal(itemView.getContext(), exerciseId, textViewExerciseName.getText().toString());
                        } else if (Discover.getCategory().equals("food")) {
                            showFoodModal(itemView.getContext(), exerciseId, textViewExerciseName.getText().toString());

                        } else if (Discover.getCategory().equals("equipment")) {
                            showEquipmentModal(itemView.getContext(), exerciseId, textViewExerciseName.getText().toString());

                        } else {
                            showExerciseModal(itemView.getContext(), exerciseId, textViewExerciseName.getText().toString());

                        }
                    }

                }
            });
            info_icon.setOnClickListener(new View.OnClickListener() {



                @Override
                public void onClick(View v) {

                    info_icon.startAnimation(animation);
                    // Get the tag associated with the info_icon
                    Object tag = info_icon.getTag();
                    if (tag != null && tag instanceof String) {
                        String exerciseId = (String) tag;
                        // Handle the click event for the info_icon
                        // For example, you can display a dialog with exercise details
                        if(Discover.getCategory().equals("exercise")){
                            showExerciseModal(itemView.getContext(), exerciseId, textViewExerciseName.getText().toString());
                        } else if (Discover.getCategory().equals("food")) {
                            showFoodModal(itemView.getContext(), exerciseId, textViewExerciseName.getText().toString());

                        } else if (Discover.getCategory().equals("equipment")) {
                            showEquipmentModal(itemView.getContext(), exerciseId, textViewExerciseName.getText().toString());

                        } else {
                            showExerciseModal(itemView.getContext(), exerciseId, textViewExerciseName.getText().toString());

                        }
                    }

                }
            });
        }

        private void showFoodModal(Context context, String exerciseId, String name) {
            fetchDataFromAPI(exerciseId, Discover.getCategory(), new DataFetchCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        // Process the fetched data
                        String description = result.getString("description");
                        String calorie = result.getString("calories");
                        String carb = result.getString("carbohydrates");
                        String protein = result.getString("protein");
                        String fat = result.getString("fat");

                        // Create a handler with the main looper
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Inflate the custom alert dialog layout
                                View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_for_food, null);
                                Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.xml.button_animation);

                                // Initialize views from the custom layout
                                TextView modalTitle = dialogView.findViewById(R.id.modal_title);
                                TextView modalDescription = dialogView.findViewById(R.id.modal_description);
                                TextView modalCalorie = dialogView.findViewById(R.id.modal_calorie);
                                TextView modalCarb = dialogView.findViewById(R.id.modal_carb);
                                TextView modalProtein = dialogView.findViewById(R.id.modal_protein);
                                TextView modalFat = dialogView.findViewById(R.id.modal_fat);
                                ImageView closeButton = dialogView.findViewById(R.id.close_button);

                                // Set exercise details to the views
                                modalTitle.setText(name.toUpperCase() + " INFORMATION");
                                modalDescription.setText(description);
                                modalCalorie.setText(calorie + " kcal");
                                modalCarb.setText( carb + " g");
                                modalProtein.setText( protein + " g");
                                modalFat.setText( fat + " g");

                                // Create and show the dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setView(dialogView);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                // Set click listener for the close button
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        closeButton.startAnimation(animation);
                                        alertDialog.dismiss(); // Dismiss the dialog
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("ExerciseAdapter", "API request failed: " + errorMessage);
                    // Handle failure
                }
            });
        }

        private void showEquipmentModal(Context context, String equipmentId, String name) {
            fetchDataFromAPI(equipmentId, Discover.getCategory(), new DataFetchCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        // Process the fetched data
                        String description = result.getString("equipmentDescription");

                        String imageUrl = result.getString("image");

                        // Create a handler with the main looper
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Inflate the custom alert dialog layout
                                View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_for_equipment, null);

                                // Initialize views from the custom layout
                                TextView modalTitle = dialogView.findViewById(R.id.modal_title);
                                TextView modalDescription = dialogView.findViewById(R.id.modal_description);
                                ImageView modalImage = dialogView.findViewById(R.id.modal_image);
                                ImageView closeButton = dialogView.findViewById(R.id.close_button);

                                // Set equipment details to the views
                                modalTitle.setText(name.toUpperCase() + " INFORMATION");
                                modalDescription.setText(description);

                                // Load image using Picasso
                                if (!imageUrl.isEmpty()) {
                                    Picasso.get()
                                            .load(imageUrl)
                                            .placeholder(R.drawable.loading) // Placeholder image while loading
                                            .error(R.drawable.notfound) // Error image if the image fails to load
                                            .into(modalImage);
                                }

                                // Create and show the dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setView(dialogView);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                // Set click listener for the close button
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss(); // Dismiss the dialog
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("ExerciseAdapter", "API request failed: " + errorMessage);
                    // Handle failure
                }
            });
        }


        private void showExerciseModal(Context context, String exerciseId, String name) {
            fetchDataFromAPI(exerciseId, Discover.getCategory(), new DataFetchCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        // Process the fetched data
                        String description = result.getString("Description");
                        String instructions = result.getString("Instructions");
                        String videoUrl = result.getString("VideoURL");

                        // Create a handler with the main looper
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Inflate the custom alert dialog layout
                                View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_for_exercise, null);

                                // Initialize views from the custom layout
                                TextView modalTitle = dialogView.findViewById(R.id.modal_title);
                                TextView modalDescription = dialogView.findViewById(R.id.modal_description);
                                TextView modalInstructions = dialogView.findViewById(R.id.modal_instruction);
                                TextView seeMoreDescriptionButton = dialogView.findViewById(R.id.see_more_description_button);
                                TextView seeMoreInstructionButton = dialogView.findViewById(R.id.see_more_instruction_button);
                                ImageView closeButton = dialogView.findViewById(R.id.close_button);
                                WebView webView = dialogView.findViewById(R.id.youtubeplayer);

                                // Set exercise details to the views
                                modalTitle.setText(name.toUpperCase() + " INFORMATION");
                                modalDescription.setText(description);
                                modalInstructions.setText(instructions);
                                setupWebView(webView, videoUrl);

                                // Set up "See more" button for description
                                // Set up "See more" button for description
                                seeMoreDescriptionButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (modalDescription.getMaxLines() == Integer.MAX_VALUE) {
                                            // Description is expanded, collapse it and set instruction to 2 lines
                                            modalDescription.setMaxLines(2);
                                            seeMoreDescriptionButton.setText("See more");
                                            modalInstructions.setMaxLines(2);
                                            seeMoreInstructionButton.setText("See more");
                                        } else {
                                            // Description is collapsed, expand it and collapse instruction
                                            modalDescription.setMaxLines(Integer.MAX_VALUE);
                                            seeMoreDescriptionButton.setText("See less");
                                            modalInstructions.setMaxLines(2);
                                            seeMoreInstructionButton.setText("See more");
                                        }
                                    }
                                });

// Set up "See more" button for instructions
                                seeMoreInstructionButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (modalInstructions.getMaxLines() == Integer.MAX_VALUE) {
                                            // Instruction is expanded, collapse it and set description to 2 lines
                                            modalInstructions.setMaxLines(2);
                                            seeMoreInstructionButton.setText("See more");
                                            modalDescription.setMaxLines(2);
                                            seeMoreDescriptionButton.setText("See more");
                                        } else {
                                            // Instruction is collapsed, expand it and collapse description
                                            modalInstructions.setMaxLines(Integer.MAX_VALUE);
                                            seeMoreInstructionButton.setText("See less");
                                            modalDescription.setMaxLines(2);
                                            seeMoreDescriptionButton.setText("See more");
                                        }
                                    }
                                });
                                // Set text justification for description
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    modalDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                                }

// Set text justification for instructions
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    modalInstructions.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                                }

                                // Create and show the dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setView(dialogView);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                // Set click listener for the close button
                                closeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss(); // Dismiss the dialog
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("ExerciseAdapter", "API request failed: " + errorMessage);
                    // Handle failure
                }
            });
        }




        // Method to set up the WebView and restrict navigation
        private void setupWebView(WebView webView, String videoUrl) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    // Restrict navigation to only the intended video URL
                    String url = request.getUrl().toString();
                    if (url.startsWith("https://www.youtube.com/embed/")) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
            webView.loadUrl(videoUrl);
        }

        private void fetchDataFromAPI(String id, String category, DataFetchCallback callback) {
            String url = URLManager.MY_URL + "/User/api/fetch_details.php?itemId=" + id + "&category=" + category;
            Log.d( "API LINK: ",url );

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d( "FAILURE: ",e.getMessage() );
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d( "API RESPONSE: ",response.body().toString() );
                        callback.onFailure("Unexpected code " + response);
                        return;
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        callback.onSuccess(jsonObject);
                    } catch (JSONException e) {
                        callback.onFailure("Error parsing JSON: " + e.getMessage());
                    } finally {
                        response.body().close(); // Close the response body
                    }
                }
            });
        }


        interface DataFetchCallback {
            void onSuccess(JSONObject result);

            void onFailure(String errorMessage);
        }


    }

}
