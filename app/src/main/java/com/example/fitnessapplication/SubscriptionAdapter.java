package com.example.fitnessapplication;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    private ArrayList<SubscriptionPlan> plans;
    public String username; // Variable to store the username

    public SubscriptionAdapter(ArrayList<SubscriptionPlan> plans, String username) {
        this.plans = plans;
        this.username = username; // Initialize the username
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubscriptionPlan plan = plans.get(position);
        holder.bind(plan);
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPlanName;
        private TextView textViewPlanDescription;
        private TextView textViewPlanPrice;
        private Button buttonSubscribe;

        private AlertDialog dialog; // Declare AlertDialog instance variable


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPlanName = itemView.findViewById(R.id.textViewPlanName);
            textViewPlanDescription = itemView.findViewById(R.id.textViewPlanDescription);
            textViewPlanPrice = itemView.findViewById(R.id.textViewPlanPrice);
            buttonSubscribe = itemView.findViewById(R.id.buttonSubscribe);

            // Subscribe Button Click Listener
            buttonSubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the plan details
                    SubscriptionPlan plan = plans.get(getAdapterPosition());

                    // Show payment method selection dialog
                    showPaymentMethodDialog(username, plan.getName(), plan.getPrice());
                }
            });
        }

        public void bind(SubscriptionPlan plan) {
            textViewPlanName.setText(plan.getName());
            textViewPlanDescription.setText(plan.getDescription());
            textViewPlanPrice.setText(String.valueOf("Price: ₱ " + plan.getPrice() + "0"));
        }

        private void showPaymentMethodDialog(String username, String planName, double price) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

            // Inflate the layout for the dialog
            View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.payment_selection_bottom_sheet, null);
            builder.setView(dialogView);

            // Initialize views
            TextView textViewCash = dialogView.findViewById(R.id.textViewCash);
            TextView textViewGcash = dialogView.findViewById(R.id.textViewGcash);

            // Set click listeners for payment methods
            textViewCash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog(username, planName, price, "Cash");
                    dismissDialog(); // Dismiss dialog after selection
                }
            });

            textViewGcash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show confirmation dialog for GCash
                    showConfirmationDialog(username, planName, price, "GCash");
                    dismissDialog();
                }
            });

            // Create the dialog and assign it to the instance variable
            dialog = builder.create(); // <- Assigning the dialog to the instance variable
            dialog.show();
        }


        private void showConfirmationDialog(String username, String planName, double price, String paymentMethod) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Confirm Payment Method");
            builder.setMessage("Are you sure you want to subscribe using "+ paymentMethod +"?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Subscribe with GCash

                    if(paymentMethod =="GCash"){
                        showToast("Gcash is not yet Available.");
                    }
                    else{
                        subscribe(username, planName, price, paymentMethod);
                    }
                    dismissDialog();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismissDialog();
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void subscribe(String username, String planName, double price, String paymentMethod) {
            String websiteurl = URLManager.MY_URL;
            OkHttpClient client = new OkHttpClient();

            // Request Body
            RequestBody requestBody = new FormBody.Builder()
                    .add("username", username)
                    .add("planName", planName)
                    .add("price", String.valueOf(price))
                    .add("paymentMethod", paymentMethod)
                    .build();

            // Request
            Request request = new Request.Builder()
                    .url(websiteurl + "/User/subscribePlan.php")
                    .post(requestBody)
                    .build();

            // Asynchronous Call
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    // Handle subscription failure
                    showToast("Subscription failed: " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Handle subscription success
                        showToast(response.body().string());
                    } else {
                        // Handle subscription failure
                        showToast("Subscription failed: " + response.message());
                    }
                }
            });
        }

        private void showToast(String message) {
            // Show toast in the UI thread
            itemView.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void dismissDialog() {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss(); // Dismiss the dialog if it's not null and is currently showing
            }
        }
    }

}
