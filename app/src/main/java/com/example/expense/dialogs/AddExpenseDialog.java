package com.example.expense.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense.R;
import com.example.expense.adapters.SplitMemberAdapter;
import com.example.expense.data.entity.Expense;
import com.example.expense.databinding.DialogAddExpenseBinding;
import com.example.expense.fragments.TripDetailsFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddExpenseDialog extends DialogFragment {
    public interface ExpenseDialogListener {
        void onExpenseAdded(Expense expense);
        void onExpenseUpdated(Expense expense);
    }

    private DialogAddExpenseBinding binding;
    private EditText editTitle;
    private EditText editAmount;
    private Spinner spinnerPaidBy;
    private Spinner spinnerCategory;
    private DatePicker datePicker;
    private List<String> members;
    private SplitMemberAdapter splitMemberAdapter;
    private Expense existingExpense;
    private long tripId;

    public static AddExpenseDialog newInstance(long tripId, List<String> members) {
        AddExpenseDialog dialog = new AddExpenseDialog();
        Bundle args = new Bundle();
        args.putLong("tripId", tripId);
        args.putStringArrayList("members", new ArrayList<>(members));
        dialog.setArguments(args);
        return dialog;
    }

    public static AddExpenseDialog newInstance(long tripId, List<String> members, Expense expense) {
        AddExpenseDialog dialog = new AddExpenseDialog();
        Bundle args = new Bundle();
        args.putLong("tripId", tripId);
        args.putStringArrayList("members", new ArrayList<>(members));
        args.putParcelable("expense", expense);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAddExpenseBinding.inflate(LayoutInflater.from(getContext()));
        View view = binding.getRoot();
        
        tripId = getArguments().getLong("tripId");
        members = getArguments().getStringArrayList("members");
        existingExpense = getArguments().getParcelable("expense");

        initializeViews();
        setupMembersSpinner();
        setupCategorySpinner();
        setupSplitMembers();

        if (existingExpense != null) {
            populateExistingExpense();
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(existingExpense != null ? "Edit Expense" : "Add Expense")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> saveExpense())
                .setNegativeButton("Cancel", null)
                .create();
    }

    private void initializeViews() {
        editTitle = binding.editExpenseTitle;
        editAmount = binding.editExpenseAmount;
        spinnerPaidBy = binding.spinnerPaidBy;
        spinnerCategory = binding.spinnerCategory;
        datePicker = binding.datePicker;
    }

    private void populateExistingExpense() {
        editTitle.setText(existingExpense.getTitle());
        editAmount.setText(String.valueOf(existingExpense.getAmount()));
        
        // Set spinner selections
        int paidByPosition = members.indexOf(existingExpense.getPaidBy());
        if (paidByPosition >= 0) {
            spinnerPaidBy.setSelection(paidByPosition);
        }

        String[] categories = getResources().getStringArray(R.array.expense_categories);
        int categoryPosition = Arrays.asList(categories).indexOf(existingExpense.getCategory());
        if (categoryPosition >= 0) {
            spinnerCategory.setSelection(categoryPosition);
        }

        // Set date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(existingExpense.getDate());
        datePicker.updateDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    private void setupMembersSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            members
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaidBy.setAdapter(adapter);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            getResources().getStringArray(R.array.expense_categories)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupSplitMembers() {
        RecyclerView recyclerView = binding.recyclerViewSplitMembers;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        splitMemberAdapter = new SplitMemberAdapter(members);
        recyclerView.setAdapter(splitMemberAdapter);

        if (existingExpense != null) {
            splitMemberAdapter.setSelectedMembers(existingExpense.getSplitMembers());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void saveExpense() {
        String title = editTitle.getText().toString().trim();
        String amountStr = editAmount.getText().toString().trim();
        String paidBy = spinnerPaidBy.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || amountStr.isEmpty()) {
            if (title.isEmpty()) {
                editTitle.setError("Title is required");
            }
            if (amountStr.isEmpty()) {
                editAmount.setError("Amount is required");
            }
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            
            // Convert DatePicker values to Date object
            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            Date date = calendar.getTime();

            // Get selected members for split
            List<String> splitMembers = splitMemberAdapter.getSelectedMembers();
            
            // Calculate split amount
            double splitAmount = amount / splitMembers.size();

            Expense expense;
            if (existingExpense != null) {
                // Update existing expense
                existingExpense.setTitle(title);
                existingExpense.setAmount(amount);
                existingExpense.setCategory(category);
                existingExpense.setPaidBy(paidBy);
                existingExpense.setDate(date);
                existingExpense.setSplitMembers(splitMembers);
                expense = existingExpense;
            } else {
                // Create new expense
                expense = new Expense(
                    tripId,
                    title,
                    amount,
                    category,
                    paidBy,
                    date,
                    splitMembers
                );
            }

            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof ExpenseDialogListener) {
                if (existingExpense != null) {
                    ((ExpenseDialogListener) parentFragment).onExpenseUpdated(expense);
                } else {
                    ((ExpenseDialogListener) parentFragment).onExpenseAdded(expense);
                }
            }
            dismiss();
        } catch (NumberFormatException e) {
            editAmount.setError("Invalid amount");
        }
    }
}
