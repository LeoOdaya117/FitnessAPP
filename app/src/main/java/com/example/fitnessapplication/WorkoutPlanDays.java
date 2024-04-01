package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutPlanDays#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutPlanDays extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CardView mondaycard,tuesdaycard,wednesdaycard,thursdaycard,fridaycard,saturdaycard,sundaycard;

    private TextView navPlansTextView;
    private TextView navWorkoutPlansTextView;
    private TextView navWeekPlanTextView;
    public WorkoutPlanDays() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutPlanDays.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutPlanDays newInstance(String param1, String param2) {
        WorkoutPlanDays fragment = new WorkoutPlanDays();
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

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workout_plan_days, container, false);

        // Find the RecyclerView from the layout
        mondaycard = rootView.findViewById(R.id.Mondaycard);
        tuesdaycard = rootView.findViewById(R.id.Tuesdaycard);
        wednesdaycard = rootView.findViewById(R.id.WednesdayCard);
        thursdaycard = rootView.findViewById(R.id.Thursdaycard);
        fridaycard = rootView.findViewById(R.id.Fridaycard);
        saturdaycard = rootView.findViewById(R.id.Saturdaycard);
        sundaycard = rootView.findViewById(R.id.Sundaycard);

        navPlansTextView = rootView.findViewById(R.id.navplans);
        navWorkoutPlansTextView = rootView.findViewById(R.id.navWorkoutplans);
        navWeekPlanTextView = rootView.findViewById(R.id.navWeekplan);


        // Set onC

        mondaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("0");
//                String planid = UserDataManager.getInstance(getContext()).getWorkoutPlanId();

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

//                // Create AlertDialog.Builder instance
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//                // Set title and message
//                builder.setTitle("Data");
//                builder.setMessage("WorkoutPlanID: " + planid + "\n" + "Day: " + UserDataManager.getInstance(getContext()).getWorkoutPlanDay());
//
//                // Add positive button
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//                    }
//                });
//
//                // Add negative button
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // User clicked No, dismiss the dialog
//                        dialog.dismiss();
//                    }
//                });
//
//                // Create and show the AlertDialog
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
            }
        });


        tuesdaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("1");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        wednesdaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("2");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        thursdaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("3");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        fridaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("4");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        saturdaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("5");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        sundaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("6");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });



        navPlansTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("6");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new Plans());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        navWorkoutPlansTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveWorkoutPlanDay("6");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutPlans());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });



        // Inflate the layout for this fragment
        return rootView;
    }


}