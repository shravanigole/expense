package com.example.expense.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense.R;
import com.example.expense.adapters.MemberAdapter;
import com.example.expense.fragments.AddTripFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class MemberSelectionDialog extends DialogFragment {
    private List<String> members = new ArrayList<>();
    private EditText editMember;
    private RecyclerView recyclerMembers;
    private MemberAdapter adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_member_selection, null);
        
        editMember = view.findViewById(R.id.editMember);
        Button btnAdd = view.findViewById(R.id.btnAddMember);
        recyclerMembers = view.findViewById(R.id.recyclerMembers);
        
        adapter = new MemberAdapter(members);
        recyclerMembers.setAdapter(adapter);
        recyclerMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        
        btnAdd.setOnClickListener(v -> addMember());

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Members")
                .setView(view)
                .setPositiveButton("Done", (dialog, which) -> {
                    if (getParentFragment() instanceof AddTripFragment) {
                        ((AddTripFragment) getParentFragment()).setMembers(members);
                    }
                })
                .create();
    }

    private void addMember() {
        String member = editMember.getText().toString().trim();
        if (!member.isEmpty()) {
            members.add(member);
            adapter.notifyItemInserted(members.size() - 1);
            editMember.setText("");
        }
    }
}