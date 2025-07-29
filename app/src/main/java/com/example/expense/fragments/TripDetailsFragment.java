package com.example.expense.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expense.R;
import com.example.expense.adapters.ExpenseAdapter;
import com.example.expense.data.entity.Expense;
import com.example.expense.data.entity.Trip;
import com.example.expense.databinding.FragmentTripDetailsBinding;
import com.example.expense.dialogs.AddExpenseDialog;
import com.example.expense.dialogs.ExpenseChartDialog;
import com.example.expense.dialogs.ShareTripDialog;
import com.example.expense.model.ExpenseCategory;
import com.example.expense.viewmodel.TripDetailsViewModel;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.core.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.navigation.Navigation;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.text.NumberFormat;
import java.util.Locale;

public class TripDetailsFragment extends Fragment 
    implements ExpenseAdapter.ExpenseClickListener, AddExpenseDialog.ExpenseDialogListener {
    private FragmentTripDetailsBinding binding;
    private TripDetailsViewModel viewModel;
    private ExpenseAdapter expenseAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this).get(TripDetailsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        expenseAdapter = new ExpenseAdapter(this);
        binding.recyclerViewExpenses.setAdapter(expenseAdapter);
        binding.recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Get tripId from arguments
        long tripId = TripDetailsFragmentArgs.fromBundle(getArguments()).getTripId();
        viewModel.loadTrip(tripId);

        // Observe trip data
        viewModel.getTrip().observe(getViewLifecycleOwner(), trip -> {
            if (trip != null) {
                updateTripHeader(trip);
            }
        });

        // Observe expenses
        viewModel.getExpenses().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null) {
                expenseAdapter.submitList(expenses);
                updateTotalExpenses(expenses);
            }
        });

        setupFab();
        setupChartButton();
    }

    private void setupFab() {
        binding.fabAddExpense.setOnClickListener(v -> showAddExpenseDialog());
    }

    private void setupChartButton() {
        binding.includeHeader.btnShowChart.setOnClickListener(v -> {
            List<Expense> currentExpenses = viewModel.getExpenses().getValue();
            if (currentExpenses != null && !currentExpenses.isEmpty()) {
                ExpenseChartDialog dialog = ExpenseChartDialog.newInstance(currentExpenses);
                dialog.show(getChildFragmentManager(), "ExpenseChart");
            }
        });
    }

    private void updateTripHeader(Trip trip) {
        binding.includeHeader.textTripTitle.setText(trip.getTitle());
        String dateRange = trip.getStartDate() + " - " + trip.getEndDate();
        binding.includeHeader.textTripDates.setText(dateRange);
    }

    private void updateTotalExpenses(List<Expense> expenses) {
        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String totalText = String.format("Total Expenses: %s", currencyFormat.format(total));
        binding.includeHeader.textTotalExpenses.setText(totalText);
    }

    private void showAddExpenseDialog() {
        Trip currentTrip = viewModel.getTrip().getValue();
        if (currentTrip != null) {
            AddExpenseDialog dialog = AddExpenseDialog.newInstance(
                currentTrip.getId(),
                currentTrip.getMembers()
            );
            dialog.show(getChildFragmentManager(), "AddExpense");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trip_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete_trip) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Trip")
                .setMessage(R.string.delete_trip_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteTrip())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteTrip() {
        Trip currentTrip = viewModel.getTrip().getValue();
        if (currentTrip != null) {
            viewModel.deleteTrip(currentTrip);
            Navigation.findNavController(requireView()).navigateUp();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onExpenseClick(Expense expense) {
        Trip currentTrip = viewModel.getTrip().getValue();
        if (currentTrip != null) {
            AddExpenseDialog dialog = AddExpenseDialog.newInstance(
                currentTrip.getId(),
                currentTrip.getMembers(),
                expense
            );
            dialog.show(getChildFragmentManager(), "EditExpense");
        }
    }

    @Override
    public void onExpenseDelete(Expense expense) {
        viewModel.deleteExpense(expense);
    }

    @Override
    public void onExpenseAdded(Expense expense) {
        viewModel.addExpense(expense);
    }

    @Override
    public void onExpenseUpdated(Expense expense) {
        viewModel.updateExpense(expense);
    }
}
