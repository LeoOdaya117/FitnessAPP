package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;
public class Report extends Fragment {

    private LineChart chart;
    private static final String BASE_URL = URLManager.MY_URL;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private TextView currentWeight, goalText, BMIText, fitnessCategory, currentHeight;
    public String email, goal;

    private OkHttpClient client;

    private float currentweight, targetweight;
    public double CurWeight,UserpredictedWeight;
    public Report() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report, container, false);

        chart = view.findViewById(R.id.lineChart);
        currentWeight = view.findViewById(R.id.currentWeight);
        goalText = view.findViewById(R.id.goalText);
        BMIText = view.findViewById(R.id.BMIText);
        currentHeight = view.findViewById(R.id.currentHeight);
        fitnessCategory = view.findViewById(R.id.fitnessCategory);

        client = new OkHttpClient();
        email = UserDataManager.getInstance(getContext()).getEmail();
        fetchWeightLog(email);
        fetchUserDataAndPredictedWeight(email);

//        String jsonresult = calculateWeightProgress(currentweight, targetweight);

//        showAlert("Predicted Wieght", jsonresult);
        Button log_button = view.findViewById(R.id.log_button);
        log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertInputDialog("Weight", "Log your Current Weight");
            }
        });

        view.findViewById(R.id.height_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.xml.button_animation);
                view.findViewById(R.id.height_button).startAnimation(animation);
                showAlertInputDialog("Height", "Update Your Height");
            }
        });



        return view;
    }

    private void fetchUseData(String userId) {

        // Check network connectivity
        if (!isNetworkConnected()) {
            showAlert("Error", "No internet connection.");
            return;
        }

        RequestBody requestBody = new FormBody.Builder()
                .add("Username", userId)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/User/api/fetch_user_details.php")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    // Handle network or server-related errors
                    showAlert("Error", "Failed to fetch user data. Please try again later.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        Log.d("USER DATA", jsonData);
                        JSONObject jsonObject = new JSONObject(jsonData);

                        // Check if user data exists
                        if (jsonObject.has("error")) {
                            // Handle server error
                            getActivity().runOnUiThread(() -> {
                                try {
                                    showAlert("Error", jsonObject.getString("error"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } else {
                            // Update UI with user data
                            getActivity().runOnUiThread(() -> {
                                try {
                                    updateUIWithUserData(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showAlert("Error", "Failed to parse user data. Please try again later.");
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> {
                            // Handle JSON parsing error
                            showAlert("Error", "Failed to parse server response. Please try again later.");
                        });
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        // Handle HTTP error
                        String jsonData = response.message().toString();
                        Log.d("USER DATA", jsonData); // Log the actual response data


                        showAlert("Error", "Failed to fetch user data. Status code: " + response.code());
                    });
                }
            }
        });
    }


    public String calculateWeightProgress(float currentWeightKg, float targetWeightKg) {
        LocalDate currentDate = LocalDate.now();
        LocalDate targetDate = currentDate.plusWeeks((long) Math.ceil((currentWeightKg - targetWeightKg) / 1.5f));

        JSONArray jsonArray = new JSONArray();

        float currentWeight = currentWeightKg;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LocalDate date = currentDate; date.isBefore(targetDate) || date.isEqual(targetDate); date = date.plusWeeks(1)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("date", date.format(formatter));
                jsonObject.put("weight", currentWeight + " kg");
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            currentWeight -= 1.5; // Assuming a linear decrease in weight each week

            if (currentWeight < targetWeightKg) {
                break;
            }
        }

        return jsonArray.toString();
    }
    // Helper method to check network connectivity
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }





    private void updateUIWithUserData(JSONObject jsonObject) throws JSONException {
        // Check if the response contains null values for height and weight
        if (jsonObject.isNull("Height") || jsonObject.isNull("Weight")) {
            // Handle case where height or weight is null (new account)
            getActivity().runOnUiThread(() -> {
                // Display a message indicating that user data is not available
//                showAlert("Info", "User data not available. Please update your profile.");
            });
        } else {
            // User data is available, proceed with updating the UI
            String currentHeightValue = jsonObject.getString("Height");
            String currentWeightValue = jsonObject.getString("Weight");
            goal = jsonObject.getString("fitnessGoal");
            double heightInMeters = Double.parseDouble(currentHeightValue) / 100.0;
            double weightInKg = Double.parseDouble(currentWeightValue);
            CurWeight = Double.parseDouble(currentWeightValue);
            double BMI = weightInKg / (heightInMeters * heightInMeters);
            String formattedBMI = String.format("%.2f", BMI);
            String goalweight = jsonObject.getString("goalweight") + " kg";

            String fitnesscategory = calculateFitnessCategory(BMI);

            getActivity().runOnUiThread(() -> {
                updateUIElements(currentWeightValue, goalweight, formattedBMI, currentHeightValue, fitnesscategory);
            });
        }
    }


    private String calculateFitnessCategory(double BMI) {
        View colorIndicator = getActivity().findViewById(R.id.color_indicator);
        View pointer1 = getActivity().findViewById(R.id.pointer1);
        View pointer2 = getActivity().findViewById(R.id.pointer2);
        View pointer3 = getActivity().findViewById(R.id.pointer3);
        View pointer4 = getActivity().findViewById(R.id.pointer4);
        View pointer5 = getActivity().findViewById(R.id.pointer5);
        View pointer6 = getActivity().findViewById(R.id.pointer6);
        String fitnesscategory = null;


        if (BMI <=  15 ) {
            fitnesscategory = "Underweight";
            colorIndicator.setBackgroundResource(R.color.colorUnderweight);
            pointer1.setVisibility(View.VISIBLE);
            pointer2.setVisibility(View.INVISIBLE);
            pointer3.setVisibility(View.INVISIBLE);
            pointer4.setVisibility(View.INVISIBLE);
            pointer5.setVisibility(View.INVISIBLE);
            pointer6.setVisibility(View.INVISIBLE);
        } else if (BMI >  15.9 && BMI <= 18.4) {
            fitnesscategory = "Underweight";
            colorIndicator.setBackgroundResource(R.color.colorUnderweight);
            pointer1.setVisibility(View.INVISIBLE);
            pointer2.setVisibility(View.VISIBLE);
            pointer3.setVisibility(View.INVISIBLE);
            pointer4.setVisibility(View.INVISIBLE);
            pointer5.setVisibility(View.INVISIBLE);
            pointer6.setVisibility(View.INVISIBLE);
        } else if (BMI > 18.5 && BMI <= 24.9) {
            fitnesscategory = "Normal";
            colorIndicator.setBackgroundResource(R.color.colorNormal);

            pointer1.setVisibility(View.INVISIBLE);
            pointer2.setVisibility(View.INVISIBLE);
            pointer3.setVisibility(View.VISIBLE);
            pointer4.setVisibility(View.INVISIBLE);
            pointer5.setVisibility(View.INVISIBLE);
            pointer6.setVisibility(View.INVISIBLE);
        } else if (BMI > 25 && BMI <= 29.9) {
            fitnesscategory = "Overweight";
            colorIndicator.setBackgroundResource(R.color.colorObesity1);
            pointer1.setVisibility(View.INVISIBLE);
            pointer2.setVisibility(View.INVISIBLE);
            pointer3.setVisibility(View.INVISIBLE);
            pointer4.setVisibility(View.VISIBLE);
            pointer5.setVisibility(View.INVISIBLE);
            pointer6.setVisibility(View.INVISIBLE);
        } else if (BMI >= 30 && BMI <= 34.9) {
            fitnesscategory = "Obesity Class I";
            colorIndicator.setBackgroundResource(R.color.colorObesity2);
            pointer1.setVisibility(View.INVISIBLE);
            pointer2.setVisibility(View.INVISIBLE);
            pointer3.setVisibility(View.INVISIBLE);
            pointer4.setVisibility(View.INVISIBLE);
            pointer5.setVisibility(View.VISIBLE);
            pointer6.setVisibility(View.INVISIBLE);
        } else if (BMI >= 35 && BMI <= 39.9) {
            fitnesscategory = "Obesity Class II";
            colorIndicator.setBackgroundResource(R.color.colorExtremeObesity);
            pointer1.setVisibility(View.INVISIBLE);
            pointer2.setVisibility(View.INVISIBLE);
            pointer3.setVisibility(View.INVISIBLE);
            pointer4.setVisibility(View.INVISIBLE);
            pointer5.setVisibility(View.INVISIBLE);
            pointer6.setVisibility(View.VISIBLE);
        } else {
            fitnesscategory = "Extreme Obesity";
            colorIndicator.setBackgroundResource(R.color.colorExtremeObesity);
            pointer1.setVisibility(View.INVISIBLE);
            pointer2.setVisibility(View.INVISIBLE);
            pointer3.setVisibility(View.INVISIBLE);
            pointer4.setVisibility(View.INVISIBLE);
            pointer5.setVisibility(View.INVISIBLE);
            pointer6.setVisibility(View.VISIBLE);

        }
        return fitnesscategory;
    }

    private void updateUIElements(String currentWeight, String goalWeight, String BMI, String currentHeight, String fitnesscategory) {
        // Update UI elements with the retrieved data
        View colorIndicator = getActivity().findViewById(R.id.color_indicator);
        TextView currentWeightTextView = getActivity().findViewById(R.id.currentWeight);
        TextView goalText = getActivity().findViewById(R.id.goalText);
        TextView BMIText = getActivity().findViewById(R.id.BMIText);
        TextView currentHeightTextView = getActivity().findViewById(R.id.currentHeight);
        TextView fitnessCategory = getActivity().findViewById(R.id.fitnessCategory);


        // Set values to UI elements
        currentWeightTextView.setText(currentWeight + " kg");
//        goalText.setText(goalWeight);
        BMIText.setText(BMI);
        currentHeightTextView.setText(currentHeight + " cm");
        fitnessCategory.setText(fitnesscategory);



    }

    private ArrayList<Float> weightLogs = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private float predictedWeight = Float.NaN;
    private String predictedDate = "";


    private void fetchWeightLog(String userId) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userId)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/User/api/fetch_user_weight_logs.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Log.d("WEIGHT LOGS: ", e.getMessage());

                    showAlert("Error", "Failed to fetch weight log. Please check your internet connection.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string(); // Read the response body once
                        Log.d("WEIGHT LOGS: ", responseBody);

                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray weightsArray = jsonObject.getJSONArray("weights");
                        JSONArray datesArray = jsonObject.getJSONArray("dates");

                        ArrayList<Float> weights = new ArrayList<>();
                        ArrayList<String> dates = new ArrayList<>();

                        // Extract weights and dates
                        for (int i = 0; i < weightsArray.length(); i++) {
                            weights.add((float) weightsArray.getDouble(i));
                            dates.add(datesArray.getString(i));
                        }

                        // Process the extracted weights and dates
                        getActivity().runOnUiThread(() -> {
                            handleWeightLogsResponse(weights, dates);
                        });
                    } else {
                        // Handle non-successful response
                        getActivity().runOnUiThread(() -> {
                            try {
                                String errorBody = response.body().string(); // Read error response body
                                Log.d("WEIGHT LOGS: ", errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            showAlert("Error", "Failed to fetch weight log. Status code: " + response.code());
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() -> {
                        showAlert("Error", "Failed to process weight log data.");
                    });
                } finally {
                    if (response.body() != null) {
                        response.body().close(); // Close the response body to release resources
                    }
                }
            }

        });
    }

    private void fetchPredictedWeight(String username) {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/linear_regression.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
//                    showAlert("Error", "Failed to fetch predicted weight. Please check your internet connection.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        if (jsonData != null && !jsonData.isEmpty()) {
                            // Handle the predicted weight response
                            getActivity().runOnUiThread(() -> {
                                try {
                                    JSONObject jsonResponse = new JSONObject(jsonData);
                                    if (jsonResponse.has("predicted_date") && jsonResponse.has("predicted_weight")) {
                                        String predictedDate = jsonResponse.getString("predicted_date");
                                        float predictedWeight = (float) jsonResponse.getDouble("predicted_weight");
                                        UserpredictedWeight  = (float) jsonResponse.getDouble("predicted_weight");
                                        goalText.setText(jsonResponse.getString("predicted_weight") + " kg");
                                        handlePredictedWeightResponse(predictedWeight, predictedDate);
                                        if (CurWeight != 0 && UserpredictedWeight != 0 && goal != null) {
                                            showProgressRecommendationDialog(CurWeight, UserpredictedWeight, goal);
                                        }
                                    } else {
//                                        showAlert("Error", "Invalid response format. Missing predicted_date or predicted_weight.");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
//                                    showAlert("Error", "Failed to process predicted weight data.");
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(() -> {
//                                showAlert("Info", "No predicted weight found.");
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> {
//                            showAlert("Error", "Failed to process predicted weight data.");
                        });
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
//                        showAlert("Error", "Failed to fetch predicted weight. Status code: " + response.code());
                    });
                }
            }
        });
    }

    private void fetchUserDataAndPredictedWeight(String userId) {
        // Call both fetchUserData and fetchPredictedWeight asynchronously
        fetchUseData(userId);
        fetchPredictedWeight(userId);
    }

    private void handleWeightLogsResponse(ArrayList<Float> weights, ArrayList<String> dates) {
        updateWeightLogs(weights, dates);
        updateChartWithData();
    }

    private void handlePredictedWeightResponse(float predictedWeight, String predictedDate) {
        updatePredictedWeight(predictedWeight, predictedDate);
        updateChartWithData();
    }

    private void updateChartWithData() {
        ArrayList<Entry> actualEntries = new ArrayList<>();
        ArrayList<String> formattedDates = new ArrayList<>();
        ArrayList<Entry> predictedEntries = new ArrayList<>();

        // Process weight logs data
        for (int i = 0; i < weightLogs.size(); i++) {
            actualEntries.add(new Entry(i, weightLogs.get(i)));
            formattedDates.add(formatDate(dates.get(i)));
        }

        LineDataSet actualDataSet = new LineDataSet(actualEntries, "Weight Logs");
        actualDataSet.setColor(Color.BLUE);
        actualDataSet.setValueTextColor(Color.BLACK);

        LineDataSet predictedDataSet = null;

        if (!Float.isNaN(predictedWeight) && !predictedDate.isEmpty()) {
            // Process predicted weight data
            predictedEntries.add(new Entry(weightLogs.size(), predictedWeight));
            formattedDates.add(formatDate(predictedDate)); // Add predicted date to the formatted dates list

            predictedDataSet = new LineDataSet(predictedEntries, "Predicted Weight");
            predictedDataSet.setColor(Color.RED);
            predictedDataSet.setValueTextColor(Color.BLACK);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(actualDataSet);

        if (predictedDataSet != null) {
            dataSets.add(predictedDataSet);
        }

        LineData lineData = new LineData(dataSets);

        // Configure X-axis formatting with dates
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(formattedDates));

        // Set data to the chart
        chart.setData(lineData);
        chart.invalidate();
    }

    private void updateWeightLogs(ArrayList<Float> weights, ArrayList<String> dateList) {
        weightLogs.clear();
        dates.clear();
        weightLogs.addAll(weights);
        dates.addAll(dateList);
    }

    private void updatePredictedWeight(float weight, String date) {
        predictedWeight = weight;
        predictedDate = date;
    }

    private String formatDate(String dateStr) {
        // Assuming dateStr is in "yyyy-MM-dd" format
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);

            // Format date for display (e.g., "Apr 06")
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // Return original date if parsing fails
        }
    }













    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertInputDialog(String category, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.weight_height_alert_dialog, null);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        EditText user_input = dialogView.findViewById(R.id.user_input);
        Button save_button = dialogView.findViewById(R.id.save_button);
        Button cancel_button = dialogView.findViewById(R.id.cancel_button);

        titleTextView.setText(title);
        if (category.equals("Weight")) {
            user_input.setHint("Enter your weight (kg)");
        } else {
            user_input.setHint("Enter your Height (cm)");
        }

        AlertDialog dialog = builder.create();
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = user_input.getText().toString().trim();
                if (input.isEmpty()) {
                    showAlert("Warning", "All fields must be filled!");
                    return;
                }

                if (category.equals("Weight")) {
                    // Check if weight input exceeds maximum allowed weight (e.g., 300 kg)
                    double weight = Double.parseDouble(input);
                    if (weight > 300.0) { // Example maximum weight constraint
                        showAlert("Warning", "Maximum weight allowed is 300 kg.");
                        return;
                    }
                    saveWeight(email, input);
                } else { // Assuming category is "Height"
                    // Check if height input exceeds maximum allowed height (e.g., 250 cm)
                    double height = Double.parseDouble(input);
                    if (height > 250.0) { // Example maximum height constraint
                        showAlert("Warning", "Maximum height allowed is 250 cm.");
                        return;
                    }
                    saveHeight(email, input);
                }
                dialog.dismiss();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void saveWeight(String userId, String weight) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userId)
                .add("weight", weight)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/User/api/save_weight.php")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    showAlert("Error", "Failed to save weight. Please try again later.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        fetchUseData(email);
                        fetchWeightLog(email);
                        fetchPredictedWeight(email);
                        handleSaveResult(result);
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        showAlert("Error", "Failed to save weight. Status code: " + response.code());
                    });
                }
            }
        });
    }

    private void saveHeight(String userId, String height) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userId)
                .add("height", height)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/User/api/save_height.php")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    showAlert("Error", "Failed to save height. Please try again later.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        fetchUseData(email);
                        fetchWeightLog(email);
                        handleSaveResult(result);
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        showAlert("Error", "Failed to save height. Status code: " + response.code());
                    });
                }
            }
        });
    }
    public void showProgressRecommendationDialog(double curWeight, double userPredictedWeight, String fitnessGoal) {
        // Calculate the difference between predicted weight and current weight
        double weightDifference = userPredictedWeight - curWeight;

        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Progress Recommendation");

        // Check fitness goal and weight difference to provide recommendation
        if (fitnessGoal != null) {
            if (fitnessGoal.equals("Loss Weight")) {
                if (weightDifference >= 0) {
                    builder.setMessage("Your weight is not decreasing. Here are some tips:\n1. Maintain a balanced diet.\n2. Increase physical activity.");
                } else {
                    builder.setMessage("Your weight is decreasing. Keep up the good work!");
                }
            } else if (fitnessGoal.equals("Maintain Weight")) {
                if (Math.abs(weightDifference) > 0.5) { // Adjust threshold as needed
                    builder.setMessage("Your weight has changed. Here are some tips to maintain weight:\n1. Monitor your food intake.\n2. Stay active.");
                } else {
                    builder.setMessage("Your weight is stable. Well done!");
                }
            } else if (fitnessGoal.equals("Gain Weight")) {
                if (weightDifference <= 0) {
                    builder.setMessage("Your weight is not increasing. Here are some tips:\n1. Increase calorie intake with healthy foods.\n2. Incorporate strength training.");
                } else {
                    builder.setMessage("Your weight is increasing. Good job!");
                }
            } else {
                // Handle unknown fitness goals
                builder.setMessage("Unknown fitness goal. Please consult a fitness expert for personalized recommendations.");
            }
        } else {
            // Handle null fitness goal
            builder.setMessage("Fitness goal is null. Please set a valid fitness goal:  " + weightDifference);
        }

        // Add OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); // Dismiss the dialog when OK is clicked
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleSaveResult(String result) {
        if (result.equals("Success")) {
            showAlert("Success", "Data successfully saved.");
            // Fetch updated data after successful save
            fetchUseData(email);
        } else {
            showAlert("Error", result);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel ongoing OkHttpClient calls when fragment is destroyed
        if (client != null) {
            client.dispatcher().cancelAll();
        }
    }

}
