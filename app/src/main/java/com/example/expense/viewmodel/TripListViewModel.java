package com.example.expense.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.expense.data.AppDatabase;
import com.example.expense.data.entity.Trip;
import java.util.List;

public class TripListViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final LiveData<List<Trip>> trips;

    public TripListViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        trips = database.tripDao().getAllTrips();
    }

    public LiveData<List<Trip>> getTrips() {
        return trips;
    }
}