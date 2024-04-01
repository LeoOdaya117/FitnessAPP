package com.example.fitnessapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context mContext;
    private List<FoodItem> mFoodList;

    public FoodAdapter(Context context, List<FoodItem> foodList) {
        mContext = context;
        mFoodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_todays_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem currentItem = mFoodList.get(position);

        holder.foodName.setText(currentItem.getName());
        holder.foodServing.setText("Serving: " + currentItem.getServing() + "g");

        // Load image using Picasso library
        Picasso.get().load(currentItem.getImageUrl()).placeholder(R.drawable.loading).into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public ImageView foodImage;
        public TextView foodName;
        public TextView foodServing;
        public CardView foodCard;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodname);
            foodServing = itemView.findViewById(R.id.foodserving);
            foodCard = itemView.findViewById(R.id.foodcard);
        }
    }
}
