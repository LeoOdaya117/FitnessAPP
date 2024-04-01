package com.example.fitnessapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class WorkoutPlanExerciseAdapter extends RecyclerView.Adapter<WorkoutPlanExerciseAdapter.WorkoutPlanExerciseViewHolder> {

    private List<WorkoutPlanExercise> exerciseList;

    public WorkoutPlanExerciseAdapter(List<WorkoutPlanExercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public WorkoutPlanExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workoutplan_exercise, parent, false);
        return new WorkoutPlanExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutPlanExerciseViewHolder holder, int position) {
        WorkoutPlanExercise exercise = exerciseList.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class WorkoutPlanExerciseViewHolder extends RecyclerView.ViewHolder {

        private TextView exerciseNameTextView, setRepsTextView, durationTextView;
        private ImageView exerciseImageView, infoImageView;

        public WorkoutPlanExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImageView = itemView.findViewById(R.id.exercisephoto);
            infoImageView = itemView.findViewById(R.id.info);
            exerciseNameTextView = itemView.findViewById(R.id.exercisename);
            setRepsTextView = itemView.findViewById(R.id.setreps);
            durationTextView = itemView.findViewById(R.id.duration);
        }

        public void bind(WorkoutPlanExercise exercise) {
            String image = exercise.getImageUrl();
            String set = exercise.getSet();
            String reps = exercise.getReps();
            String duration = exercise.getDuration();

            // If sets and reps are empty, display duration only
            if (set.isEmpty() && reps.isEmpty()) {
                setRepsTextView.setText("Duration: " + duration);
                durationTextView.setVisibility(View.GONE); // Hide duration TextView
            } else {
                // Display sets and reps
                setRepsTextView.setText("Sets: " + set);
                durationTextView.setText("Reps: " + reps);
                durationTextView.setVisibility(View.VISIBLE); // Show duration TextView
            }

            exerciseNameTextView.setText(exercise.getName());

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.loading) // Optional: Placeholder image while loading
                    .error(R.drawable.notfound) // Optional: Error image if the image fails to load
                    .into(exerciseImageView);
        }

    }
}
