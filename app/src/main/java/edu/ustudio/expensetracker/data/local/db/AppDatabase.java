package edu.ustudio.expensetracker.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.ustudio.expensetracker.data.local.dao.ExpenseDao;
import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;

@Database(entities = {ExpenseEntity.class}, version = 2, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "expense_tracker.db";
    private static volatile AppDatabase INSTANCE;

    public abstract ExpenseDao expenseDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}