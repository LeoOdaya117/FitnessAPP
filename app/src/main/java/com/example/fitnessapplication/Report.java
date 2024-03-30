package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Report#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Report extends Fragment {

    private LineChart chart;
    private static final String BASE_URL = URLManager.MY_URL;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private TextView currentWeight, goalText,BMIText,fitnessCategory,currentHeight;
    private String email = UserDataManager.getInstance(getContext()).getEmail();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Report() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Report.
     */
    // TODO: Rename and change types and number of parameters
    public static Report newInstance(String param1, String param2) {
        Report fragment = new Report();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report, container, false);

        chart = view.findViewById(R.id.lineChart);
        currentWeight = view.findViewById(R.id.currentWeight); // Initialize currentWeight TextView
        goalText = view.findViewById(R.id.goalText); // Initialize goalText TextView
        BMIText = view.findViewById(R.id.BMIText); // Initialize BMIText TextView
        currentHeight = view.findViewById(R.id.currentHeight); // Initialize currentHeight TextView
        fitnessCategory = view.findViewById(R.id.fitnessCategory); // Initialize fitnessCategory TextView



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


        return view; // Return the inflated view
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
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        updateChartWithData(jsonData);

                    });
                } else {
                    System.out.println("Failed to fetch weight log: " + response.code());
                    // Display an error message in an AlertDialog
                    getActivity().runOnUiThread(() -> {
                        showAlert("Error", "Failed to fetch weight log: " + response.code());
                    });
                }
            }

        });
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
                    if (result.equals("Success")) {
                        getActivity().runOnUiThread(() -> {
                            showAlert("Message", "Weight successfully logged.");
                            fetchWeightLog(email);
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            showAlert("Error", result);
                        });
                    }
                } else {
                    int statusCode = response.code();
                    getActivity().runOnUiThread(() -> {
                        showAlert("Error", "Failed to save weight. Status code: " + statusCode);
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
                    showAlert("Error", "Failed to save weight. Please try again later.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    if (result.equals("Success")) {
                        getActivity().runOnUiThread(() -> {
                            showAlert("Message", "Height successfully updated.");
                            fetchUseData(email);
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            showAlert("Error", result);
                        });
                    }
                } else {
                    int statusCode = response.code();
                    getActivity().runOnUiThread(() -> {
                        showAlert("Error", "Failed to save height. Status code: " + statusCode);
                    });
                }
            }
        });
    }



    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustomStyle);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null); // You can add an OnClickListener if needed
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to update chart with fetched data
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
        }
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
        if(category.equals("Weight")){
            user_input.setHint("Enter your weight (kg)");
        }else{
            user_input.setHint("Enter your Height (cm)");
        }



        AlertDialog dialog = builder.create();

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(category.equals("Weight")){
                    String input = user_input.getText().toString();
                    saveWeight(email, input);
                    dialog.dismiss();
                }else{
                    String input = user_input.getText().toString();
                    saveHeight(email, input);
                    dialog.dismiss();
                }
                fetchUseData(email);
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Dismiss the dialog when the OK button is clicked
            }
        });

        dialog.show();
    }

    private void fetchUseData(String userId) {
        OkHttpClient client = new OkHttpClient();
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
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {


                    String jsonData = response.body().string();
                    if(jsonData.equals("Not found") || jsonData.isEmpty()){
                        getActivity().runOnUiThread(() -> {
                            showAlert("Info", "No user data found.");
                        });
                        return; // Exit early if data is not found
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);

                        final String currentHeight = jsonObject.getString("Height") ;
                        final String currentWeight = jsonObject.getString("Weight");


    // Convert height from cm to meters
                        double heightInMeters = Double.parseDouble(currentHeight) / 100.0;

    // Convert weight from kg to double
                        double weightInKg = Double.parseDouble(currentWeight);

    // Calculate BMI
                        double BMI = weightInKg / (heightInMeters * heightInMeters);

    // Format BMI to display with two decimal places
                        String formattedBMI = String.format("%.2f", BMI);

                        final String goalweight = jsonObject.getString("goalweight") + " kg";

                        getActivity().runOnUiThread(() -> {
                            View colorIndicator = getActivity().findViewById(R.id.color_indicator);
                            TextView currentWeightTextView = getActivity().findViewById(R.id.currentWeight);
                            TextView goalText = getActivity().findViewById(R.id.goalText);
                            TextView BMIText = getActivity().findViewById(R.id.BMIText);
                            TextView currentHeightTextView = getActivity().findViewById(R.id.currentHeight);
                            TextView fitnessCategory = getActivity().findViewById(R.id.fitnessCategory);
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
                            } else if (BMI >  15.9 && BMI <= 18.4) {
                                fitnesscategory = "Underweight";
                                colorIndicator.setBackgroundResource(R.color.colorUnderweight);
                                pointer2.setVisibility(View.VISIBLE);
                            } else if (BMI > 18.5 && BMI <= 24.9) {
                                fitnesscategory = "Normal";
                                colorIndicator.setBackgroundResource(R.color.colorNormal);
                                pointer3.setVisibility(View.VISIBLE);
                            } else if (BMI > 25 && BMI <= 29.9) {
                                fitnesscategory = "Overweight";
                                colorIndicator.setBackgroundResource(R.color.colorObesity1);
                                pointer4.setVisibility(View.VISIBLE);
                            } else if (BMI >= 30 && BMI <= 34.9) {
                                fitnesscategory = "Obesity Class I";
                                colorIndicator.setBackgroundResource(R.color.colorObesity2);
                                pointer5.setVisibility(View.VISIBLE);
                            } else if (BMI >= 35 && BMI <= 39.9) {
                                fitnesscategory = "Obesity Class II";
                                colorIndicator.setBackgroundResource(R.color.colorExtremeObesity);
                                pointer6.setVisibility(View.VISIBLE);
                            } else {
                                fitnesscategory = "Extreme Obesity";
                                colorIndicator.setBackgroundResource(R.color.colorExtremeObesity);
                                pointer6.setVisibility(View.VISIBLE);
                            }

                            currentWeightTextView.setText(currentWeight+ " kg");
                            goalText.setText(goalweight );
                            BMIText.setText(formattedBMI); // Set the formatted BMI
                            currentHeightTextView.setText(currentHeight + " cm");
                            fitnessCategory.setText(fitnesscategory);
                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    System.out.println("Failed to fetch weight log: " + response.code());
                    // Display an error message in an AlertDialog
                    getActivity().runOnUiThread(() -> {
                        showAlert("Error", "Failed to fetch weight log: " + response.code());
                    });
                }
            }

        });
    }
}