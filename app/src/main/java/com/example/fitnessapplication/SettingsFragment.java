package com.example.fitnessapplication;

import android.content.Intent;
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
                Intent intent = new Intent(getActivity(), Account.class);
                intent.putExtra("username", getActivity().getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        subscriptionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Subscription.class);
                intent.putExtra("username", getActivity().getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        pendingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Pending.class);
                intent.putExtra("username", getActivity().getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        historyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeightLogs.class);
                intent.putExtra("username", getActivity().getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        logoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout
            }
        });

        bmiContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BMI.class);
                startActivity(intent);
            }
        });

        bmrContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BMR.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
