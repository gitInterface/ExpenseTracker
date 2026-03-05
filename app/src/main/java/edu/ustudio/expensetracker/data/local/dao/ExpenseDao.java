package edu.ustudio.expensetracker.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;


@Dao
public interface ExpenseDao {

    @Insert
    long insert(ExpenseEntity expense);

    @Query("SELECT * FROM expense ORDER BY createdAt DESC")
    List<ExpenseEntity> getAll();

}