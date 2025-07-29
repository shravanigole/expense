package com.example.expense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.expense.R;
import com.example.expense.data.entity.Trip;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends ListAdapter<Trip, TripAdapter.TripViewHolder> {

    private final OnTripClickListener listener;

    public TripAdapter(OnTripClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = getItem(position);
        if (trip != null) {
            holder.tripName.setText(trip.getTitle());  // Changed from getName() to getTitle()
            
            // Format the date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String dateStr = dateFormat.format(trip.getStartDate());
            holder.tripDate.setText(dateStr);
            
            // Format members
            String members = "Members: " + String.join(", ", trip.getMembers());
            holder.tripMembers.setText(members);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTripClick(trip);
                }
            });
        }
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tripName;
        TextView tripDate;
        TextView tripMembers;

        TripViewHolder(View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.tripName);
            tripDate = itemView.findViewById(R.id.tripDate);
            tripMembers = itemView.findViewById(R.id.tripMembers);
        }
    }

    private static final DiffUtil.ItemCallback<Trip> DIFF_CALLBACK = new DiffUtil.ItemCallback<Trip>() {
        @Override
        public boolean areItemsTheSame(@NonNull Trip oldItem, @NonNull Trip newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Trip oldItem, @NonNull Trip newItem) {
            return oldItem.equals(newItem);
        }
    };

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }
}
