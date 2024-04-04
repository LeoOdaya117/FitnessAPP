package com.example.fitnessapplication;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeightLogs extends AppCompatActivity {

    private ScrollView scrollViewLayout;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_logs);

        scrollViewLayout = findViewById(R.id.weightrecord);
        LinearLayout weightContainer = findViewById(R.id.weightContainer); // New LinearLayout container
        back = findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(WeightLogs.this, R.xml.button_animation);
                back.startAnimation(animation);
                onBackPressed(); // Simulate back button press to return to the previous activity (Settings)
            }
        });

        String email = UserDataManager.getInstance(WeightLogs.this).getEmail();
        fetchWeightLog(email, weightContainer); // Pass the weightContainer to the fetchWeightLog method

    }


    private void showserverResponse(String response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle("Details")
                .setMessage("Server Response: \n" + response )
                .setPositiveButton("OK", null)
                .show();
    }

    private void addWeightRecord(LinearLayout weightContainer, String date, double weight , double diff) {
        LinearLayout itemLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_wieght_logs, null);

        TextView dateTextView = itemLayout.findViewById(R.id.logdate);
        TextView weightTextView = itemLayout.findViewById(R.id.weight);
        TextView differenceTextView = itemLayout.findViewById(R.id.weightdifference);
        ImageView indicatorImageView = itemLayout.findViewById(R.id.imagaindicator); // Get reference to the ImageView
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            String formattedDate = String.format(Locale.getDefault(), "%d %s, %s",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    new SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.getTime()),
                    new SimpleDateFormat("E", Locale.getDefault()).format(calendar.getTime()));

            dateTextView.setText(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
            dateTextView.setText(date);
        }
        weightTextView.setText(String.valueOf(weight));
        differenceTextView.setText(String.format("%.2f", diff));

        if(diff > 0){
            indicatorImageView.setVisibility(View.VISIBLE);
            indicatorImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrowup));
        } else if (diff == 0) {
            differenceTextView.setText("Same Weight");
            differenceTextView.setTextSize(10);
            indicatorImageView.setVisibility(View.GONE);
        } else{
            indicatorImageView.setVisibility(View.VISIBLE);
            indicatorImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrowdown));
        }
        weightContainer.addView(itemLayout); // Add the itemLayout to the weightContainer
    }

    private void fetchWeightLog(String userId, LinearLayout weightContainer) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userId)
                .build();
        Request request = new Request.Builder()
                .url(URLManager.MY_URL + "/Gym_Website/user/api/fetch_user_weight_logs.php")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                showserverResponse("Failed to fetch weight log. Please check your internet connection.");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d("Server Response", responseData);
                if (response.isSuccessful()) {
                    if (responseData != null && !responseData.isEmpty()) {
                        try {
                            JSONObject json = new JSONObject(responseData);
                            JSONArray weights = json.getJSONArray("weights");
                            JSONArray dates = json.getJSONArray("dates");

                            for (int i = dates.length() - 1; i >= 0; i--) {
                                double weight = weights.getDouble(i);
                                double previous = weights.getDouble(i-1);


                                String date = dates.getString(i);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addWeightRecord(weightContainer, date, weight, weight-previous); // Pass the weightContainer to the addWeightRecord method
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showserverResponse("No weight log found.");
                    }
                } else {
                    showserverResponse("Failed to fetch weight log. Status code: " + response.code());
                }
            }
        });
    }


}
