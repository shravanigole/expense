package com.example.expense.data.repository;

import androidx.lifecycle.LiveData;
import com.example.expense.data.dao.ExpenseDao;
import com.example.expense.data.entity.Expense;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private final ExpenseDao expenseDao;
    private final ExecutorService executorService;

    public ExpenseRepository(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void insertExpense(Expense expense) {
        executorService.execute(() -> expenseDao.insert(expense));
    }

    public void updateExpense(Expense expense) {
        executorService.execute(() -> expenseDao.update(expense));
    }

    public void deleteExpense(Expense expense) {
        executorService.execute(() -> expenseDao.delete(expense));
    }

    public LiveData<List<Expense>> getExpensesForTrip(long tripId) {
        return expenseDao.getExpensesForTrip(tripId);
    }

    public LiveData<Double> getTotalExpensesForTrip(long tripId) {
        return expenseDao.getTotalExpensesForTrip(tripId);
    }
}
