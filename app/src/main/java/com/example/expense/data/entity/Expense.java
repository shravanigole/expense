package com.example.expense.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.expense.data.converter.DateConverter;
import com.example.expense.data.converter.StringListConverter;
import java.util.Date;
import java.util.List;

@Entity(tableName = "expenses")
@TypeConverters({DateConverter.class, StringListConverter.class})
public class Expense implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long tripId;
    private String title;
    private double amount;
    private String category;
    private String paidBy;
    private Date date;
    private List<String> splitBetween;
    private List<String> splitMembers;

    public Expense(long tripId, String title, double amount, String category, String paidBy, Date date, List<String> splitBetween) {
        this.tripId = tripId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.paidBy = paidBy;
        this.date = date;
        this.splitBetween = splitBetween;
    }

    protected Expense(Parcel in) {
        id = in.readLong();
        tripId = in.readLong();
        title = in.readString();
        amount = in.readDouble();
        category = in.readString();
        paidBy = in.readString();
        date = new Date(in.readLong());
        splitBetween = in.createStringArrayList();
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    // Existing getters
    public long getId() { return id; }
    public long getTripId() { return tripId; }
    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getPaidBy() { return paidBy; }
    public Date getDate() { return date; }
    public List<String> getSplitBetween() { return splitBetween; }

    // Existing setters
    public void setId(long id) { this.id = id; }
    public void setTripId(long tripId) { this.tripId = tripId; }
    public void setTitle(String title) { this.title = title; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
    public void setDate(Date date) { this.date = date; }
    public void setSplitBetween(List<String> splitBetween) { this.splitBetween = splitBetween; }

    // New getters and setters
    public List<String> getSplitMembers() {
        return splitMembers;
    }

    public void setSplitMembers(List<String> splitMembers) {
        this.splitMembers = splitMembers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(tripId);
        dest.writeString(title);
        dest.writeDouble(amount);
        dest.writeString(category);
        dest.writeString(paidBy);
        dest.writeLong(date != null ? date.getTime() : 0);
        dest.writeStringList(splitBetween);
    }
}
