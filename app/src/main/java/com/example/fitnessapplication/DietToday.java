package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DietToday extends Fragment {

    private TextView breakfastTitle, lunchTitle, dinnerTitle,dayplan;
    private LinearLayout breakfastLayout, lunchLayout, dinnerLayout;
    private ProgressDialog progressDialog;

    public DietToday() {
        // Required empty public constructor
    }

    public static DietToday newInstance() {
        return new DietToday();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_today, container, false);

        // Initialize UI elements
        breakfastTitle = view.findViewById(R.id.breakfastTitle);
        lunchTitle = view.findViewById(R.id.lunchTitle);
        dinnerTitle = view.findViewById(R.id.dinnerTitle);
        breakfastLayout = view.findViewById(R.id.breakfastLayout);
        lunchLayout = view.findViewById(R.id.lunchLayout);
        dinnerLayout = view.findViewById(R.id.dinnerLayout);
        dayplan = view.findViewById(R.id.day);
        String day = UserDataManager.getInstance(getContext()).getDietPlanDay();

        if(day.equals("1")){
            dayplan.setText("Monday Food Plan");
        } else if (day.equals("2")) {
            dayplan.setText("Tueday Food Plan");
        }
        else if (day.equals("3")) {
            dayplan.setText("Wednesday Food Plan");
        }
        else if (day.equals("4")) {
            dayplan.setText("Thursday Food Plan");
        }else if (day.equals("5")) {
            dayplan.setText("Friday Food Plan");
        }
        else if (day.equals("6")) {
            dayplan.setText("Saturday Food Plan");
        }
        else if (day.equals("7")) {
            dayplan.setText("Sunday Food Plan");
        }

        // Show progress dialog
//        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Fetching data...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        // Make HTTP request to fetch data
        fetchDataFromApi();

        return view;
    }

    private void fetchDataFromApi() {
        OkHttpClient client = new OkHttpClient();

        String baseUrl = URLManager.MY_URL;
        String username = UserDataManager.getInstance(getContext()).getEmail();
        String workoutPlanId = UserDataManager.getInstance(getContext()).getDietPlanId();
        String day = UserDataManager.getInstance(getContext()).getDietPlanDay();
        String url = baseUrl + "/Gym_Website/user/api/fetch_dietplan_data.php?";
        url += "IdNum=" + username + "&";  // Separate parameters with "&"
        url += "dietplanid=" + workoutPlanId + "&";
        url += "day=" + day;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
//                progressDialog.dismiss();
                showErrorDialog("Failed to fetch data. Please try again later.: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Show alert dialog with server response
//                                showAlert(responseData);
                                // Parse JSON response and populate UI
                                parseAndPopulateUI(responseData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    showErrorDialog("Failed to fetch data. Please try again later."+ response.body().toString());
                }
            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Server Response");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void parseAndPopulateUI(String responseData) throws JSONException {
        JSONArray jsonArray = new JSONArray(responseData);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject foodObject = jsonArray.getJSONObject(i);
            String mealType = foodObject.getString("mealtype");
            String foodid = foodObject.getString("id");
            String foodName = foodObject.getString("name");
            String servingSize = String.valueOf(foodObject.getInt("Serving"));
            String image = foodObject.getString("image");


            // Determine which layout to populate based on the meal type
            LinearLayout layout;
            switch (mealType) {
                case "breakfast":
                    layout = breakfastLayout;
                    break;
                case "lunch":
                    layout = lunchLayout;
                    break;
                case "dinner":
                    layout = dinnerLayout;
                    break;
                default:
                    // Skip if meal type is not recognized
                    continue;
            }

            // Inflate item_todays_food.xml layout
            View foodItemView = getLayoutInflater().inflate(R.layout.item_todays_food, null);

            // Set food name and serving size
            TextView foodNameTextView = foodItemView.findViewById(R.id.foodname);
            ImageView foodimage = foodItemView.findViewById(R.id.foodImage);
            ImageView info = foodItemView.findViewById(R.id.foodinfo);
            TextView foodServingTextView = foodItemView.findViewById(R.id.foodserving);
            info.setTag(foodid);
            foodNameTextView.setText(foodName);
            foodServingTextView.setText("Serving: " + servingSize);
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.loading) // Optional: Placeholder image while loading
                    .error(R.drawable.notfound) // Optional: Error image if the image fails to load
                    .into(foodimage);
            // Add the inflated layout to the appropriate meal section's LinearLayout
            layout.addView(foodItemView);
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}
