package com.example.expense.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.expense.data.converter.StringListConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "trips")
public class Trip implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    @TypeConverters(StringListConverter.class)
    private List<String> members;

    public Trip() {
        members = new ArrayList<>();
    }

    protected Trip(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        members = in.createStringArrayList();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public List<String> getMembers() { return members; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setMembers(List<String> members) { this.members = members; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeStringList(members);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id == trip.id &&
                Objects.equals(title, trip.title) &&
                Objects.equals(description, trip.description) &&
                Objects.equals(startDate, trip.startDate) &&
                Objects.equals(endDate, trip.endDate) &&
                Objects.equals(members, trip.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, startDate, endDate, members);
    }
}
