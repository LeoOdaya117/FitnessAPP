package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DietPlanAdapter extends RecyclerView.Adapter<DietPlanAdapter.DietPlanViewHolder> {
    private List<DietPlan> dietPlanList;

    public DietPlanAdapter(List<DietPlan> dietPlanList) {
        this.dietPlanList = dietPlanList;
    }

    @NonNull
    @Override
    public DietPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workoutplan, parent, false);
        return new DietPlanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DietPlanViewHolder holder, int position) {
        DietPlan dietPlan = dietPlanList.get(position);
        holder.titleTextView.setText(dietPlan.getTitle());
        holder.detailsTextView.setText(dietPlan.getDetails());
    }

    @Override
    public int getItemCount() {
        return dietPlanList.size();
    }

    static class DietPlanViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView detailsTextView;
        CardView dietPlanCard;

        DietPlanViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.workoutplan);
            detailsTextView = itemView.findViewById(R.id.workoutplantext);
            dietPlanCard = itemView.findViewById(R.id.workoutplansCard);

            dietPlanCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the context from the itemView
                    Context context = itemView.getContext();

                    // Save diet plan ID
                    String dietPlanId = titleTextView.getText().toString();
                    UserDataManager.getInstance(context).saveDietPlanId(dietPlanId);

                    // Navigate to DietPlanDays fragment
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, new DietPlanDays());
                    transaction.addToBackStack(null); // Optional: Add transaction to back stack
                    transaction.commit();

                    // Show diet details dialog
//                    showDietDetailsDialog(dietPlanId);
                }
            });
        }

        private void showDietDetailsDialog(String ID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Card Details")
                    .setMessage("Diet Plan ID: " + ID )
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}
