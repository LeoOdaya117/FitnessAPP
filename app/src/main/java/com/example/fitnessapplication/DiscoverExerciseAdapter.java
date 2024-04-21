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

public class DiscoverExerciseAdapter extends RecyclerView.Adapter<DiscoverExerciseAdapter.ExerciseViewHolder> {
    private List<ExerciseItem> itemList;
    private static final OkHttpClient client = new OkHttpClient();

    public DiscoverExerciseAdapter(List<ExerciseItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workoutplan_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseItem item = itemList.get(position);
        String image = item.getImage();
        String set = item.getSet();
        String reps = item.getReps();
        String duration = item.getDuration();

        // If sets and reps are empty, display duration only
        if (set.isEmpty() && reps.isEmpty()) {
            holder.setRepsTextView.setText("Duration: " + duration);
            holder.durationTextView.setVisibility(View.GONE); // Hide duration TextView
        } else {
            // Display sets and reps
            holder.setRepsTextView.setText("Sets: " + set);
            holder.durationTextView.setText("Reps: " + reps);
            holder.durationTextView.setVisibility(View.VISIBLE); // Show duration TextView
        }
        holder.todayExerciseCard.setTag(item.getExerciseId());
        holder.infoImageView.setTag(item.getExerciseId());
        holder.exerciseNameTextView.setText(item.getName());

        Picasso.get()
                .load(image)
                .placeholder(R.drawable.loading) // Optional: Placeholder image while loading
                .error(R.drawable.notfound) // Optional: Error image if the image fails to load
                .into(holder.exerciseImageView);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseNameTextView, setRepsTextView, durationTextView;
        private ImageView exerciseImageView, infoImageView;
        private Animation animation;
        private CardView todayExerciseCard;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            animation = AnimationUtils.loadAnimation(itemView.getContext(), R.xml.button_animation);

            exerciseImageView = itemView.findViewById(R.id.exercisephoto);
            infoImageView = itemView.findViewById(R.id.info);
            exerciseNameTextView = itemView.findViewById(R.id.exercisename);
            setRepsTextView = itemView.findViewById(R.id.setreps);
            durationTextView = itemView.findViewById(R.id.duration);

            todayExerciseCard = itemView.findViewById(R.id.todayExerciseCard);

            // Set OnClickListener for the info_icon
            todayExerciseCard.setOnClickListener(new View.OnClickListener() {



                @Override
                public void onClick(View v) {

                    todayExerciseCard.startAnimation(animation);
                    // Get the tag associated with the info_icon
                    Object tag = todayExerciseCard.getTag();

                    if (tag != null && tag instanceof String) {
                        String exerciseId = (String) tag;
                        showExerciseModal(itemView.getContext(), exerciseId, exerciseNameTextView.getText().toString());

                    }


                }
            });

            // Set OnClickListener for the info_icon
            infoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(animation);
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
            fetchDataFromAPI(exerciseId, "exercise", new DataFetchCallback() {
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
                                seeMoreDescriptionButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        toggleTextViewExpansion(modalDescription, seeMoreDescriptionButton);
                                    }
                                });

                                // Set up "See more" button for instructions
                                seeMoreInstructionButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        toggleTextViewExpansion(modalInstructions, seeMoreInstructionButton);
                                    }
                                });

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
                    Log.e("DiscoverExerciseAdapter", "API request failed: " + errorMessage);
                    // Handle failure
                }
            });
        }

        private void setupWebView(WebView webView, String videoUrl) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    // Restrict navigation to only the intended video URL
                    String url = request.getUrl().toString();
                    return !url.startsWith("https://www.youtube.com/embed/");
                }
            });
            webView.loadUrl(videoUrl);
        }

        private void fetchDataFromAPI(String id, String category, DataFetchCallback callback) {
            String url = URLManager.MY_URL + "/User/api/fetch_details.php?itemId=" + id + "&category=" + category;
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

        private void toggleTextViewExpansion(TextView textView, TextView toggleButton) {
            if (textView.getMaxLines() == Integer.MAX_VALUE) {
                // Text is expanded, collapse it
                textView.setMaxLines(2);
                toggleButton.setText("See more");
            } else {
                // Text is collapsed, expand it
                textView.setMaxLines(Integer.MAX_VALUE);
                toggleButton.setText("See less");
            }
        }
    }



    interface DataFetchCallback {
        void onSuccess(JSONObject result);

        void onFailure(String errorMessage);
    }


}
