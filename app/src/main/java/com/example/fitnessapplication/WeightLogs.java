package com.example.fitnessapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeightLogs extends AppCompatActivity {

    private LinearLayout weightContainer;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_logs);

        weightContainer = findViewById(R.id.weightContainer);
        back = findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(WeightLogs.this, R.xml.button_animation);
                back.startAnimation(animation);
                onBackPressed();
            }
        });

        String email = UserDataManager.getInstance(WeightLogs.this).getEmail();
        fetchWeightLog(email);
    }

    private void showServerResponse(String response) {
        new AlertDialog.Builder(this)
                .setTitle("Server Response")
                .setMessage(response)
                .setPositiveButton("OK", null)
                .show();
    }

    private void addWeightRecord(String date, double weight, double diff) {
        View itemLayout = getLayoutInflater().inflate(R.layout.item_wieght_logs, weightContainer, false);

        TextView dateTextView = itemLayout.findViewById(R.id.logdate);
        TextView weightTextView = itemLayout.findViewById(R.id.weight);
        TextView differenceTextView = itemLayout.findViewById(R.id.weightdifference);
        ImageView indicatorImageView = itemLayout.findViewById(R.id.imagaindicator);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            String formattedDate = String.format(Locale.getDefault(), "%d %s, %s",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    new SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.getTime()),
                    new SimpleDateFormat("E", Locale.getDefault()).format(calendar.getTime()));

            dateTextView.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            dateTextView.setText(date);
        }

        weightTextView.setText(String.valueOf(weight));
        differenceTextView.setText(String.format(Locale.getDefault(), "%.2f", diff));

        if (diff > 0) {
            indicatorImageView.setVisibility(View.VISIBLE);
            indicatorImageView.setImageResource(R.drawable.arrowup);
        } else if (diff == 0) {
            differenceTextView.setText("Same Weight");
            differenceTextView.setTextSize(10);
            indicatorImageView.setVisibility(View.GONE);
        } else {
            indicatorImageView.setVisibility(View.VISIBLE);
            indicatorImageView.setImageResource(R.drawable.arrowdown);
        }

        weightContainer.addView(itemLayout);
    }

    private void fetchWeightLog(String userId) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userId)
                .build();
        Request request = new Request.Builder()
                .url(URLManager.MY_URL + "/User/api/weight_logs.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                showServerResponse("Failed to fetch weight log. Please check your internet connection.");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d("Server Response", responseData);

                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONArray weights = json.getJSONArray("weights");
                        JSONArray dates = json.getJSONArray("dates");

                        if (weights.length() == 0 || dates.length() == 0) {
                            // No weight logs found, display a message
                            runOnUiThread(() -> {
                                TextView noLogsTextView = new TextView(WeightLogs.this);
                                noLogsTextView.setText("No weight logs found.");
                                noLogsTextView.setGravity(Gravity.CENTER);
                                weightContainer.addView(noLogsTextView);
                            });
                        } else {
                            // Weight logs found, process and display them
                            for (int i = 0; i < dates.length(); i++) {
                                double weight = weights.getDouble(i);
                                double previousWeight = i > 0 ? weights.getDouble(i - 1) : weight;

                                String date = dates.getString(i);
                                runOnUiThread(() -> addWeightRecord(date, weight, weight - previousWeight));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showServerResponse("Failed to parse server response.");
                    }
                } else {
                    showServerResponse("Failed to fetch weight log. Status code: " + response.code());
                }
            }
        });
    }
}
