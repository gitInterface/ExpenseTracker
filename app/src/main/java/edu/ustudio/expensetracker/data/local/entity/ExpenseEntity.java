package edu.ustudio.expensetracker.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense")
public class ExpenseEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int amount;

    public String imageUri;

    public String category;

    public String status;

    public boolean needsReview;

    public long createdAt;

    public long classificationDeadline;

}
