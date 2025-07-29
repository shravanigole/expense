package com.example.expense.data.repository;

import androidx.lifecycle.LiveData;
import com.example.expense.data.dao.TripDao;
import com.example.expense.data.entity.Trip;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TripRepository {
    private final TripDao tripDao;
    private final ExecutorService executorService;

    public TripRepository(TripDao tripDao) {
        this.tripDao = tripDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void insertTrip(Trip trip) {
        executorService.execute(() -> tripDao.insert(trip));
    }

    public void updateTrip(Trip trip) {
        executorService.execute(() -> tripDao.update(trip));
    }

    public void deleteTrip(Trip trip) {
        executorService.execute(() -> tripDao.delete(trip));
    }

    public LiveData<List<Trip>> getAllTrips() {
        return tripDao.getAllTrips();
    }

    public LiveData<Trip> getTripById(long tripId) {
        return tripDao.getTripById(tripId);
    }
}