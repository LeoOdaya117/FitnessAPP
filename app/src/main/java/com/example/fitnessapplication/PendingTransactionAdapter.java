package com.example.fitnessapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PendingTransactionAdapter extends RecyclerView.Adapter<PendingTransactionAdapter.ViewHolder> {

    private List<PendingTransaction> pendingTransactions;

    public PendingTransactionAdapter(List<PendingTransaction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingTransaction transaction = pendingTransactions.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return pendingTransactions.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSubscriptionName;
        private TextView textViewSubscriptionDescription;
        private TextView textViewPaymentID;
        private TextView textViewPrice;
        private TextView textViewStatus;
        private TextView textViewPaymentMethod;
        private TextView textViewSubscriptionDate;
        private Button buttonCancelSubscription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubscriptionName = itemView.findViewById(R.id.textViewSubscriptionName);
            textViewSubscriptionDescription = itemView.findViewById(R.id.textViewSubscriptionDescription);
            textViewPaymentID = itemView.findViewById(R.id.textViewPaymentID);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewPaymentMethod = itemView.findViewById(R.id.textViewPaymentMethod);
            textViewSubscriptionDate = itemView.findViewById(R.id.textViewSubscriptionDate);
            buttonCancelSubscription = itemView.findViewById(R.id.buttonCancelSubscription);
        }

        public void bind(PendingTransaction transaction) {
            textViewSubscriptionName.setText(transaction.getSubscriptionName());
            textViewSubscriptionDescription.setText(transaction.getSubscriptionDescription());
            textViewPaymentID.setText(transaction.getPaymentID());
            textViewPrice.setText("₱ "+ transaction.getPrice() + ".00");
            textViewStatus.setText(transaction.getStatus());
            textViewPaymentMethod.setText(transaction.getPaymentMethod());
            textViewSubscriptionDate.setText(transaction.getSubscriptionDate());

            // Set click listener for cancel button
            buttonCancelSubscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCancelConfirmationDialog(v.getContext(), transaction.getPaymentID());
                }

            });
        }
    }

    public static void showCancelConfirmationDialog(Context context, String paymentID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Cancellation");
        builder.setMessage("Are you sure you want to cancel this subscription?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelTransaction(context, paymentID, new TransactionCancellationListener() {
                    @Override
                    public void onTransactionCancelled(boolean success) {

                    }
                });
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public static void cancelTransaction(Context context, String salesID, TransactionCancellationListener listener) {
        String websiteurl = URLManager.MY_URL;
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("salesID", salesID)
                .build();

        Request request = new Request.Builder()
                .url(websiteurl + "/Gym_Website/user/cancel_transaction_mobile.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                listener.onTransactionCancelled(false); // Notify listener of failure
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Process response data (e.g., check if cancellation was successful)
                    if (responseData.equals("success")) {
                        listener.onTransactionCancelled(true); // Notify listener of success
                    } else {
                        listener.onTransactionCancelled(false); // Notify listener of failure
                    }
                } else {
                    listener.onTransactionCancelled(false); // Notify listener of failure
                }
            }
        });
    }


    public interface TransactionCancellationListener {
        void onTransactionCancelled(boolean success);
    }



}
