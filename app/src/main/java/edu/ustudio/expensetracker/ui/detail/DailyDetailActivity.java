package edu.ustudio.expensetracker.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.ustudio.expensetracker.R;
import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;
import edu.ustudio.expensetracker.data.repository.ExpenseRepository;
import edu.ustudio.expensetracker.ui.add.AddExpenseActivity;
import edu.ustudio.expensetracker.ui.edit.EditExpenseActivity;
import edu.ustudio.expensetracker.ui.home.ExpenseCardAdapter;

public class DailyDetailActivity extends AppCompatActivity {

    private TextView txtTitleDate;
    private TextView txtDailyAmount;
    private TextView txtDailyCount;

    private RecyclerView recyclerExpenses;
    private FloatingActionButton fabAddExpense;

    private ExpenseRepository repository;
    private ExpenseCardAdapter adapter;

    private ActivityResultLauncher<Intent> addExpenseLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);

        txtTitleDate = findViewById(R.id.txtTitleDate);
        txtDailyAmount = findViewById(R.id.txtDailyAmount);
        txtDailyCount = findViewById(R.id.txtDailyCount);

        recyclerExpenses = findViewById(R.id.recyclerExpenses);
        fabAddExpense = findViewById(R.id.fabAddExpense);

        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));


        adapter = new ExpenseCardAdapter();

        adapter.setOnDeleteClickListener(expense -> {
            new android.app.AlertDialog.Builder(DailyDetailActivity.this)
                    .setTitle("刪除記帳")
                    .setMessage("確定刪除這筆記帳？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("刪除", (dialog, which) -> {
                        repository.deleteExpenseAsync(expense);
                        loadExpenses();
                    })
                    .show();
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

        addExpenseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> loadExpenses()
        );

        fabAddExpense.setOnClickListener(v -> {
            long date = getIntent().getLongExtra("expenseDate", 0);

            Intent intent = new Intent(DailyDetailActivity.this, AddExpenseActivity.class);
            intent.putExtra("presetExpenseDate", date);

            addExpenseLauncher.launch(intent);
        });

        loadExpenses();
    }

    private void loadExpenses() {
        long expenseDate = getIntent().getLongExtra("expenseDate", 0);

        repository.getExpensesByDateAsync(expenseDate, list ->
                runOnUiThread(() -> {
                    if (list == null || list.isEmpty()) {
                        finish();
                        return;
                    }

                    adapter.setItems(list);
                    recyclerExpenses.scheduleLayoutAnimation();

                    updateSummary(list);
                })
        );
    }

    private void updateSummary(List<ExpenseEntity> list) {
        int total = 0;

        for (ExpenseEntity expense : list) {
            total += expense.amount;
        }

        txtDailyAmount.setText("$" + total);
        txtDailyCount.setText(String.valueOf(list.size()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }
}
