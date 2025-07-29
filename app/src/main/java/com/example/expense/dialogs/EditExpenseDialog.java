package com.example.expense.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.expense.R;
import com.example.expense.data.entity.Expense;
import com.example.expense.fragments.TripDetailsFragment;
import com.example.expense.model.ExpenseCategory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;

public class EditExpenseDialog extends DialogFragment {
    private EditText editTitle;
    private EditText editAmount;
    private Spinner spinnerCategory;
    private Spinner spinnerPaidBy;
    private DatePicker datePicker;
    private List<String> members;
    private Expense expense;
    private AddExpenseDialog.ExpenseDialogListener listener;

    public static EditExpenseDialog newInstance(Expense expense, List<String> members) {
        EditExpenseDialog dialog = new EditExpenseDialog();
        Bundle args = new Bundle();
        args.putParcelable("expense", (Parcelable) expense);
        args.putStringArrayList("members", new ArrayList<>(members));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_expense, null);
        
        expense = getArguments().getParcelable("expense");
        members = getArguments().getStringArrayList("members");

        initializeViews(view);
        populateExpenseData();

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Expense")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> saveExpense())
                .setNegativeButton("Cancel", null)
                .create();
    }

    private void populateExpenseData() {
        editTitle.setText(expense.getTitle());
        editAmount.setText(String.valueOf(expense.getAmount()));
        
        // Find the matching ExpenseCategory enum value
        String categoryStr = expense.getCategory();
        for (ExpenseCategory category : ExpenseCategory.values()) {
            if (category.getDisplayName().equals(categoryStr)) {
                spinnerCategory.setSelection(category.ordinal());
                break;
            }
        }

        int memberIndex = members.indexOf(expense.getPaidBy());
        if (memberIndex != -1) {
            spinnerPaidBy.setSelection(memberIndex);
        }
    }

    private void saveExpense() {
        String title = editTitle.getText().toString().trim();
        String amountStr = editAmount.getText().toString().trim();
        String paidBy = spinnerPaidBy.getSelectedItem().toString();
        ExpenseCategory selectedCategory = (ExpenseCategory) spinnerCategory.getSelectedItem();

        if (title.isEmpty() || amountStr.isEmpty()) {
            return;
        }

        double amount = Double.parseDouble(amountStr);
        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setPaidBy(paidBy);
        expense.setCategory(selectedCategory.getDisplayName());

        if (getParentFragment() instanceof AddExpenseDialog.ExpenseDialogListener) {
            listener.onExpenseUpdated(expense);
        }
        dismiss();
    }

    private void initializeViews(View view) {
        editTitle = view.findViewById(R.id.editExpenseTitle);
        editAmount = view.findViewById(R.id.editExpenseAmount);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerPaidBy = view.findViewById(R.id.spinnerPaidBy);
        datePicker = view.findViewById(R.id.datePicker);

        // Set up category spinner
        ArrayAdapter<ExpenseCategory> categoryAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ExpenseCategory.values()
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set up paid by spinner
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            members
        );
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaidBy.setAdapter(memberAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Get the listener from parent fragment
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof AddExpenseDialog.ExpenseDialogListener) {
            listener = (AddExpenseDialog.ExpenseDialogListener) parentFragment;
        } else {
            throw new RuntimeException(parentFragment.toString()
                    + " must implement ExpenseDialogListener");
        }
    }
}
