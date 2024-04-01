package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.text.LineBreaker;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class WorkoutPlanExerciseAdapter extends RecyclerView.Adapter<WorkoutPlanExerciseAdapter.WorkoutPlanExerciseViewHolder> {

    private List<WorkoutPlanExercise> exerciseList;
    private static final OkHttpClient client = new OkHttpClient();
    public WorkoutPlanExerciseAdapter(List<WorkoutPlanExercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public WorkoutPlanExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workoutplan_exercise, parent, false);
        return new WorkoutPlanExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutPlanExerciseViewHolder holder, int position) {
        WorkoutPlanExercise exercise = exerciseList.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class WorkoutPlanExerciseViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseNameTextView, setRepsTextView, durationTextView;
        private ImageView exerciseImageView, infoImageView;

        public WorkoutPlanExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.xml.button_animation);

            exerciseImageView = itemView.findViewById(R.id.exercisephoto);
            infoImageView = itemView.findViewById(R.id.info);
            exerciseNameTextView = itemView.findViewById(R.id.exercisename);
            setRepsTextView = itemView.findViewById(R.id.setreps);
            durationTextView = itemView.findViewById(R.id.duration);


            // Set OnClickListener for the info_icon
            infoImageView.setOnClickListener(new View.OnClickListener() {



                @Override
                public void onClick(View v) {

                    infoImageView.startAnimation(animation);
                    // Get the tag associated with the info_icon
                    Object tag = infoImageView.getTag();

                    if (tag != null && tag instanceof String) {
                        String exerciseId = (String) tag;
                        showExerciseModal(itemView.getContext(), exerciseId, exerciseNameTextView.getText().toString());

                    }


                }
            });

        }

        private void showExerciseModal(Context context, String exerciseId, String name) {
            fetchDataFromAPI(exerciseId, "exercise", new ExerciseAdapter.ExerciseViewHolder.DataFetchCallback() {
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

        private void fetchDataFromAPI(String id, String category, ExerciseAdapter.ExerciseViewHolder.DataFetchCallback callback) {
            String url = URLManager.MY_URL + "/Gym_Website/user/api/fetch_details.php?itemId=" + id + "&category=" + category;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
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

        public void bind(WorkoutPlanExercise exercise) {
            String image = exercise.getImageUrl();
            String set = exercise.getSet();
            String reps = exercise.getReps();
            String duration = exercise.getDuration();

            // If sets and reps are empty, display duration only
            if (set.isEmpty() && reps.isEmpty()) {
                setRepsTextView.setText("Duration: " + duration);
                durationTextView.setVisibility(View.GONE); // Hide duration TextView
            } else {
                // Display sets and reps
                setRepsTextView.setText("Sets: " + set);
                durationTextView.setText("Reps: " + reps);
                durationTextView.setVisibility(View.VISIBLE); // Show duration TextView
            }
            infoImageView.setTag(exercise.getExerciseID());
            exerciseNameTextView.setText(exercise.getName());

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.loading) // Optional: Placeholder image while loading
                    .error(R.drawable.notfound) // Optional: Error image if the image fails to load
                    .into(exerciseImageView);


        }

    }
}
