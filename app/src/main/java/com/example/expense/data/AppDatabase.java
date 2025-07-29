package com.example.expense.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.expense.data.converter.DateConverter;
import com.example.expense.data.converter.StringListConverter;
import com.example.expense.data.dao.ExpenseDao;
import com.example.expense.data.dao.TripDao;
import com.example.expense.data.entity.Expense;
import com.example.expense.data.entity.Trip;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {Trip.class, Expense.class},
    version = 4, // Update version to latest
    exportSchema = true // Set to true to export schema
)
@TypeConverters({DateConverter.class, StringListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    
    public static final ExecutorService databaseWriteExecutor = 
        Executors.newFixedThreadPool(4);

    public abstract TripDao tripDao();
    public abstract ExpenseDao expenseDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Rename imageUri to imageUrl in trips table
            database.execSQL("ALTER TABLE trips RENAME COLUMN imageUri TO imageUrl");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE expenses ADD COLUMN splitBetween TEXT");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE expenses ADD COLUMN splitMembers TEXT");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "expense_database")
                            .addMigrations(MIGRATION_1_2) // Add all migrations
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
