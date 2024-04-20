package com.example.fitnessapplication;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DietPlans extends Fragment {

    private View rootView; // Declare rootView as a class-level variable

    private TextView currentWeektext;
    private TextView currentDietplan, navplans, currentWeek;
    private RecyclerView recyclerView;
    private CardView currentDietplancard;

    private ArrayList<DietPlan> dietPlanList;
    private DietPlanAdapter adapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DietPlans() {
        // Required empty public constructor
    }

    public static DietPlans newInstance(String param1, String param2) {
        DietPlans fragment = new DietPlans();
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
        rootView = inflater.inflate(R.layout.fragment_diet_plans, container, false); // Assign rootView here

        recyclerView = rootView.findViewById(R.id.dietplancon);
        currentDietplancard = rootView.findViewById(R.id.currentDietplan);
        currentWeek = rootView.findViewById(R.id.currentWeek);
        currentWeektext = rootView.findViewById(R.id.currentWeektext);
        currentDietplan = rootView.findViewById(R.id.currentWeek);
        navplans = rootView.findViewById(R.id.navplans);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        dietPlanList = new ArrayList<>();
        adapter = new DietPlanAdapter(dietPlanList);
        recyclerView.setAdapter(adapter);

        fetchDietPlans();

        navplans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new Plans());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        currentDietplancard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager.getInstance(getContext()).saveDietPlanId(currentWeek.getText().toString());
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new DietPlanDays());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
    }



    private void fetchDietPlans() {
        UserDataManager userDataManager = UserDataManager.getInstance(requireContext());
        String username = userDataManager.getEmail();
        String url = URLManager.MY_URL + "/User/api/get_diet_plans.php?IdNum=" + username;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Failed to fetch diet plans. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray currentPlans = jsonObject.getJSONArray("currentDietPlan");
                        JSONArray previousPlans = jsonObject.getJSONArray("previousDietPlans");

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dietPlanList.clear();

                                if (currentPlans.length() == 0) {
                                    // If current plan is empty, show the "No Current Diet Plan Records" card
                                    currentDietplancard.setVisibility(View.GONE);
                                    rootView.findViewById(R.id.nocurrentrecord).setVisibility(View.VISIBLE);
                                } else {
                                    // If current plan exists, hide the "No Current Diet Plan Records" card
                                    currentDietplancard.setVisibility(View.VISIBLE);
                                    rootView.findViewById(R.id.nocurrentrecord).setVisibility(View.GONE);

                                    try {
                                        JSONObject currentPlan = currentPlans.getJSONObject(0);
                                        String currentTitle = currentPlan.getString("DietPlanID");
                                        String currentDetails = "Diet Plan details for Week " + currentTitle;
                                        currentWeek.setText(currentTitle);
                                        currentWeektext.setText(currentDetails);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (previousPlans.length() == 0) {
                                    // If no previous plans, show the "No Previous Diet Plan Records" card
                                    rootView.findViewById(R.id.nopreviousrecord).setVisibility(View.VISIBLE);
                                } else {
                                    // If previous plans exist, hide the "No Previous Diet Plan Records" card
                                    rootView.findViewById(R.id.nopreviousrecord).setVisibility(View.GONE);

                                    for (int i = 0; i < previousPlans.length(); i++) {
                                        try {
                                            JSONObject previousPlan = previousPlans.getJSONObject(i);
                                            String title = previousPlan.getString("DietPlanID");
                                            String details = "Diet Plan details for Week " + title;
                                            dietPlanList.add(new DietPlan(title, details));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Failed to fetch diet plans. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}
