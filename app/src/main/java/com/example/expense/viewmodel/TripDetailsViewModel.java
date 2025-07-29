package com.example.expense.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.expense.data.AppDatabase;
import com.example.expense.data.entity.Trip;
import com.example.expense.data.entity.Expense;
import com.example.expense.model.ExpenseCategory;
import java.util.*;
import java.util.stream.Collectors;

public class TripDetailsViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private LiveData<Trip> trip;
    private LiveData<List<Expense>> expenses;
    private MutableLiveData<List<Expense>> filteredExpenses = new MutableLiveData<>();
    private MutableLiveData<Map<String, Double>> settlements = new MutableLiveData<>();
    private MutableLiveData<List<Settlement>> optimizedSettlements = new MutableLiveData<>();

    public TripDetailsViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public void loadTrip(long tripId) {
        trip = database.tripDao().getTripById(tripId);
        expenses = database.expenseDao().getExpensesForTrip(tripId);
        expenses.observeForever(expenseList -> {
            if (expenseList != null) {
                filteredExpenses.setValue(expenseList);
                calculateSettlements(expenseList);
            }
        });
    }

    public LiveData<Trip> getTrip() {
        return trip;
    }

    public LiveData<List<Expense>> getExpenses() {
        return expenses;
    }

    public LiveData<List<Expense>> getFilteredExpenses() {
        return filteredExpenses;
    }

    public LiveData<Map<String, Double>> getSettlements() {
        return settlements;
    }

    public LiveData<List<Settlement>> getOptimizedSettlements() {
        return optimizedSettlements;
    }

    public void addExpense(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.expenseDao().insert(expense);
        });
    }

    public void updateExpense(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.expenseDao().update(expense);
        });
    }

    public void filterExpenses(Set<ExpenseCategory> categories) {
        List<Expense> currentExpenses = expenses.getValue();
        if (currentExpenses == null) return;

        if (categories.isEmpty()) {
            filteredExpenses.setValue(currentExpenses);
            calculateSettlements(currentExpenses);
            return;
        }

        List<Expense> filtered = currentExpenses.stream()
            .filter(expense -> categories.contains(expense.getCategory()))
            .collect(Collectors.toList());
        
        filteredExpenses.setValue(filtered);
        calculateSettlements(filtered);
    }

    private void calculateSettlements(List<Expense> expenseList) {
        Trip currentTrip = trip.getValue();
        if (currentTrip == null) return;

        Map<String, Double> balances = new HashMap<>();
        double totalExpense = 0;

        // Initialize balances
        for (String member : currentTrip.getMembers()) {
            balances.put(member, 0.0);
        }

        // Calculate payments
        for (Expense expense : expenseList) {
            String paidBy = expense.getPaidBy();
            double amount = expense.getAmount();
            balances.put(paidBy, balances.get(paidBy) + amount);
            totalExpense += amount;
        }

        // Calculate per person share
        double perPersonShare = totalExpense / currentTrip.getMembers().size();

        // Adjust balances
        Map<String, Double> finalSettlements = new HashMap<>();
        for (String member : currentTrip.getMembers()) {
            double balance = balances.get(member) - perPersonShare;
            finalSettlements.put(member, balance);
        }

        settlements.postValue(finalSettlements);
        optimizeSettlements(finalSettlements);
    }

    private void optimizeSettlements(Map<String, Double> balances) {
        List<Settlement> settlements = new ArrayList<>();
        List<String> debtors = new ArrayList<>();
        List<String> creditors = new ArrayList<>();

        // Separate members into debtors and creditors
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            if (entry.getValue() < 0) {
                debtors.add(entry.getKey());
            } else if (entry.getValue() > 0) {
                creditors.add(entry.getKey());
            }
        }

        // Match debtors with creditors
        for (String debtor : debtors) {
            double debt = Math.abs(balances.get(debtor));
            for (String creditor : creditors) {
                double credit = balances.get(creditor);
                if (credit <= 0) continue;

                double amount = Math.min(debt, credit);
                if (amount > 0) {
                    settlements.add(new Settlement(debtor, creditor, amount));
                    balances.put(creditor, credit - amount);
                    debt -= amount;
                }
                if (debt <= 0) break;
            }
        }

        optimizedSettlements.postValue(settlements);
    }

    public static class Settlement {
        public final String from;
        public final String to;
        public final double amount;

        public Settlement(String from, String to, double amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
    }

    public void deleteExpense(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.expenseDao().delete(expense);
        });
    }

    public void deleteTrip(Trip trip) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // First delete all expenses associated with this trip
            database.expenseDao().deleteExpensesForTrip(trip.getId());
            // Then delete the trip
            database.tripDao().delete(trip);
        });
    }
}
