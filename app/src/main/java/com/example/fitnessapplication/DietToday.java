package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

public class DietToday extends Fragment implements View.OnClickListener {

    private TextView breakfastTitle, lunchTitle, dinnerTitle,dayplan;
    private LinearLayout breakfastLayout, lunchLayout, dinnerLayout,snacksLayout;
    private ProgressDialog progressDialog;

    private TextView navPlansTextView;
    private TextView navWorkoutPlansTextView;
    private TextView navWeekPlanTextView;
    private TextView navExerciseListTextView;
    private static final OkHttpClient client = new OkHttpClient();

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
        snacksLayout = view.findViewById(R.id.snacksLayout);

        dayplan = view.findViewById(R.id.day);

        navPlansTextView = view.findViewById(R.id.navplans);
        navWorkoutPlansTextView = view.findViewById(R.id.navWorkoutplans);
        navWeekPlanTextView = view.findViewById(R.id.navWeekplan);
        navExerciseListTextView = view.findViewById(R.id.navexerciselist);


        // Set onClick listeners
        navPlansTextView.setOnClickListener(this);
        navWorkoutPlansTextView.setOnClickListener(this);
        navWeekPlanTextView.setOnClickListener(this);
        navExerciseListTextView.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        // Handle onClick events for TextViews
        int viewId = v.getId();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (viewId == R.id.navplans) {
            // Handle click for nav plans
            transaction.replace(R.id.fragment_container, new Plans());
            transaction.addToBackStack(null); // Optional: Add transaction to back stack
            transaction.commit();
        } else if (viewId == R.id.navWorkoutplans) {
            // Handle click for nav workout plans
            transaction.replace(R.id.fragment_container, new DietPlans());
            transaction.addToBackStack(null); // Optional: Add transaction to back stack
            transaction.commit();
        } else if (viewId == R.id.navWeekplan) {
            // Handle click for nav week plan
            transaction.replace(R.id.fragment_container, new DietPlanDays());
            transaction.addToBackStack(null); // Optional: Add transaction to back stack
            transaction.commit();
        } else if (viewId == R.id.navexerciselist) {
            // Handle click for nav exercise list
        }
    }

    private void showFoodModal(Context context, String exerciseId, String name, Integer servingSize) {
        fetchDataFromAPI(exerciseId, "food", new ExerciseAdapter.ExerciseViewHolder.DataFetchCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    // Process the fetched data
                    String description = result.getString("description");
                    String calorie = result.getString("calories");
                    String carb = result.getString("carbohydrates");
                    String protein = result.getString("protein");
                    String fat = result.getString("fat");

                    int calorieInt = Integer.parseInt(calorie);
                    int carbInt = Integer.parseInt(carb);
                    int proteinInt = Integer.parseInt(protein);
                    int fatInt = Integer.parseInt(fat);
                    // Create a handler with the main looper
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Inflate the custom alert dialog layout
                            View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_for_food, null);
                            Animation animation = AnimationUtils.loadAnimation(getContext(), R.xml.button_animation);

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

                            if(servingSize < 4){

                                modalCalorie.setText(String.valueOf(servingSize * calorieInt) + " kcal");
                                modalCarb.setText( String.valueOf(servingSize * carbInt) + " g");
                                modalProtein.setText( String.valueOf(servingSize * proteinInt) + " g");
                                modalFat.setText( String.valueOf(servingSize * fatInt) + " g");
                            }else{
                                modalCalorie.setText(calorie + " kcal");
                                modalCarb.setText( carb + " g");
                                modalProtein.setText( protein + " g");
                                modalFat.setText( fat + " g");
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

    private void fetchDataFromApi() {
        OkHttpClient client = new OkHttpClient();

        String baseUrl = URLManager.MY_URL;
        String username = UserDataManager.getInstance(getContext()).getEmail();
        String workoutPlanId = UserDataManager.getInstance(getContext()).getDietPlanId();
        String day = UserDataManager.getInstance(getContext()).getDietPlanDay();
        String url = baseUrl + "/User/api/fetch_dietplan_data.php?";
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
            int perServing = foodObject.getInt("PerServing");
            int servingSize = foodObject.getInt("Serving");
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
                case "snack":
                    layout = snacksLayout;
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

            if(servingSize < 4){
                foodServingTextView.setText("Serving: " + String.valueOf(servingSize * perServing) + " g");

            }
            else{
                foodServingTextView.setText("Serving: " + String.valueOf(servingSize )+ " g");

            }
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.loading) // Optional: Placeholder image while loading
                    .error(R.drawable.notfound) // Optional: Error image if the image fails to load
                    .into(foodimage);
            // Add the inflated layout to the appropriate meal section's LinearLayout
            layout.addView(foodItemView);

            // Set OnClickListener for the info ImageView
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.xml.button_animation);

                    info.startAnimation(animation);
                    // Get the exercise ID associated with the info ImageView
                    String exerciseId = (String) v.getTag();
                    // Get the food name associated with the TextView
                    String foodName = foodNameTextView.getText().toString();
                    // Call showFoodModal method
                    showFoodModal(getContext(), exerciseId, foodName, servingSize);
                }
            });

        }
    }

    private void fetchDataFromAPI(String id, String category, ExerciseAdapter.ExerciseViewHolder.DataFetchCallback callback) {
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
