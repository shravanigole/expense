package com.example.expense.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.example.expense.R;
import com.example.expense.data.entity.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseChartDialog extends DialogFragment {
    private List<Expense> expenses;

    public static ExpenseChartDialog newInstance(List<Expense> expenses) {
        ExpenseChartDialog dialog = new ExpenseChartDialog();
        dialog.expenses = expenses;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_expense_chart, null);
        PieChart pieChart = view.findViewById(R.id.pieChart);
        setupPieChart(pieChart);
        updateExpenseChart(pieChart);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Expense Distribution")
                .setView(view)
                .setPositiveButton("Close", null)
                .create();
    }

    private void setupPieChart(PieChart pieChart) {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(35f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setDrawEntryLabels(false);  // Disable labels on pie slices
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawCenterText(false);
        
        // Configure the legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setWordWrapEnabled(true);
        legend.setXEntrySpace(10f);
        legend.setYEntrySpace(5f);
    }

    private void updateExpenseChart(PieChart pieChart) {
        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");  // Empty label for dataset
        // Updated colors with better contrast
        dataSet.setColors(
            Color.rgb(255, 99, 71),    // Tomato Red
            Color.rgb(30, 144, 255),   // Dodger Blue
            Color.rgb(50, 205, 50),    // Lime Green
            Color.rgb(255, 165, 0),    // Orange
            Color.rgb(138, 43, 226),   // Blue Violet
            Color.rgb(255, 215, 0)     // Gold
        );
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.6f);
        dataSet.setValueLinePart2Length(0.3f);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setValueLineWidth(2f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.invalidate();
    }
}