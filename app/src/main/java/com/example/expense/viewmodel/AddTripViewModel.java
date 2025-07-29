package com.example.expense.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.expense.data.entity.Trip;
import com.example.expense.data.repository.TripRepository;
import com.example.expense.data.AppDatabase;

public class AddTripViewModel extends AndroidViewModel {
    private final TripRepository repository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> tripSaved = new MutableLiveData<>();

    public AddTripViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        repository = new TripRepository(db.tripDao());
    }

    public void saveTrip(Trip trip) {
        isLoading.setValue(true);
        try {
            repository.insertTrip(trip);
            tripSaved.setValue(true);
        } catch (Exception e) {
            errorMessage.setValue("Failed to save trip: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getTripSaved() {
        return tripSaved;
    }
}
