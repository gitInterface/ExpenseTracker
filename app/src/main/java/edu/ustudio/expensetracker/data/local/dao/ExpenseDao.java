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
                    "MAX(e.expenseDate) AS expenseDate, " +
                    "SUM(e.amount) AS totalAmount, " +
                    "(" +
                    "   SELECT imageUri FROM expense e2 " +
                    "   WHERE date(e2.expenseDate/1000,'unixepoch','localtime') = date(e.expenseDate/1000,'unixepoch','localtime') " +
                    "   ORDER BY e2.expenseDate DESC " +
                    "   LIMIT 1" +
                    ") AS coverImageUri, " +
                    "COUNT(*) AS expenseCount " +
                    "FROM expense e " +
                    "GROUP BY date(e.expenseDate/1000,'unixepoch','localtime') " +
                    "ORDER BY MAX(e.expenseDate) DESC"
    )
    List<DailySummary> getDailySummary();

    @Query(
            "SELECT * FROM expense " +
                    "WHERE date(expenseDate/1000,'unixepoch','localtime') = " +
                    "date(:date/1000,'unixepoch','localtime') " +
                    "ORDER BY expenseDate DESC"
    )
    List<ExpenseEntity> getExpensesByDate(long date);

    @Delete
    void deleteExpense(ExpenseEntity expense);

    @Query(
            "DELETE FROM expense " +
                    "WHERE date(expenseDate/1000,'unixepoch','localtime') = " +
                    "date(:date/1000,'unixepoch','localtime')"
    )
    void deleteByDate(long date);

    @Query("SELECT * FROM expense WHERE id = :id LIMIT 1")
    ExpenseEntity getExpenseById(long id);

    @Update
    void updateExpense(ExpenseEntity expense);
}