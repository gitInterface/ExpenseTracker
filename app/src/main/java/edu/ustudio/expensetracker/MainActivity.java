package edu.ustudio.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.ustudio.expensetracker.data.repository.ExpenseRepository;
import edu.ustudio.expensetracker.ui.add.AddExpenseActivity;
import edu.ustudio.expensetracker.ui.classify.ClassifyActivity;
import edu.ustudio.expensetracker.ui.detail.DailyDetailActivity;
import edu.ustudio.expensetracker.ui.home.DailySummaryAdapter;
import edu.ustudio.expensetracker.ui.settings.SettingsActivity;
import edu.ustudio.expensetracker.ui.stats.StatsActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txtTodayAmount;
    private TextView txtMonthAmount;
    private TextView txtYearAmount;
    private RecyclerView recyclerView;
    private DailySummaryAdapter adapter;
    private ExpenseRepository repository;
    private ActivityResultLauncher<Intent> addExpenseLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTodayAmount = findViewById(R.id.txtTodayAmount);
        txtMonthAmount = findViewById(R.id.txtMonthAmount);
        txtYearAmount = findViewById(R.id.txtYearAmount);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                return true;
            }

            if (item.getItemId() == R.id.nav_stats) {
                startActivity(new Intent(this, StatsActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_classify) {
                startActivity(new Intent(this, ClassifyActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }

            return false;
        });

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
                        loadDashboardTotals();

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
        loadDashboardTotals();
    }

    private void loadDailySummary() {
        repository.getDailySummaryAsync(list ->
                runOnUiThread(() -> {
                    adapter.setItems(list);
                    recyclerView.scheduleLayoutAnimation();
                })
        );
    }

    private void loadDashboardTotals() {

        repository.getTodayTotalAsync(total ->
                runOnUiThread(() -> txtTodayAmount.setText("$" + total))
        );

        repository.getMonthTotalAsync(total ->
                runOnUiThread(() -> txtMonthAmount.setText("$" + total))
        );

        repository.getYearTotalAsync(total ->
                runOnUiThread(() -> txtYearAmount.setText("$" + total))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDailySummary();
        loadDashboardTotals();
    }

}