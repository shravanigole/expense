package com.example.expense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expense.R;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private List<String> members;

    public MemberAdapter(List<String> members) {
        this.members = members;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        holder.textMemberName.setText(members.get(position));
        holder.btnRemoveMember.setOnClickListener(v -> {
            members.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, members.size());
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView textMemberName;
        ImageButton btnRemoveMember;

        MemberViewHolder(View itemView) {
            super(itemView);
            textMemberName = itemView.findViewById(R.id.textMemberName);
            btnRemoveMember = itemView.findViewById(R.id.btnRemoveMember);
        }
    }
}