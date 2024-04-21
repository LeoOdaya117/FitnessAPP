package com.example.fitnessapplication;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.List;

public class WorkoutToday extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private WorkoutPlanExerciseAdapter adapter;
    private CardView restDayCard;

    private TextView navPlansTextView;
    private TextView navWorkoutPlansTextView;
    private TextView navWeekPlanTextView;
    private TextView navExerciseListTextView,dayplan;

    public WorkoutToday() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout_today, container, false);
        recyclerView = rootView.findViewById(R.id.exerciseTodayContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        restDayCard = rootView.findViewById(R.id.restDayCard);

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


}
