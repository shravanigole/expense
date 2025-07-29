package com.example.expense.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.expense.R;
import com.example.expense.databinding.FragmentAddExpenseBinding;
import com.example.expense.viewmodel.ExpenseViewModel;
import com.example.expense.data.entity.Expense;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {
    private FragmentAddExpenseBinding binding;
    private ExpenseViewModel viewModel;
    private long tripId;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        
        if (getArguments() != null) {
            tripId = getArguments().getLong("tripId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupSpinners();
        setupSaveButton();
    }

    private void setupSpinners() {
        // Setup category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.expense_categories,
            android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(categoryAdapter);

        // Setup paid by spinner with trip members
        viewModel.getTripMembers(tripId).observe(getViewLifecycleOwner(), members -> {
            ArrayAdapter<String> paidByAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                members
            );
            paidByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerPaidBy.setAdapter(paidByAdapter);
        });
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String description = binding.editTitle.getText().toString();  // Changed from editDescription to editTitle
        String amountStr = binding.editAmount.getText().toString();
        String dateStr = binding.editDate.getText().toString();
        String paidBy = binding.spinnerPaidBy.getSelectedItem().toString();
        String category = binding.spinnerCategory.getSelectedItem().toString();

        // Validate input
        if (description.isEmpty() || amountStr.isEmpty() || dateStr.isEmpty()) {
            if (description.isEmpty()) {
                binding.editTitle.setError("Title is required");  // Changed from editDescription to editTitle
            }
            if (amountStr.isEmpty()) {
                binding.editAmount.setError("Amount is required");
            }
            if (dateStr.isEmpty()) {
                binding.editDate.setError("Date is required");
            }
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            Date date = dateFormat.parse(dateStr);

            if (date == null) {
                binding.editDate.setError("Invalid date format");
                return;
            }

            // Get trip members for split
            viewModel.getTripMembers(tripId).observe(getViewLifecycleOwner(), members -> {
                // Create expense with all members in split
                Expense expense = new Expense(
                    tripId, 
                    description,  // Using description as title
                    amount, 
                    category, 
                    paidBy, 
                    date,
                    new ArrayList<>(members) // Include all members in split by default
                );
                viewModel.addExpense(expense);
                
                // Clear fields or navigate back
                requireActivity().onBackPressed();
            });

        } catch (NumberFormatException e) {
            binding.editAmount.setError("Invalid amount format");
        } catch (ParseException e) {
            binding.editDate.setError("Invalid date format");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
