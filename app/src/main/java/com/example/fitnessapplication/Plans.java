package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Plans#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Plans extends Fragment {

    private OnPlanSelectedListener listener;
    public String websiteurl = URLManager.MY_URL;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Plans() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Plans.
     */
    // TODO: Rename and change types and number of parameters
    public static Plans newInstance(String param1, String param2) {
        Plans fragment = new Plans();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPlanSelectedListener) {
            listener = (OnPlanSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnPlanSelectedListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_plans, container, false);

        ImageView workoutImageView = rootView.findViewById(R.id.workoutplan);
        ImageView dietImageView = rootView.findViewById(R.id.dietplan);
        CardView cardadd = rootView.findViewById(R.id.buttonBelowDietCard); // Find the add button

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.xml.button_animation);



        workoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workoutImageView.startAnimation(animation);
                FragmentManager fragmentManager = getParentFragmentManager();
                Fragment newFragment = new WorkoutPlans();
                WEB.replaceFragment(fragmentManager, newFragment);
                // Notify the hosting activity with the workout plan URL
//                if (listener != null) {
//                    listener.onPlanSelected(websiteurl + "/User/workoutplan_noheader.php");
//                }
            }
        });

        dietImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dietImageView.startAnimation(animation);
                // Notify the hosting activity with the diet plan URL
                FragmentManager fragmentManager = getParentFragmentManager();
                Fragment newFragment = new DietPlans();
                WEB.replaceFragment(fragmentManager, newFragment);
//                if (listener != null) {
//                    listener.onPlanSelected(websiteurl + "/User/dietplans_noheader.php");
//                }
            }
        });


        cardadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog
                cardadd.startAnimation(animation);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to create new Workout and Diet Plan?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Start SignupAge activity when user confirms

                        Intent intent = new Intent(getActivity(), SignupGender.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog if user cancels
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });



        return rootView;
    }

    public interface OnPlanSelectedListener {
        void onPlanSelected(String url);
    }

}