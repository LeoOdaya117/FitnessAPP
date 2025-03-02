package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitnessapplication.CustomRecyclerView;



import java.util.ArrayList;
import java.util.List;

public class Discover extends Fragment {

    private CustomRecyclerView recyclerView;
    private boolean isCategoryChanging = false;

    private boolean isScrollingEnabled = true;
    private TextView navWorkouts;
    private TextView navExercise;
    private TextView navFood;
    private TextView navEquipment;
    private TextView navWarmup;

    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;

    private EditText search_text;
    private ImageView searchbutton,scrollUpButton;

    private ScrollView workouts_container;
    private CardView warmupcard, absBeginner, chestBeginner, armBeginner, legBeginner, backBeginner,
            absIntermediate, chestIntermediate, armIntermediate, legIntermediate, backIntermediate,
            absAdvanced, chestADVANCED, armADVANCED, legADVANCED, backADVANCED;

    private static String category;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;


    public Discover() {
        // Required empty public constructor
    }

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


        search_text = view.findViewById(R.id.search_input);
        searchbutton = view.findViewById(R.id.searchbtn);
        workouts_container = view.findViewById(R.id.workouts_container);
        workouts_container.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recycler_view_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList);
        recyclerView.setAdapter(exerciseAdapter);

        workouts_container.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        scrollUpButton = view.findViewById(R.id.scroll_up_button);

        // Initialize CardView instances
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

        // Set click listeners for category cards
        setCardClickListener(warmupcard, "Warm Up");
        setCardClickListener(absBeginner, "Abs Beginner");
        setCardClickListener(chestBeginner, "Chest Beginner");
        setCardClickListener(armBeginner, "Arm Beginner");
        setCardClickListener(legBeginner, "Leg Beginner");
        setCardClickListener(backBeginner, "Shoulder & Back Beginner");
        setCardClickListener(absIntermediate, "Abs Intermediate");
        setCardClickListener(chestIntermediate, "Chest Intermediate");
        setCardClickListener(armIntermediate, "Arm Intermediate");
        setCardClickListener(legIntermediate, "Leg Intermediate");
        setCardClickListener(backIntermediate, "Shoulder & Back Intermediate");
        setCardClickListener(absAdvanced, "Abs Advanced");
        setCardClickListener(chestADVANCED, "Chest Advanced");
        setCardClickListener(armADVANCED, "Arm Advanced");
        setCardClickListener(legADVANCED, "Leg Advanced");
        setCardClickListener(backADVANCED, "Shoulder & Back Advanced");

        // OnClickListener for scroll up button
        scrollUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll the RecyclerView to the top position
                if (recyclerView != null) {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });

        recyclerView.addOnScrollListener(new CustomRecyclerView.OnScrollListener() {
            private static final int SCROLL_THRESHOLD = 20; // Adjust this threshold as needed

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the absolute value of dy is greater than the threshold (indicating a significant scroll)
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    if (dy > 0 && scrollUpButton.getVisibility() != View.VISIBLE) {
                        // Scrolling downward, show the scroll up button
                        scrollUpButton.setVisibility(View.VISIBLE);
                    } else if (dy < 0 && scrollUpButton.getVisibility() == View.VISIBLE) {
                        // Scrolling upward, hide the scroll up button
                        scrollUpButton.setVisibility(View.GONE);
                    }
                }
            }
        });





        // Text changed listener for search
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Initialize TextViews for navigation
        navWorkouts = view.findViewById(R.id.nav_workouts);
        navWarmup = view.findViewById(R.id.nav_warmup);
        navExercise = view.findViewById(R.id.nav_exercise);
        navFood = view.findViewById(R.id.nav_food);
        navEquipment = view.findViewById(R.id.nav_equipment);

        // Set OnClickListener for each navigation item
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

//        navWorkouts.setOnClickListener(v -> changeCategory("workouts"));
        navWarmup.setOnClickListener(v -> changeCategory("warmup"));
        navExercise.setOnClickListener(v -> changeCategory("exercise"));
        navFood.setOnClickListener(v -> changeCategory("food"));
        navEquipment.setOnClickListener(v -> changeCategory("equipment"));

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

    private void changeCategory(String category) {
        workouts_container.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        resetTextViewsAppearance();
        // Update UI for selected category
        TextView selectedTextView = getCategoryTextView(category);
        if (selectedTextView != null) {
            selectedTextView.setTypeface(null, Typeface.BOLD);
            selectedTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        // Disable RecyclerView interaction
        isScrollingEnabled = false;

        // Stop the RecyclerView from scrolling immediately
        if (recyclerView != null) {
            recyclerView.stopScroll();
        }

        // Update category data
        fetchData(search_text.getText().toString());

        // Delay category change to avoid conflicts with ongoing RecyclerView operations
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            this.category = category;
            workouts_container.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            isScrollingEnabled = true; // Re-enable RecyclerView interaction
            isCategoryChanging = false; // Allow next category change
        }, 1000); // Adjust delay as needed
    }



    public static String getCategory() {
        return category;
    }
    private TextView getCategoryTextView(String category1) {
        switch (category1) {
            case "workouts":
                category = "workouts";
                return navWorkouts;
            case "warmup":
                category = "warmup";
                return navWarmup;
            case "exercise":
                category = "exercise";
                return navExercise;
            case "food":
                category = "food";
                return navFood;
            case "equipment":
                category = "equipment";
                return navEquipment;
            default:
                return null;
        }
    }

    private void resetTextViewsAppearance() {
        // Reset appearance of all TextViews
        navWorkouts.setTypeface(null, Typeface.NORMAL);
        navWorkouts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        navWarmup.setTypeface(null, Typeface.NORMAL);
        navWarmup.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        navExercise.setTypeface(null, Typeface.NORMAL);
        navExercise.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        navFood.setTypeface(null, Typeface.NORMAL);
        navFood.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        navEquipment.setTypeface(null, Typeface.NORMAL);
        navEquipment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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
}
