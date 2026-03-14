package edu.ustudio.expensetracker.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;
import edu.ustudio.expensetracker.data.model.DailySummary;


@Dao
public interface ExpenseDao {

    @Insert
    long insert(ExpenseEntity expense);

    @Query("SELECT * FROM expense ORDER BY createdAt DESC")
    List<ExpenseEntity> getAll();

    @Query(
            "SELECT " +
                    "expenseDate as expenseDate, " +
                    "SUM(amount) as totalAmount, " +
                    "MIN(imageUri) as coverImageUri, " +
                    "COUNT(*) as expenseCount " +
                    "FROM expense " +
                    "GROUP BY expenseDate " +
                    "ORDER BY expenseDate DESC"
    )
    List<DailySummary> getDailySummary();

    @Query(
            "SELECT * FROM expense " +
                    "WHERE expenseDate = :date " +
                    "ORDER BY createdAt DESC"
    )
    List<ExpenseEntity> getExpensesByDate(long date);

    @Delete
    void deleteExpense(ExpenseEntity expense);

    @Query(
            "DELETE FROM expense " +
                    "WHERE expenseDate = :date"
    )
    void deleteByDate(long date);

    @Query("SELECT * FROM expense WHERE id = :id LIMIT 1")
    ExpenseEntity getExpenseById(long id);

    @Update
    void updateExpense(ExpenseEntity expense);
}