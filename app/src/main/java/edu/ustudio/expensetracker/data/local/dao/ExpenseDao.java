package edu.ustudio.expensetracker.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;
import edu.ustudio.expensetracker.data.model.CategoryTotal;
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

    @Query(
            "SELECT IFNULL(SUM(amount), 0) FROM expense " +
                    "WHERE date(expenseDate/1000,'unixepoch','localtime') = date('now','localtime')"
    )
    int getTodayTotal();

    @Query(
            "SELECT IFNULL(SUM(amount), 0) FROM expense " +
                    "WHERE strftime('%Y-%m', expenseDate/1000, 'unixepoch', 'localtime') = strftime('%Y-%m', 'now', 'localtime')"
    )
    int getMonthTotal();

    @Query(
            "SELECT IFNULL(SUM(amount),0) FROM expense " +
                    "WHERE strftime('%Y', expenseDate/1000,'unixepoch','localtime') = :year " +
                    "AND strftime('%m', expenseDate/1000,'unixepoch','localtime') = :month"
    )
    int getMonthTotalByYearMonth(String year, String month);

    @Query(
            "SELECT IFNULL(SUM(amount), 0) FROM expense " +
                    "WHERE strftime('%Y', expenseDate/1000, 'unixepoch', 'localtime') = strftime('%Y', 'now', 'localtime')"
    )
    int getYearTotal();

    @Query("SELECT IFNULL(SUM(amount),0) FROM expense")
    int getTotalExpense();

    @Query(
            "SELECT category, SUM(amount) as total " +
                    "FROM expense " +
                    "GROUP BY category " +
                    "ORDER BY total DESC"
    )
    List<CategoryTotal> getCategoryTotals();

    @Query(
            "SELECT category AS category, SUM(amount) AS total " +
                    "FROM expense " +
                    "WHERE strftime('%Y', expenseDate/1000,'unixepoch','localtime') = :year " +
                    "AND strftime('%m', expenseDate/1000,'unixepoch','localtime') = :month " +
                    "GROUP BY category " +
                    "ORDER BY total DESC"
    )
    List<CategoryTotal> getCategoryTotalsByYearMonth(String year, String month);

    @Query(
            "SELECT DISTINCT strftime('%Y', expenseDate/1000,'unixepoch','localtime') AS year " +
                    "FROM expense " +
                    "ORDER BY year DESC"
    )
    List<String> getAvailableYears();

    @Query(
            "SELECT COUNT(*) FROM expense " +
                    "WHERE strftime('%Y', expenseDate/1000,'unixepoch','localtime') = :year " +
                    "AND strftime('%m', expenseDate/1000,'unixepoch','localtime') = :month"
    )
    int getRecordCountByYearMonth(String year, String month);
}