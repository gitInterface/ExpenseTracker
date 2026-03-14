package edu.ustudio.expensetracker.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.ustudio.expensetracker.R;
import edu.ustudio.expensetracker.data.repository.ExpenseRepository;
import edu.ustudio.expensetracker.ui.edit.EditExpenseActivity;
import edu.ustudio.expensetracker.ui.home.ExpenseCardAdapter;

public class DailyDetailActivity extends AppCompatActivity {

    private TextView txtTitleDate;
    private RecyclerView recyclerExpenses;
    private ExpenseRepository repository;
    private ExpenseCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);

        txtTitleDate = findViewById(R.id.txtTitleDate);
        recyclerExpenses = findViewById(R.id.recyclerExpenses);

        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseCardAdapter();
        adapter.setOnDeleteClickListener(expense -> {

            repository.deleteExpenseAsync(expense);

            recreate(); // 簡單刷新頁面

        });
        adapter.setOnCardClickListener(expense -> {

            Intent intent = new Intent(DailyDetailActivity.this, EditExpenseActivity.class);
            intent.putExtra("expenseId", expense.id);

            startActivity(intent);

        });
        recyclerExpenses.setAdapter(adapter);

        repository = new ExpenseRepository(this);

        long expenseDate = getIntent().getLongExtra("expenseDate", 0);

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        txtTitleDate.setText(df.format(new Date(expenseDate)));

        repository.getExpensesByDateAsync(expenseDate, list ->
                runOnUiThread(() -> adapter.setItems(list))
        );
    }
}