package com.example.expense.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.expense.adapter.TripAdapter;
import com.example.expense.databinding.FragmentTripListBinding;
import com.example.expense.viewmodel.TripViewModel;

public class TripListFragment extends Fragment {
    private FragmentTripListBinding binding;
    private TripViewModel tripViewModel;
    private TripAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripListBinding.inflate(inflater, container, false);
        
        setupRecyclerView();
        setupViewModel();
        setupFab();
        
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new TripAdapter(trip -> {
            TripListFragmentDirections.ActionTripListToTripDetails action =
                TripListFragmentDirections.actionTripListToTripDetails(trip.getId());
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });
        
        binding.recyclerTrips.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTrips.setAdapter(adapter);
    }

    private void setupViewModel() {
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        tripViewModel.getAllTrips().observe(getViewLifecycleOwner(), trips -> {
            adapter.submitList(trips);
            binding.emptyView.setVisibility(trips.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void setupFab() {
        binding.fabAddTrip.setOnClickListener(v ->
            Navigation.findNavController(v).navigate(
                TripListFragmentDirections.actionTripListToAddTrip()
            )
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
