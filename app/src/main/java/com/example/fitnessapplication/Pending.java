package com.example.fitnessapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitnessapplication.PendingTransaction;
import com.example.fitnessapplication.PendingTransactionAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Pending extends AppCompatActivity implements PendingTransactionAdapter.TransactionCancellationListener{
    private RecyclerView recyclerViewPendingTransactions;
    private PendingTransactionAdapter adapter;
    private List<PendingTransaction> pendingTransactions;
    private Handler handler = new Handler();

    public String websiteurl = URLManager.MY_URL;
    private Runnable fetchPendingRunnable = new Runnable() {
        @Override
        public void run() {
            UserDataManager userDataManager = UserDataManager.getInstance(getApplicationContext());

            String username = userDataManager.getEmail();
            pendingTransactions = new ArrayList<>();
            fetchPendingTransactions(username);
            handler.postDelayed(this, 2000); // Fetch every 2 seconds
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        String username = getIntent().getStringExtra("username");
        String posURL = getIntent().getStringExtra("posURL");

        // Initialize RecyclerView
        recyclerViewPendingTransactions = findViewById(R.id.recyclerViewPendingTransactions);
        recyclerViewPendingTransactions.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data
        pendingTransactions = new ArrayList<>();



        // Create and set adapter
        adapter = new PendingTransactionAdapter(pendingTransactions);
        recyclerViewPendingTransactions.setAdapter(adapter);
        ImageView details = findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pending.this, About.class);
                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        ImageView btnback = findViewById(R.id.backButton);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(Pending.this, R.xml.button_animation);
                btnback.startAnimation(animation);
                onBackPressed(); // Simulate back button press to return to the previous activity (Settings)
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start fetching data when the activity resumes
        handler.post(fetchPendingRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop fetching data when the activity is paused
        handler.removeCallbacks(fetchPendingRunnable);
    }

    private void fetchPendingTransactions(String username) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("Username", username)
                .build();

        Request request = new Request.Builder()
                .url(websiteurl + "/User/fetch_pending.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            PendingTransaction transaction = new PendingTransaction(
                                    jsonObject.getString("subscriptionName"),
                                    jsonObject.getString("subscriptionDescription"),
                                    jsonObject.getString("paymentID"),
                                    jsonObject.getString("price"),
                                    jsonObject.getString("status"),
                                    jsonObject.getString("paymentMethod"),
                                    jsonObject.getString("subscriptionDate")
                            );
                            pendingTransactions.add(transaction);
                        }
                        // Update UI on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set adapter with fetched data
                                adapter = new PendingTransactionAdapter(pendingTransactions);
                                recyclerViewPendingTransactions.setAdapter(adapter);
                                if (pendingTransactions.isEmpty()) {
                                    // Show "No Pending Transaction" message
                                    findViewById(R.id.NoPendingTransaction).setVisibility(View.VISIBLE);
                                }
                                else{
                                    findViewById(R.id.NoPendingTransaction).setVisibility(View.GONE);

                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onTransactionCancelled(boolean success) {
        if (success) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Pending.this, "Transaction cancelled successfully", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Pending.this, "Failed to cancel transaction", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
