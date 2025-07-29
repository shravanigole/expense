package com.example.expense.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.expense.data.AppDatabase;
import com.example.expense.data.entity.Expense;
import com.example.expense.data.entity.Trip;
import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final MutableLiveData<Boolean> expenseSaved = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ExpenseViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public void addExpense(Expense expense) {
        isLoading.setValue(true);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                database.expenseDao().insert(expense);
                expenseSaved.postValue(true);
            } catch (Exception e) {
                errorMessage.postValue("Failed to save expense: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public LiveData<List<String>> getTripMembers(long tripId) {
        return Transformations.map(database.tripDao().getTripById(tripId),
            trip -> trip != null ? trip.getMembers() : null);
    }

    public LiveData<Boolean> getExpenseSaved() {
        return expenseSaved;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
