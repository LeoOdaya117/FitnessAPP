package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Discover#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Discover extends Fragment {

    private TextView navWorkouts;
    private TextView navExercise;
    private TextView navFood;
    private TextView navEquipment;
    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;

    private EditText search_text;
    private ImageView searchbutton, info_icon;

    private ScrollView workouts_container;
    private ImageView scrollUpButton;
    private CardView warmupcard,absBeginner,chestBeginner,armBeginner,legBeginner,backBeginner,absIntermediate,chestIntermediate,armIntermediate,legIntermediate,backIntermediate,absAdvanced,chestADVANCED,armADVANCED,legADVANCED,backADVANCED;

    private static String category;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Discover() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Discover.
     */
    // TODO: Rename and change types and number of parameters
    public static Discover newInstance(String param1, String param2) {
        Discover fragment = new Discover();
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
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        search_text = view.findViewById(R.id.search_input); // Initialize EditText for search
        searchbutton = view.findViewById(R.id.searchbtn); // Initialize EditText for search
        workouts_container = view.findViewById(R.id.workouts_container);
        workouts_container.setVisibility(View.VISIBLE); // Add this line to make it visible

        recyclerView = view.findViewById(R.id.recycler_view_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize your exercise list with an empty list
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList);
        recyclerView.setAdapter(exerciseAdapter);

        workouts_container.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        // Fetch data from the server

        scrollUpButton = view.findViewById(R.id.scroll_up_button);

         warmupcard = view.findViewById(R.id.warmupcard);
         absBeginner = view.findViewById(R.id.absBeginner);
         chestBeginner = view.findViewById(R.id.chestBeginner);
         armBeginner = view.findViewById(R.id.armBeginner);
         legBeginner = view.findViewById(R.id.legBeginner);
         backBeginner = view.findViewById(R.id.backBeginner);
         absIntermediate = view.findViewById(R.id.absIntermediate);
         chestIntermediate = view.findViewById(R.id.chestIntermediate);
         armIntermediate = view.findViewById(R.id.armIntermediate);
         legIntermediate = view.findViewById(R.id.legIntermediate);
         backIntermediate = view.findViewById(R.id.backIntermediate);
         absAdvanced = view.findViewById(R.id.absAdvanced);
         chestADVANCED = view.findViewById(R.id.chestADVANCED);
         armADVANCED = view.findViewById(R.id.armADVANCED);
         legADVANCED = view.findViewById(R.id.legADVANCED);
         backADVANCED = view.findViewById(R.id.backADVANCED);

        // Set click listener for all CardViews
        setCardClickListener(warmupcard, "Warm Up");
        setCardClickListener(absBeginner, "Abs Beginner");
        setCardClickListener(chestBeginner, "Chest Beginner");
        setCardClickListener(armBeginner, "Arm Beginner");
        setCardClickListener(legBeginner, "Leg Beginner");
        setCardClickListener(backBeginner, "Back Beginner");
        setCardClickListener(absIntermediate, "Abs Intermediate");
        setCardClickListener(chestIntermediate, "Chest Intermediate");
        setCardClickListener(armIntermediate, "Arm Intermediate");
        setCardClickListener(legIntermediate, "Leg Intermediate");
        setCardClickListener(backIntermediate, "Back Intermediate");
        setCardClickListener(absAdvanced, "Abs Advanced");
        setCardClickListener(chestADVANCED, "Chest Advanced");
        setCardClickListener(armADVANCED, "Arm Advanced");
        setCardClickListener(legADVANCED, "Leg Advanced");
        setCardClickListener(backADVANCED, "Back Advanced");


        // Set an OnClickListener to the scroll up button
        scrollUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll the RecyclerView to the top position
                if (recyclerView != null) {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the user has scrolled past a certain threshold
                if (dy > 0 && scrollUpButton.getVisibility() == View.GONE) {
                    // Scrolling down, show the scroll up button
                    scrollUpButton.setVisibility(View.VISIBLE);
                } else if (dy < 0 && scrollUpButton.getVisibility() == View.VISIBLE) {
                    // Scrolling up, hide the scroll up button
                    scrollUpButton.setVisibility(View.GONE);
                }
            }
        });

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchData(search_text.getText().toString());
            }
        });
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called to notify you that the text is about to change.
                // You can use it to take any necessary actions before the text changes.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called to notify you that the text has changed.
                // You can perform actions based on the changed text here.
                String searchText = charSequence.toString();
                fetchData(searchText); // Call fetchData with the entered search text
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called to notify you that the text has changed after any transformations have been applied.
                // You can use it to take any necessary actions after the text changes.
            }
        });

        // Initialize TextViews
        navWorkouts = view.findViewById(R.id.nav_workouts);
        navExercise = view.findViewById(R.id.nav_exercise);
        navFood = view.findViewById(R.id.nav_food);
        navEquipment = view.findViewById(R.id.nav_equipment);

        // Set OnClickListener for each TextView
        navWorkouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset appearance of all TextViews
                workouts_container.setVisibility(View.VISIBLE); // Ensure workouts container is visible
                resetTextViewsAppearance();
                category = "workouts";
                // Update appearance of clicked TextView
                navWorkouts.setTypeface(null, Typeface.BOLD);
                navWorkouts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                scrollUpButton.setVisibility(View.GONE);

                recyclerView.setVisibility(View.GONE);
                // Handle click action for Workouts
            }
        });

        navExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset appearance of all TextViews
                resetTextViewsAppearance();
                category = "exercise";

                // Update appearance of clicked TextView
                navExercise.setTypeface(null, Typeface.BOLD);
                navExercise.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                fetchData(search_text.getText().toString());
                workouts_container.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                // Handle click action for Workouts
            }
        });

        navFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset appearance of all TextViews
                resetTextViewsAppearance();
                category = "food";
                fetchData(search_text.getText().toString());
                workouts_container.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                // Update appearance of clicked TextView
                navFood.setTypeface(null, Typeface.BOLD);
                navFood.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                // Handle click action for Workouts
            }
        });

        navEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset appearance of all TextViews
                resetTextViewsAppearance();
                category = "equipment";
                fetchData(search_text.getText().toString());
                workouts_container.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                // Update appearance of clicked TextView
                navEquipment.setTypeface(null, Typeface.BOLD);
                navEquipment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                // Handle click action for Workouts
            }
        });




        // Inflate the layout for this fragment
        return view;
    }

    private void setCardClickListener(CardView cardView, final String category) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start WorkoutActivity and pass the category name as an extra
                Intent intent = new Intent(getContext(), Workout.class);
                intent.putExtra("category", category);
                startActivity(intent);
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

    public static String getCategory() {
        return category;
    }

    // Method to fetch data from the server
    private void fetchData(String search) {
        FetchDataTask task = new FetchDataTask(new FetchDataTask.OnDataFetchedListener() {
            @Override
            public void onDataFetched(List<Exercise> fetchedExerciseList) {
                // Update the exercise list with the fetched data
                exerciseList.clear();
                exerciseList.addAll(fetchedExerciseList);
                // Notify the adapter that the data set has changed
                exerciseAdapter.notifyDataSetChanged();

                // Display an alert showing the number of exercises fetched
//                showAlert("Data Fetched", "Fetched " + fetchedExerciseList.size() + " exercises.");
            }
        });
        task.execute(search);
    }


    private void resetTextViewsAppearance() {
        navWorkouts.setTypeface(null, Typeface.NORMAL);
        navWorkouts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        navExercise.setTypeface(null, Typeface.NORMAL);
        navExercise.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        navFood.setTypeface(null, Typeface.NORMAL);
        navFood.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        navEquipment.setTypeface(null, Typeface.NORMAL);
        navEquipment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    }
}
