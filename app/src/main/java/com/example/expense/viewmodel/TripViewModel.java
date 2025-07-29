package com.example.expense.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.expense.ExpenseApplication;
import com.example.expense.data.AppDatabase;
import com.example.expense.data.entity.Trip;
import com.example.expense.data.repository.TripRepository;

import java.util.List;

public class TripViewModel extends AndroidViewModel {
    private final TripRepository repository;
    private final LiveData<List<Trip>> allTrips;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> tripSaved = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public TripViewModel(Application application) {
        super(application);
        repository = ((ExpenseApplication) application).getTripRepository();
        allTrips = repository.getAllTrips();
    }

    public void insert(Trip trip) {
        isLoading.setValue(true);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                repository.insertTrip(trip);
                tripSaved.postValue(true);
            } catch (Exception e) {
                errorMessage.postValue("Failed to save trip: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getTripSaved() {
        return tripSaved;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<Trip>> getAllTrips() {
        return allTrips;
    }
}
