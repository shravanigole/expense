package com.example.expense.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.expense.data.entity.Trip;
import com.example.expense.databinding.FragmentAddTripBinding;
import com.example.expense.viewmodel.TripViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.text.ParseException;

public class AddTripFragment extends Fragment {
    private FragmentAddTripBinding binding;
    private TripViewModel tripViewModel;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTripBinding.inflate(inflater, container, false);
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);

        setupDatePickers();
        setupSaveButton();
        setupMemberInput();
        observeLoadingState();

        return binding.getRoot();
    }

    private void setupDatePickers() {
        binding.startDate.setOnClickListener(v -> showDatePicker(true));
        binding.endDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStartDate) {
        if (getContext() == null) return;
        
        DatePickerDialog dialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, day) -> {
                if (binding == null) return; // Check if binding is still valid
                
                calendar.set(year, month, day);
                String formattedDate = dateFormat.format(calendar.getTime());
                if (isStartDate) {
                    binding.startDate.setText(formattedDate);
                } else {
                    binding.endDate.setText(formattedDate);
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void setupMemberInput() {
        binding.addMemberButton.setOnClickListener(v -> {
            String member = binding.memberInput.getText().toString().trim();
            if (!member.isEmpty()) {
                if (isMemberAlreadyAdded(member)) {
                    binding.memberInput.setError("Member already added");
                    return;
                }
                addMemberChip(member);
                binding.memberInput.setText("");
            }
        });
    }

    private boolean isMemberAlreadyAdded(String member) {
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(member)) {
                return true;
            }
        }
        return false;
    }

    public void setMembers(List<String> members) {
        binding.chipGroup.removeAllViews(); // Clear existing chips
        for (String member : members) {
            addMemberChip(member);
        }
    }

    private void addMemberChip(String member) {
        Chip chip = new Chip(requireContext());
        chip.setText(member);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> binding.chipGroup.removeView(chip));
        binding.chipGroup.addView(chip);
    }

    private void setupSaveButton() {
        binding.saveButton.setOnClickListener(v -> {
            if (validateInput()) {
                saveTrip();
                // Don't navigate immediately, wait for confirmation
                tripViewModel.getTripSaved().observe(getViewLifecycleOwner(), saved -> {
                    if (saved) {
                        Navigation.findNavController(v).navigateUp();
                    }
                });
                
                // Handle errors
                tripViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
                    if (error != null) {
                        // Show error message to user
                        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private boolean validateInput() {
        if (binding.tripTitle.getText().toString().trim().isEmpty()) {
            binding.tripTitle.setError("Title is required");
            return false;
        }
        if (binding.startDate.getText().toString().isEmpty()) {
            binding.startDate.setError("Start date is required");
            return false;
        }
        if (binding.endDate.getText().toString().isEmpty()) {
            binding.endDate.setError("End date is required");
            return false;
        }
        
        // Add date validation
        try {
            Date startDate = dateFormat.parse(binding.startDate.getText().toString());
            Date endDate = dateFormat.parse(binding.endDate.getText().toString());
            if (endDate.before(startDate)) {
                binding.endDate.setError("End date cannot be before start date");
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        
        return true;
    }

    private void saveTrip() {
        Trip trip = new Trip();
        trip.setTitle(binding.tripTitle.getText().toString().trim());
        trip.setDescription(binding.tripDescription.getText().toString().trim());
        trip.setStartDate(binding.startDate.getText().toString());
        trip.setEndDate(binding.endDate.getText().toString());
        
        ArrayList<String> members = new ArrayList<>();
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(i);
            members.add(chip.getText().toString());
        }
        trip.setMembers(members);

        tripViewModel.insert(trip);
    }

    private void observeLoadingState() {
        tripViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.saveButton.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
