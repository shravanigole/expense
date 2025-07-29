package com.example.expense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expense.R;
import java.util.ArrayList;
import java.util.List;

public class SplitMemberAdapter extends RecyclerView.Adapter<SplitMemberAdapter.ViewHolder> {
    private List<String> members;
    private List<Boolean> selectedStates;

    public SplitMemberAdapter(List<String> members) {
        this.members = members;
        this.selectedStates = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            selectedStates.add(true); // All members selected by default
        }
    }

    public void setSelectedMembers(List<String> selectedMembers) {
        // Reset all states to false first
        for (int i = 0; i < selectedStates.size(); i++) {
            selectedStates.set(i, false);
        }
        
        // Set true for members that are in the selectedMembers list
        for (String selectedMember : selectedMembers) {
            int index = members.indexOf(selectedMember);
            if (index >= 0) {
                selectedStates.set(index, true);
            }
        }
        
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_split_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.checkBox.setText(members.get(position));
        holder.checkBox.setChecked(selectedStates.get(position));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> 
            selectedStates.set(position, isChecked));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public List<String> getSelectedMembers() {
        List<String> selectedMembers = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            if (selectedStates.get(i)) {
                selectedMembers.add(members.get(i));
            }
        }
        return selectedMembers;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBoxMember);
        }
    }
}