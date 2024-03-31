package com.example.fitnessapplication;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
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

        // Reset views to default or empty values
        holder.textViewExerciseName.setText("");
        holder.text_view_equipment.setText("");
        holder.exercise_image_view.setImageResource(R.drawable.notfound);
        holder.info_icon.setTag(null); // Clear tag
        String category = Discover.getCategory();

        String name = "";
        String secondtext = "";
        String image = "";
        String tag = "";

        if(Discover.getCategory().equals("exercise")){
            name =exercise.getExerciseName();
            secondtext = "Equipment: " + exercise.getEquipmentId();
            image = exercise.getImageUrl() ;
            tag = exercise.getExerciseId();


        } else if (Discover.getCategory().equals("food")) {
            name =exercise.getFoodName();
            secondtext = "Serving: " + exercise.getServing() + " g";
            image = exercise.getFoodImage() ;
            tag = exercise.getFoodID();

        }
        else if (Discover.getCategory().equals("equipment")) {
            name =exercise.getEquipmentName();
            secondtext = "";
            image = exercise.getEquipmentImage() ;
            tag = exercise.getEquipmentID();
        }
        else{

        }

        holder.textViewExerciseName.setText(name);
        holder.text_view_equipment.setText(secondtext);
        if(!image.isEmpty()){
            Picasso.get().load(image).into(holder.exercise_image_view);
        }

        holder.info_icon.setTag(tag); // Assuming you want to set the Exercise object as the tag



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

            // Set OnClickListener for the info_icon
            info_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the tag associated with the info_icon
                    Object tag = info_icon.getTag();
                    if (tag != null && tag instanceof String) {
                        String exerciseId = (String) tag;
                        // Handle the click event for the info_icon
                        // For example, you can display a dialog with exercise details
                        showExerciseDetailsDialog(exerciseId, textViewExerciseName.getText().toString());
                    }
                }
            });
        }
        // Method to show exercise details dialog
        private void showExerciseDetailsDialog(String exerciseId, String name) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Card Details")
                    .setMessage("ID: " + exerciseId + "\nName: " + name)
                    .setPositiveButton("OK", null)
                    .show();
        }

    }
}

