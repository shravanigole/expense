package com.example.expense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expense.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.NumberFormat;
import java.util.Locale;

public class SettlementAdapter extends RecyclerView.Adapter<SettlementAdapter.SettlementViewHolder> {
    private List<Settlement> settlements = new ArrayList<>();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public static class Settlement {
        public final String member;
        public final double amount;
        public final boolean isOwed;

        public Settlement(String member, double amount) {
            this.member = member;
            this.amount = Math.abs(amount);
            this.isOwed = amount < 0;
        }
    }

    public void setSettlements(Map<String, Double> balances) {
        settlements.clear();
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            settlements.add(new Settlement(entry.getKey(), entry.getValue()));
        }
        notifyDataSetChanged();
    }

    @Override
    public SettlementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settlement, parent, false);
        return new SettlementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SettlementViewHolder holder, int position) {
        Settlement settlement = settlements.get(position);
        String text = settlement.member + " " + 
                     (settlement.isOwed ? "owes " : "gets back ") +
                     currencyFormat.format(settlement.amount);
        holder.textSettlement.setText(text);
        holder.textSettlement.setTextColor(holder.itemView.getContext().getColor(
            settlement.isOwed ? R.color.colorNegative : R.color.colorPositive
        ));
    }

    @Override
    public int getItemCount() {
        return settlements.size();
    }

    static class SettlementViewHolder extends RecyclerView.ViewHolder {
        final TextView textSettlement;

        SettlementViewHolder(View itemView) {
            super(itemView);
            textSettlement = itemView.findViewById(R.id.textSettlement);
        }
    }
}