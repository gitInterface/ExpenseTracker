package edu.ustudio.expensetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.ustudio.expensetracker.data.repository.ExpenseRepository;
import edu.ustudio.expensetracker.ui.add.AddExpenseActivity;
import edu.ustudio.expensetracker.ui.detail.DailyDetailActivity;
import edu.ustudio.expensetracker.ui.home.DailySummaryAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DailySummaryAdapter adapter;
    private ExpenseRepository repository;
    private ActivityResultLauncher<Intent> addExpenseLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DailySummaryAdapter();
        recyclerView.setAdapter(adapter);

        // Repository
        repository = new ExpenseRepository(this);

        // 點每日摘要卡片 → 進入當日明細頁
        adapter.setOnDayClickListener(date -> {
            Intent intent = new Intent(MainActivity.this, DailyDetailActivity.class);
            intent.putExtra("expenseDate", date);
            startActivity(intent);
        });

        // 點每日摘要卡片上的 X → 刪除整天記帳
        adapter.setOnDeleteDayClickListener(date -> {

            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("刪除今日記帳")
                    .setMessage("確定刪除今天所有記帳？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("刪除", (dialog, which) -> {

                        repository.deleteByDateAsync(date);
                        loadDailySummary();

                    })
                    .show();

        });

        // AddExpenseActivity 返回後刷新首頁
        addExpenseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> loadDailySummary()
        );

        // FAB 新增記帳
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(80)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(80)
                                .start();

                        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                        addExpenseLauncher.launch(intent);
                    })
                    .start();
        });

        // 首次載入
        loadDailySummary();
    }

    private void loadDailySummary() {
        repository.getDailySummaryAsync(list ->
                runOnUiThread(() -> {
                    adapter.setItems(list);
                    recyclerView.scheduleLayoutAnimation();
                })
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDailySummary();
    }
}