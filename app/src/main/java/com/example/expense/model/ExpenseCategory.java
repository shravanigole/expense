package com.example.expense.model;

import com.example.expense.R;

public enum ExpenseCategory {
    FOOD("Food", R.drawable.ic_food),
    TRANSPORT("Transport", R.drawable.ic_transport),
    ACCOMMODATION("Accommodation", R.drawable.ic_accommodation),
    SHOPPING("Shopping", R.drawable.ic_shopping),
    ACTIVITIES("Activities", R.drawable.ic_activities),
    OTHER("Other", R.drawable.ic_other);

    private final String displayName;
    private final int iconResId;

    ExpenseCategory(String displayName, int iconResId) {
        this.displayName = displayName;
        this.iconResId = iconResId;
    }

    public String getDisplayName() { return displayName; }
    public int getIconResId() { return iconResId; }
}