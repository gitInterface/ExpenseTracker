package edu.ustudio.expensetracker.data.repository;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ustudio.expensetracker.data.local.dao.ExpenseDao;
import edu.ustudio.expensetracker.data.local.db.AppDatabase;
import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;

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
}
