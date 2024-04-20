package com.example.fitnessapplication;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class Subscription extends AppCompatActivity {

    public String websiteurl = URLManager.MY_URL;
    private RecyclerView recyclerView;
    private SubscriptionAdapter adapter;
    private TextView textViewCurrentMembershipDetails;

    private ImageView backbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        UserDataManager userDataManager = UserDataManager.getInstance(getApplicationContext());

        String username = userDataManager.getEmail();

        backbutton = findViewById(R.id.subscription_backbtn);

        textViewCurrentMembershipDetails = findViewById(R.id.textViewCurrentMembershipDetails);

        // Retrieve RecyclerView from layout
        recyclerView = findViewById(R.id.recyclerViewSubscriptions);

        // Set up RecyclerView with LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation(Subscription.this, R.xml.button_animation);
                backbutton.startAnimation(animation);
                onBackPressed();
            }
        });

        // Fetch membership plans
        DummyData.getDummyPlans(new DummyData.MembershipFetchListener() {
            @Override
            public void onMembershipFetched(ArrayList<SubscriptionPlan> fetchedPlans) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new SubscriptionAdapter(fetchedPlans, username);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }


            @Override
            public void onFetchError(String errorMessage) {
                // Handle error appropriately
            }
        });

        ImageView details = findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataManager userDataManager = UserDataManager.getInstance(getApplicationContext());

                String username = userDataManager.getEmail();
                Intent intent = new Intent(Subscription.this, About.class);
                intent.putExtra("username", username); // Pass the username value
                startActivity(intent);
            }
        });

        fetchMembershipDetails(username);
    }

    private void fetchMembershipDetails(String username) {
        OkHttpClient client = new OkHttpClient();

        // Construct the URL with the username
        String url = websiteurl + "/User/fetch_membership_user.php?Username=" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle failure appropriately
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        String plan = jsonObject.getString("plan");
                        String startDate = jsonObject.getString("startDate");
                        String dueDate = jsonObject.getString("dueDate");
                        String status = jsonObject.getString("status");

                        // Update the UI on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update the textViewCurrentMembershipDetails
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date dueDateObj = null;
                                try {
                                    dueDateObj = sdf.parse(dueDate);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                // Calculate today's date
                                Calendar today = Calendar.getInstance();
                                today.set(Calendar.HOUR_OF_DAY, 0);
                                today.set(Calendar.MINUTE, 0);
                                today.set(Calendar.SECOND, 0);
                                today.set(Calendar.MILLISECOND, 0);

                                // Calculate the difference in days between today and dueDate
                                long diffInMillis = dueDateObj.getTime() - today.getTimeInMillis();
                                long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                                // Check if dueDate is today or within 3 days or has already passed
                                if (diffInDays <= 3 ) {
                                    CardView cardView = findViewById(R.id.cardViewCurrentMembership);
                                    cardView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Subscription.this, R.color.orange)));
                                }else if ((diffInDays >= 0 &&  diffInDays < 0)) {
                                    CardView cardView = findViewById(R.id.cardViewCurrentMembership);
                                    cardView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Subscription.this, R.color.red)));
                                }


                                String membershipDetails = "You are currently subscribed to the " + plan + " plan. "
                                        + "Enjoy unlimited access to our gym facilities!\n\n"
                                        + "Membership End Date: " + dueDate;
                                textViewCurrentMembershipDetails.setText(membershipDetails);

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                    System.out.println("Error: " + response.code());
                }
            }
        });
    }
}
