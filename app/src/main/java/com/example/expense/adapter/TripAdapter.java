package com.example.expense.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expense.data.entity.Trip;
import com.example.expense.databinding.ItemTripBinding;

public class TripAdapter extends ListAdapter<Trip, TripAdapter.TripViewHolder> {
    private final OnTripClickListener listener;

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    public TripAdapter(OnTripClickListener listener) {
        super(new TripDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripBinding binding = ItemTripBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new TripViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = getItem(position);
        holder.binding.setTrip(trip);
        holder.binding.executePendingBindings();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        private final ItemTripBinding binding;

        TripViewHolder(ItemTripBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onTripClick(getItem(position));
                }
            });
        }
    }

    static class TripDiffCallback extends DiffUtil.ItemCallback<Trip> {
        @Override
        public boolean areItemsTheSame(@NonNull Trip oldItem, @NonNull Trip newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Trip oldItem, @NonNull Trip newItem) {
            return oldItem.equals(newItem);
        }
    }
}
