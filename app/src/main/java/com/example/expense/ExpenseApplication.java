package com.example.expense;

import android.app.Application;
import com.example.expense.data.AppDatabase;
import com.example.expense.data.repository.TripRepository;
import com.example.expense.data.repository.ExpenseRepository;

public class ExpenseApplication extends Application {
    private AppDatabase database;
    private TripRepository tripRepository;
    private ExpenseRepository expenseRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
        tripRepository = new TripRepository(database.tripDao());
        expenseRepository = new ExpenseRepository(database.expenseDao());
    }

    public TripRepository getTripRepository() {
        return tripRepository;
    }

    public ExpenseRepository getExpenseRepository() {
        return expenseRepository;
    }
}
