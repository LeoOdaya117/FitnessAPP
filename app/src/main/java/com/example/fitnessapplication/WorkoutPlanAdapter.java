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
import androidx.fragment.app.FragmentActivity;


public class WorkoutPlanAdapter extends RecyclerView.Adapter<WorkoutPlanAdapter.WorkoutPlanViewHolder> {
    private List<WorkoutPlan> workoutPlanList;

    public WorkoutPlanAdapter(List<WorkoutPlan> workoutPlanList) {
        this.workoutPlanList = workoutPlanList;
    }

    @NonNull
    @Override
    public WorkoutPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workoutplan, parent, false);
        return new WorkoutPlanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutPlanViewHolder holder, int position) {
        WorkoutPlan workoutPlan = workoutPlanList.get(position);
        holder.titleTextView.setText(workoutPlan.getTitle());
        holder.detailsTextView.setText(workoutPlan.getDetails());


    }


    @Override
    public int getItemCount() {
        return workoutPlanList.size();
    }

    static class WorkoutPlanViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView detailsTextView;

        CardView workoutplansCard;

        WorkoutPlanViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.workoutplan);
            detailsTextView = itemView.findViewById(R.id.workoutplantext);
            workoutplansCard = itemView.findViewById(R.id.workoutplansCard);

            workoutplansCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the context from the itemView
                    Context context = itemView.getContext();

                    // Save workout plan ID
                    String workoutPlanId = titleTextView.getText().toString();
                    UserDataManager.getInstance(context).saveWorkoutPlanId(workoutPlanId);

                    // Navigate to WorkoutPlanDays fragment
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, new WorkoutPlanDays());
                    transaction.addToBackStack(null); // Optional: Add transaction to back stack
                    transaction.commit();

                    // Show exercise details dialog
//                    showExerciseDetailsDialog(workoutPlanId);
                }
            });
        }

        private void showExerciseDetailsDialog(String ID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Card Details")
                    .setMessage("Workout Plan ID: " + ID )
                    .setPositiveButton("OK", null)
                    .show();
        }
    }




}
