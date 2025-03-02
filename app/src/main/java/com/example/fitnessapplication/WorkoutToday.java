package com.example.fitnessapplication;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapplication.WorkoutPlanExercise;
import com.example.fitnessapplication.WorkoutPlanExerciseAdapter;
import com.example.fitnessapplication.WorkoutPlanExerciseFetcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkoutToday extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private WorkoutPlanExerciseAdapter adapter;
    private CardView restDayCard;

    private TextView navPlansTextView;
    private TextView navWorkoutPlansTextView;
    private TextView navWeekPlanTextView;
    private TextView navExerciseListTextView,dayplan;


    private OkHttpClient client;
    public WorkoutToday() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout_today, container, false);
        client = new OkHttpClient();
        recyclerView = rootView.findViewById(R.id.exerciseTodayContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        restDayCard = rootView.findViewById(R.id.restDayCard);

        fetchWorkoutPlans();
        navPlansTextView = rootView.findViewById(R.id.navplans);
        navWorkoutPlansTextView = rootView.findViewById(R.id.navWorkoutplans);
        navWeekPlanTextView = rootView.findViewById(R.id.navWeekplan);
        navExerciseListTextView = rootView.findViewById(R.id.navexerciselist);
        dayplan = rootView.findViewById(R.id.day);

        // Set onClick listeners
        navPlansTextView.setOnClickListener(this);
        navWorkoutPlansTextView.setOnClickListener(this);
        navWeekPlanTextView.setOnClickListener(this);
        navExerciseListTextView.setOnClickListener(this);

        String day = UserDataManager.getInstance(getContext()).getWorkoutPlanDay();

        if(day.equals("0")){
            dayplan.setText("Monday Exercises");
        } else if (day.equals("1")) {
            dayplan.setText("Tueday Exercises");
        }
        else if (day.equals("2")) {
            dayplan.setText("Wednesday Exercises");
        }
        else if (day.equals("3")) {
            dayplan.setText("Thursday Exercises");
        }else if (day.equals("4")) {
            dayplan.setText("Friday Exercises");
        }
        else if (day.equals("5")) {
            dayplan.setText("Saturday Exercises");
        }
        else if (day.equals("6")) {
            dayplan.setText("Sunday Exercises");
        }


        return rootView;
    }

    @Override
    public void onClick(View v) {
        // Handle onClick events for TextViews
        int viewId = v.getId();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (viewId == R.id.navplans) {

            transaction.replace(R.id.fragment_container, new Plans());
            transaction.addToBackStack(null); // Optional: Add transaction to back stack
            transaction.commit();
        } else if (viewId == R.id.navWorkoutplans) {

            transaction.replace(R.id.fragment_container, new WorkoutPlans());
            transaction.addToBackStack(null); // Optional: Add transaction to back stack
            transaction.commit();
            // Handle click for nav workout plans
        } else if (viewId == R.id.navWeekplan) {

            transaction.replace(R.id.fragment_container, new WorkoutPlanDays());
            transaction.addToBackStack(null); // Optional: Add transaction to back stack
            transaction.commit();
            // Handle click for nav week plan
        } else if (viewId == R.id.navexerciselist) {
            // Handle click for nav exercise list
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchExercises();
    }

    private void fetchExercises() {
        WorkoutPlanExerciseFetcher exerciseFetcher = new WorkoutPlanExerciseFetcher(getContext());

        exerciseFetcher.fetchExercises(new WorkoutPlanExerciseFetcher.WorkoutPlanExerciseFetchListener() {
            @Override
            public void onExercisesFetched(List<WorkoutPlanExercise> exercises) {
                if (exercises.isEmpty()) {
                    // If no exercises are fetched, show the "Rest Day" card
                    showRestDayCard();
                } else {
                    // If exercises are fetched, display them in the RecyclerView
                    displayExercises(exercises);
                }
            }
        }, new WorkoutPlanExerciseFetcher.WorkoutPlanExerciseFetchErrorListener() {
            @Override
            public void onFetchFailed(IOException e) {
                showErrorDialog(e.getMessage());
            }
        });
    }

    private void displayExercises(List<WorkoutPlanExercise> exercises) {
        adapter = new WorkoutPlanExerciseAdapter(exercises);
        recyclerView.setAdapter(adapter);
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showRestDayCard() {
        restDayCard.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void fetchWorkoutPlans() {
        UserDataManager userDataManager = UserDataManager.getInstance(requireContext());
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Define date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Format the current date
        String formattedDate = dateFormat.format(currentDate);
        String username = userDataManager.getEmail();
        String day = userDataManager.getWorkoutPlanDay();
        String date = userDataManager.getCreatedDate();
        String workoutplanid = userDataManager.getWorkoutPlanId();
        // Assuming your PHP server URL is stored in a constant named SERVER_URL
        String url = URLManager.MY_URL + "/User/api/check_attendance_yesterday.php?email=" + username +"&date=" + date+"&day=" + day +"&workoutplanid=" + workoutplanid;


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show an error message to the user
                        Toast.makeText(getContext(), "Failed to attendance. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Process the response JSON
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);

                        final String status = jsonObject.getString("status");
                        final String datecreate = jsonObject.getString("date");
                        final String exerciseId = jsonObject.getString("exercise");
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                showServerResponse("Adjusting exercise sets, reps, and duration due to missed sessions or absence from workouts plans."+"\n\n" + "Date: "+datecreate);

                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date currentDate = new Date();
                                    Date responseDate = sdf.parse(datecreate);

                                    // Compare response date with current date
                                    if (responseDate != null && !responseDate.after(currentDate)) {
                                        if (status.equals("Absent") && !(exerciseId.equals("Rest Day"))) {
                                            // Adjust exercise based on absence
                                            showServerResponse("Adjusting exercise sets, reps, or duration due to missed sessions or absence from workouts plans." + "\n\n" + "Absent Date: " + datecreate);
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

//                                Toast.makeText(getContext(), "Status: " + status  + userDataManager.getCreatedDate()  + userDataManager.getCreatedDate() +1, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Show an error message to the user
                            Toast.makeText(getContext(), "Failed to fetch attendance. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showServerResponse(String response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.bmr_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);

// Display BMI, fitness category, and fitness goal recommendation
        titleTextView.setText("Exercise Adjustment");
        messageTextView.setText(response);

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Dismiss the dialog when the OK button is clicked


            }
        });

        dialog.show();

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
