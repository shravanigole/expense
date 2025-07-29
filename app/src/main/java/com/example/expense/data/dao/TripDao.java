package com.example.expense.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.expense.data.entity.Trip;
import java.util.List;

@Dao
public interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    LiveData<List<Trip>> getAllTrips();

    @Query("SELECT * FROM trips WHERE id = :tripId")
    LiveData<Trip> getTripById(long tripId);

    @Query("SELECT members FROM trips WHERE id = :tripId")
    LiveData<List<String>> getTripMembersById(long tripId);

    @Insert
    long insert(Trip trip);

    @Update
    void update(Trip trip);

    @Delete
    void delete(Trip trip);
}
