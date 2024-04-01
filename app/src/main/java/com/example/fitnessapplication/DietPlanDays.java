package com.example.fitnessapplication;

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
 * Use the {@link DietPlanDays#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DietPlanDays extends Fragment {

    private CardView mondaycard,tuesdaycard,wednesdaycard,thursdaycard,fridaycard,saturdaycard,sundaycard;

    private TextView navPlansTextView;
    private TextView navWorkoutPlansTextView;
    private TextView navWeekPlanTextView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DietPlanDays() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DietPlanDays.
     */
    // TODO: Rename and change types and number of parameters
    public static DietPlanDays newInstance(String param1, String param2) {
        DietPlanDays fragment = new DietPlanDays();
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
        View rootView = inflater.inflate(R.layout.fragment_diet_plan_days, container, false);

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
                UserDataManager.getInstance(getContext()).saveDietPlanDay("1");
//                String planid = UserDataManager.getInstance(getContext()).getWorkoutPlanId();

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });


        tuesdaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanDay("2");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        wednesdaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanDay("3");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: DietToday transaction to back stack
                transaction.commit();

            }
        });

        thursdaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanDay("4");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        fridaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanDay("5");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        saturdaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanDay("6");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        sundaycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanDay("7");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });



        navPlansTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanDay("6");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });

        navWorkoutPlansTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanDay("6");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietToday());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();

            }
        });



        // Inflate the layout for this fragment
        return rootView;
    }
}