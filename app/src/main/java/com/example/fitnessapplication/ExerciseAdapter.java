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

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exercises;

    public ExerciseAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.textViewExerciseName.setText(exercise.getExerciseName());
        holder.text_view_equipment.setText("Equipment: " + exercise.getEquipmentId());
        Picasso.get().load(exercise.getImageUrl()).into(holder.exercise_image_view);
        holder.info_icon.setTag(exercise); // Assuming you want to set the Exercise object as the tag

    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewExerciseName;
        TextView text_view_equipment;
        ImageView exercise_image_view, info_icon;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewExerciseName = itemView.findViewById(R.id.text_view_exercise_name);
            text_view_equipment= itemView.findViewById(R.id.text_view_equipment);
            exercise_image_view = itemView.findViewById(R.id.exercise_image_view);
            info_icon = itemView.findViewById(R.id.info_icon);
        }
    }
}

