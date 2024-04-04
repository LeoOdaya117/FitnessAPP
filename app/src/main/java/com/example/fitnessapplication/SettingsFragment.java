package com.example.fitnessapplication;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Get references to your LinearLayout containers
        LinearLayout accountContainer = rootView.findViewById(R.id.account_container);
        LinearLayout subscriptionContainer = rootView.findViewById(R.id.subscription_container);
        LinearLayout pendingContainer = rootView.findViewById(R.id.pending_container);
        LinearLayout historyContainer = rootView.findViewById(R.id.history_container);
        LinearLayout logoutContainer = rootView.findViewById(R.id.logout_container);
        LinearLayout bmiContainer = rootView.findViewById(R.id.bmi_container);
        LinearLayout bmrContainer = rootView.findViewById(R.id.bmr_container);

        // Set click listeners for each container
        accountContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAccountActivity();
            }
        });

        subscriptionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSubscriptionActivity();
            }
        });

        pendingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPendingActivity();
            }
        });

        historyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToWeightLogsActivity();
            }
        });

        logoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        bmiContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBMIActivity();
            }
        });

        bmrContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBMRActivity();
            }
        });

        return rootView;
    }

    // Method to navigate to the Account activity
    private void navigateToAccountActivity() {
        Intent intent = new Intent(getActivity(), Account.class);
        startActivity(intent);
    }

    // Method to navigate to the Subscription activity
    private void navigateToSubscriptionActivity() {
        Intent intent = new Intent(getActivity(), Subscription.class);
        startActivity(intent);
    }

    // Method to navigate to the Pending activity
    private void navigateToPendingActivity() {
        Intent intent = new Intent(getActivity(), Pending.class);
        startActivity(intent);
    }

    // Method to navigate to the WeightLogs activity
    private void navigateToWeightLogsActivity() {
        Intent intent = new Intent(getActivity(), WeightLogs.class);
        startActivity(intent);
    }

    // Method to handle logout
    private void logout() {
        // Clear any user session or credentials
        // For example, if you're using SharedPreferences to store user credentials, clear them here
        SharedPreferences preferences = getActivity().getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to the login screen
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish(); // Finish the hosting activity to prevent the user from navigating back to it with the back button
    }

    // Method to navigate to the BMI activity
    private void navigateToBMIActivity() {
        Intent intent = new Intent(getActivity(), BMI.class);
        startActivity(intent);
    }

    // Method to navigate to the BMR activity
    private void navigateToBMRActivity() {
        Intent intent = new Intent(getActivity(), BMR.class);
        startActivity(intent);
    }
}
