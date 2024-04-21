package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Workout extends AppCompatActivity {

    private OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        client = new OkHttpClient();
        // Retrieve the category name from the intent extras
        String category = getIntent().getStringExtra("category");

        // Now you have the category name and can use it as needed in your activity
        // For example, you can display it in a TextView
        TextView categoryTextView = findViewById(R.id.title);
        ImageView backImage = findViewById(R.id.actvityBackground);
        ImageView backbuton = findViewById(R.id.backButton);

        if (category.equals("Warm Up")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.warmupstwo));
        } else if (category.equals("Abs Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.abs));
        } else if (category.equals("Chest Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.chest));
        } else if (category.equals("Arm Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.arm));
        } else if (category.equals("Leg Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.leg));
        } else if (category.equals("Shoulder & Back Beginner")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.backphoto));
        } else if (category.equals("Abs Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.abs));
        } else if (category.equals("Chest Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.chest));
        } else if (category.equals("Arm Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.arm));
        } else if (category.equals("Leg Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.leg));
        } else if (category.equals("Shoulder & Back Intermediate")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.backphoto));
        } else if (category.equals("Abs Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.abs));
        } else if (category.equals("Chest Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.chest));
        } else if (category.equals("Arm Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.arm));
        } else if (category.equals("Leg Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.leg));
        } else if (category.equals("Shoulder & Back Advanced")) {
            backImage.setImageDrawable(getResources().getDrawable(R.drawable.backphoto));
        }

        categoryTextView.setText(category.toUpperCase());

        // Fetch data from the API
        fetchData(category);

        backbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(Workout.this, R.xml.button_animation);
                backbuton.startAnimation(animation);
                onBackPressed();
            }
        });
    }

    private void fetchData(String workoutset) {

        String apiurl = URLManager.MY_URL +"/User/api/fetch_workoutset.php?workoutset=" + workoutset;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URLManager.MY_URL + "/User/api/fetch_workoutset.php").newBuilder();
        urlBuilder.addQueryParameter("workoutset", workoutset);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("API URL", apiurl);
                    Log.d("ResponseData", responseData);

                    // Parse response data and create a list of ExerciseItem objects
                    List<ExerciseItem> itemList = parseResponseData(responseData);

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        // Create and set adapter
                        DiscoverExerciseAdapter adapter = new DiscoverExerciseAdapter(itemList);
                        RecyclerView recyclerView = findViewById(R.id.workoutContainer);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Workout.this));
                    });
                } else {
                    // Handle unsuccessful response
                }
            }
        });
    }

    private List<ExerciseItem> parseResponseData(String responseData) {
        List<ExerciseItem> itemList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String exerciseId = jsonObject.getString("ExerciseID");
                String image = jsonObject.getString("ImageURL");
                String name = jsonObject.getString("ExerciseName");
                String set = jsonObject.getString("set");
                String reps = jsonObject.getString("reps");
                String duration = jsonObject.getString("duration");
                String difficulty = jsonObject.getString("Difficulty");

                ExerciseItem item = new ExerciseItem(exerciseId, image, name, set, reps, duration, difficulty);
                itemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemList;
    }

}
