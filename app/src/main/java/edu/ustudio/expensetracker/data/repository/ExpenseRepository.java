package edu.ustudio.expensetracker.data.repository;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ustudio.expensetracker.data.local.dao.ExpenseDao;
import edu.ustudio.expensetracker.data.local.db.AppDatabase;
import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;
import edu.ustudio.expensetracker.data.model.CategoryTotal;
import edu.ustudio.expensetracker.data.model.DailySummary;

public class ExpenseRepository {

    private final ExpenseDao expenseDao;
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    public ExpenseRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.expenseDao = db.expenseDao();
    }

    // callback 介面：讓 UI 拿到結果
    public interface ResultCallback<T> {
        void onResult(T result);
    }

    public void insertAsync(ExpenseEntity expense, ResultCallback<Long> callback) {
        dbExecutor.execute(() -> {
            long id = expenseDao.insert(expense);
            if (callback != null) callback.onResult(id);
        });
    }

    public void getAllAsync(ResultCallback<List<ExpenseEntity>> callback) {
        dbExecutor.execute(() -> {
            List<ExpenseEntity> list = expenseDao.getAll();
            if (callback != null) callback.onResult(list);
        });
    }

    public void getDailySummaryAsync(ResultCallback<List<DailySummary>> callback) {

        dbExecutor.execute(() -> {

            List<DailySummary> list = expenseDao.getDailySummary();

            if (callback != null) {
                callback.onResult(list);
            }

        });

    }

    public void getExpensesByDateAsync(long date,
                                       ResultCallback<List<ExpenseEntity>> callback) {

        dbExecutor.execute(() -> {

            List<ExpenseEntity> list =
                    expenseDao.getExpensesByDate(date);

            if (callback != null) {
                callback.onResult(list);
            }

        });
    }

    public void deleteExpenseAsync(ExpenseEntity expense) {

        dbExecutor.execute(() -> {
            expenseDao.deleteExpense(expense);
        });

    }

    public void deleteByDateAsync(long date) {

        dbExecutor.execute(() -> {
            expenseDao.deleteByDate(date);
        });

    }

    public void getExpenseByIdAsync(long id, ResultCallback<ExpenseEntity> callback) {

        dbExecutor.execute(() -> {

            ExpenseEntity e = expenseDao.getExpenseById(id);

            if (callback != null) {
                callback.onResult(e);
            }

        });
    }

    public void updateExpenseAsync(ExpenseEntity expense) {

        dbExecutor.execute(() -> {
            expenseDao.updateExpense(expense);
        });

    }

    public void getTodayTotalAsync(ResultCallback<Integer> callback) {
        dbExecutor.execute(() -> {
            int total = expenseDao.getTodayTotal();
            if (callback != null) {
                callback.onResult(total);
            }
        });
    }

    public void getMonthTotalAsync(ResultCallback<Integer> callback) {
        dbExecutor.execute(() -> {
            int total = expenseDao.getMonthTotal();
            if (callback != null) {
                callback.onResult(total);
            }
        });
    }

    public void getYearTotalAsync(ResultCallback<Integer> callback) {
        dbExecutor.execute(() -> {
            int total = expenseDao.getYearTotal();
            if (callback != null) {
                callback.onResult(total);
            }
        });
    }

    public void getCategoryTotalsAsync(ResultCallback<List<CategoryTotal>> callback) {

        dbExecutor.execute(() -> {

            List<CategoryTotal> list = expenseDao.getCategoryTotals();

            if (callback != null) {
                callback.onResult(list);
            }

        });
    }

    public void getMonthTotalByYearMonthAsync(String year, String month, ResultCallback<Integer> callback) {
        dbExecutor.execute(() -> {
            int total = expenseDao.getMonthTotalByYearMonth(year, month);
            if (callback != null) {
                callback.onResult(total);
            }
        });
    }

    public void getCategoryTotalsByYearMonthAsync(String year, String month, ResultCallback<List<CategoryTotal>> callback) {
        dbExecutor.execute(() -> {
            List<CategoryTotal> list = expenseDao.getCategoryTotalsByYearMonth(year, month);
            if (callback != null) {
                callback.onResult(list);
            }
        });
    }

    public void getAvailableYearsAsync(ResultCallback<List<String>> callback) {
        dbExecutor.execute(() -> {
            List<String> years = expenseDao.getAvailableYears();

            if (callback != null) {
                callback.onResult(years);
            }
        });
    }

    public void getRecordCountByYearMonthAsync(String year, String month, ResultCallback<Integer> callback) {
        dbExecutor.execute(() -> {
            int count = expenseDao.getRecordCountByYearMonth(year, month);
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }
}
