package com.example.expense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expense.R;
import com.example.expense.data.entity.Expense;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private List<Expense> expenses;
    private final NumberFormat currencyFormat;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final ExpenseClickListener listener;

    public interface ExpenseClickListener {
        void onExpenseClick(Expense expense);
        void onExpenseDelete(Expense expense);
    }

    public ExpenseAdapter(ExpenseClickListener listener) {
        this.listener = listener;
        this.expenses = null;
        // Setup Indian Rupee format
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.expenseTitle.setText(expense.getTitle());
        holder.amount.setText(currencyFormat.format(expense.getAmount()));
        holder.paidBy.setText("Paid by: " + expense.getPaidBy());
        holder.expenseDate.setText(dateFormat.format(expense.getDate()));

        // Calculate and display split details
        List<String> splitMembers = expense.getSplitBetween();
        if (splitMembers != null && !splitMembers.isEmpty()) {
            double splitAmount = expense.getAmount() / splitMembers.size();
            StringBuilder splitDetails = new StringBuilder();
            
            for (String member : splitMembers) {
                if (!member.equals(expense.getPaidBy())) {
                    splitDetails.append(member)
                              .append(" owes ")
                              .append(currencyFormat.format(splitAmount))
                              .append(" to ")
                              .append(expense.getPaidBy())
                              .append("\n");
                }
            }
            
            if (splitDetails.length() > 0) {
                holder.splitDetails.setText(splitDetails.toString().trim());
                holder.splitContainer.setVisibility(View.VISIBLE);
            } else {
                holder.splitContainer.setVisibility(View.GONE);
            }
        } else {
            holder.splitContainer.setVisibility(View.GONE);
        }

        // Setup click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExpenseClick(expense);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onExpenseDelete(expense);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return expenses != null ? expenses.size() : 0;
    }

    public void submitList(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView expenseTitle;
        TextView amount;
        TextView paidBy;
        TextView expenseDate;
        TextView splitDetails;
        View splitContainer;

        ViewHolder(View view) {
            super(view);
            expenseTitle = view.findViewById(R.id.expenseTitle);
            amount = view.findViewById(R.id.amount);
            paidBy = view.findViewById(R.id.paidBy);
            expenseDate = view.findViewById(R.id.expenseDate);
            splitDetails = view.findViewById(R.id.splitDetails);
            splitContainer = view.findViewById(R.id.splitContainer);
        }
    }
}
