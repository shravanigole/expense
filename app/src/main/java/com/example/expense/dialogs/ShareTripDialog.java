package com.example.expense.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import com.example.expense.R;
import com.example.expense.data.entity.Trip;
import com.example.expense.data.entity.Expense;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class ShareTripDialog extends DialogFragment {
    private Trip trip;
    private List<Expense> expenses;
    private Map<String, Double> settlements;

    public static ShareTripDialog newInstance(Trip trip, List<Expense> expenses, Map<String, Double> settlements) {
        ShareTripDialog dialog = new ShareTripDialog();
        Bundle args = new Bundle();
        args.putParcelable("trip", trip);
        ArrayList<Parcelable> parcelableExpenses = new ArrayList<>(expenses);
        args.putParcelableArrayList("expenses", parcelableExpenses);
        // Convert settlements to a serializable format
        args.putSerializable("settlements", new HashMap<>(settlements));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_share_trip, null);
        trip = getArguments().getParcelable("trip");
        expenses = getArguments().getParcelableArrayList("expenses");

        TextView textSummary = view.findViewById(R.id.textTripSummary);
        textSummary.setText(generateSummary(trip, expenses));

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Share Trip Summary")
                .setView(view)
                .setPositiveButton("Share", (dialog, which) -> shareTrip())
                .setNegativeButton("Cancel", null)
                .create();
    }

    private String generateSummary(Trip trip, List<Expense> expenses) {
        StringBuilder summary = new StringBuilder();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        
        summary.append("Trip: ").append(trip.getTitle()).append("\n\n");
        summary.append("Total expenses: ").append(currencyFormat.format(total)).append("\n");
        summary.append("Per person: ").append(currencyFormat.format(total / trip.getMembers().size()));
        
        return summary.toString();
    }

    private void shareTrip() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Trip Summary: " + trip.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, generateSummary(trip, expenses));
        startActivity(Intent.createChooser(shareIntent, "Share Trip Summary"));
    }
}
