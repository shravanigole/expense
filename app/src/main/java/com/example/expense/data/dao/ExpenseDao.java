package com.example.expense.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.expense.data.entity.Expense;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesForTrip(long tripId);

    @Insert
    long insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT SUM(amount) FROM expenses WHERE tripId = :tripId")
    LiveData<Double> getTotalExpensesForTrip(long tripId);

    @Query("DELETE FROM expenses WHERE tripId = :tripId")
    void deleteExpensesForTrip(long tripId);
}
