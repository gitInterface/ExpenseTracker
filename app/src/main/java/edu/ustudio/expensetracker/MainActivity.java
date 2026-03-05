package edu.ustudio.expensetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.ustudio.expensetracker.data.repository.ExpenseRepository;
import edu.ustudio.expensetracker.ui.add.AddExpenseActivity;
import edu.ustudio.expensetracker.ui.home.ExpenseCardAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExpenseCardAdapter adapter;
    private ExpenseRepository repository;
    private androidx.activity.result.ActivityResultLauncher<Intent> addExpenseLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addExpenseLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 只要從 Add 回來就刷新（成功/取消都可以先刷新，最簡單穩）
                    loadExpenses();
                }
        );

        recyclerView = findViewById(R.id.recyclerExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseCardAdapter();
        recyclerView.setAdapter(adapter);

        repository = new ExpenseRepository(this);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddExpenseActivity.class);
            addExpenseLauncher.launch(i);
        });

        loadExpenses();
    }

    private void loadExpenses() {
        repository.getAllAsync(list -> runOnUiThread(() -> adapter.setItems(list)));
    }
}