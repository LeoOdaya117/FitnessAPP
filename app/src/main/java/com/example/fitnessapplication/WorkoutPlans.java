package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutPlans#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutPlans extends Fragment {

    private View rootView; // Define rootView as a class-level variable

    private OkHttpClient client;
    private RecyclerView recyclerView;
    private WorkoutPlanAdapter adapter;
    private List<WorkoutPlan> workoutPlanList;
    private TextView currentweekTextView;
    private TextView currentdetailsTextView, navplans;

    private CardView CurrentWorkPlan, nocurrentrecord,nopreviousrecord;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WorkoutPlans() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutPlans.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutPlans newInstance(String param1, String param2) {
        WorkoutPlans fragment = new WorkoutPlans();
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
        rootView = inflater.inflate(R.layout.fragment_workout_plans, container, false); // Assign rootView here

        client = new OkHttpClient();
        // Find the RecyclerView from the layout
        recyclerView = rootView.findViewById(R.id.workoutCon);
        currentweekTextView = rootView.findViewById(R.id.currentWeek);
        currentdetailsTextView =rootView.findViewById(R.id.currentWeektext);
        CurrentWorkPlan=rootView.findViewById(R.id.CurrentWorkPlan);
        navplans = rootView.findViewById(R.id.navplans);
        // Create a layout manager for the RecyclerView (optional)
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Initialize the adapter with an empty list
        workoutPlanList = new ArrayList<>();
        adapter = new WorkoutPlanAdapter(workoutPlanList);
        recyclerView.setAdapter(adapter);

        // Fetch workout plans from the server
        fetchWorkoutPlans();



        CurrentWorkPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserDataManager.getInstance(getContext()).saveWorkoutPlanId(currentweekTextView.getText().toString());
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WorkoutPlanDays());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();
            }
        });

        navplans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new Plans());
                transaction.addToBackStack(null); // Optional: Add transaction to back stack
                transaction.commit();
            }
        });


        return rootView;
    }



    private void fetchWorkoutPlans() {
        UserDataManager userDataManager = UserDataManager.getInstance(requireContext());
        String username = userDataManager.getEmail();
        // Assuming your PHP server URL is stored in a constant named SERVER_URL
        String url = URLManager.MY_URL + "/User/api/get_workout_plans.php?IdNum=" + username;


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
                        Toast.makeText(getContext(), "Failed to fetch workout plans. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Process the response JSON
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray currentPlans = jsonObject.getJSONArray("currentWorkoutPlan");
                        JSONArray previousPlans = jsonObject.getJSONArray("previousWorkoutPlans");
                        // Process the JSON data and update UI
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Clear the existing workout plan list
                                workoutPlanList.clear();

                                // Process the current workout plan
                                if (currentPlans.length() == 0) {
                                    // If current plan is empty, show the "No Current Workout Plan Records" card
                                    CurrentWorkPlan.setVisibility(View.GONE);
                                    rootView.findViewById(R.id.nocurrentrecord).setVisibility(View.VISIBLE);
                                } else {
                                    // If current plan exists, hide the "No Current Workout Plan Records" card
                                    CurrentWorkPlan.setVisibility(View.VISIBLE);
                                    rootView.findViewById(R.id.nocurrentrecord).setVisibility(View.GONE);

                                    // Assuming there's only one current plan
                                    JSONObject currentPlan = null;
                                    try {
                                        currentPlan = currentPlans.getJSONObject(0);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    String currentTitle = null;
                                    try {
                                        currentTitle = currentPlan.getString("WorkoutPlanID");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    String currentDetails = "Workout Plan details for Week " + currentTitle;
                                    currentweekTextView.setText(currentTitle);
                                    currentdetailsTextView.setText(currentDetails);
                                }

                                // Process the previous workout plans
                                if (previousPlans.length() == 0) {
                                    // If no previous plans, show the "No Previous Workout Plan Records" card
                                    rootView.findViewById(R.id.nopreviousrecord).setVisibility(View.VISIBLE);
                                } else {
                                    // If previous plans exist, hide the "No Previous Workout Plan Records" card
                                    rootView.findViewById(R.id.nopreviousrecord).setVisibility(View.GONE);

                                    // Add previous workout plans
                                    for (int i = 0; i < previousPlans.length(); i++) {
                                        try {
                                            JSONObject previousPlan = previousPlans.getJSONObject(i);
                                            String title = previousPlan.getString("WorkoutPlanID");
                                            String details = "Workout Plan details for Week " + title;
                                            workoutPlanList.add(new WorkoutPlan(title, details));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                // Notify the adapter that the data set has changed
                                adapter.notifyDataSetChanged();
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
                            Toast.makeText(getContext(), "Failed to fetch workout plans. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
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