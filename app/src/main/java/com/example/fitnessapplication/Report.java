package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Report extends Fragment {

    private LineChart chart;
    private static final String BASE_URL = URLManager.MY_URL;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private TextView currentWeight, goalText, BMIText, fitnessCategory, currentHeight;
    private String email;

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

        email = UserDataManager.getInstance(getContext()).getEmail();
        fetchWeightLog(email);
        fetchUseData(email);

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
                showAlertInputDialog("Height", "Update Your Height");
            }
        });

        return view;
    }

    private void fetchUseData(String userId) {
        OkHttpClient client = new OkHttpClient();

        // Check network connectivity
        if (!isNetworkConnected()) {
            showAlert("Error", "No internet connection.");
            return;
        }

        RequestBody requestBody = new FormBody.Builder()
                .add("Username", userId)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/Gym_Website/user/api/fetch_user_details.php")
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
                        showAlert("Error", "Failed to fetch user data. Status code: " + response.code());
                    });
                }
            }
        });
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
            double heightInMeters = Double.parseDouble(currentHeightValue) / 100.0;
            double weightInKg = Double.parseDouble(currentWeightValue);
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
        goalText.setText(goalWeight);
        BMIText.setText(BMI);
        currentHeightTextView.setText(currentHeight + " cm");
        fitnessCategory.setText(fitnesscategory);



    }



    private void fetchWeightLog(String userId) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userId)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/Gym_Website/user/api/fetch_user_weight_logs.php")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    showAlert("Error", "Failed to fetch weight log. Please check your internet connection.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    if (jsonData != null && !jsonData.isEmpty()) {
                        getActivity().runOnUiThread(() -> {
                            updateChartWithData(jsonData);
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            showAlert("Info", "No weight log found.");
                        });
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        showAlert("Error", "Failed to fetch weight log. Status code: " + response.code());
                    });
                }
            }
        });
    }

    private void updateChartWithData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray weightsArray = jsonObject.getJSONArray("weights");
            JSONArray datesArray = jsonObject.getJSONArray("dates");

            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < weightsArray.length(); i++) {
                float weight = (float) weightsArray.getDouble(i);
                entries.add(new Entry(i, weight));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Weight Data");
            dataSet.setColor(Color.BLUE);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setCircleColor(Color.BLACK);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int index = (int) value;
                    if (index >= 0 && index < datesArray.length()) {
                        try {
                            String dateString = datesArray.getString(index);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = sdf.parse(dateString);
                            SimpleDateFormat sdfOutput = new SimpleDateFormat("MMM d");
                            return sdfOutput.format(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return "";
                }
            });

            YAxis yAxisRight = chart.getAxisRight();
            yAxisRight.setEnabled(false);

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to parse weight log data.");
        }
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomStyle);
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
                String input = user_input.getText().toString();
                if (category.equals("Weight")) {
                    saveWeight(email, input);
                } else {
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
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userId)
                .add("weight", weight)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/Gym_Website/user/api/save_weight.php")
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
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("email", userId)
                .add("height", height)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/Gym_Website/user/api/save_height.php")
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

    private void handleSaveResult(String result) {
        if (result.equals("Success")) {
            showAlert("Success", "Data successfully saved.");
            // Fetch updated data after successful save
            fetchUseData(email);
        } else {
            showAlert("Error", result);
        }
    }

}
