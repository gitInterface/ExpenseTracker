package edu.ustudio.expensetracker.ui.edit;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.ustudio.expensetracker.R;
import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;
import edu.ustudio.expensetracker.data.repository.ExpenseRepository;

public class EditExpenseActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private Button btnPickGallery;
    private Button btnTakePhoto;

    private EditText editAmount;
    private TextView txtDate;
    private Spinner spinnerCategory;
    private final String[] categories = {
            "未分類",
            "餐飲",
            "交通",
            "購物",
            "娛樂",
            "醫療",
            "美容",
            "帳單",
            "其他"
    };
    private Button buttonSave;

    private ExpenseRepository repository;
    private ExpenseEntity currentExpense;

    private long selectedDate;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        imagePreview = findViewById(R.id.imagePreview);
        btnPickGallery = findViewById(R.id.btnPickGallery);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);

        editAmount = findViewById(R.id.editAmount);
        txtDate = findViewById(R.id.txtDate);
        txtDate.setOnClickListener(v -> {

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(selectedDate);

            new android.app.DatePickerDialog(
                    EditExpenseActivity.this,
                    (view, year, month, dayOfMonth) -> {

                        java.util.Calendar newCal = java.util.Calendar.getInstance();
                        newCal.set(year, month, dayOfMonth, 0, 0, 0);

                        selectedDate = newCal.getTimeInMillis();

                        updateDateLabel();

                    },
                    cal.get(java.util.Calendar.YEAR),
                    cal.get(java.util.Calendar.MONTH),
                    cal.get(java.util.Calendar.DAY_OF_MONTH)
            ).show();

        });
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapterCategory =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        categories);

        spinnerCategory.setAdapter(adapterCategory);
        buttonSave = findViewById(R.id.buttonSave);

        repository = new ExpenseRepository(this);

        long expenseId = getIntent().getLongExtra("expenseId", -1);
        if (expenseId == -1) {
            android.widget.Toast.makeText(this, "資料錯誤", android.widget.Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository.getExpenseByIdAsync(expenseId, expense -> {

            if (expense == null) {

                runOnUiThread(() -> {
                    android.widget.Toast.makeText(
                            EditExpenseActivity.this,
                            "資料不存在",
                            android.widget.Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });

                return;
            }

            currentExpense = expense;

            runOnUiThread(this::loadExpense);

        });

        buttonSave.setOnClickListener(v -> {

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

                        saveExpense();

                    })
                    .start();

        });

    }

    private void loadExpense() {
        if (currentExpense == null) {
            finish();
            return;
        }

        editAmount.setText(String.valueOf(currentExpense.amount));

        selectedDate = currentExpense.expenseDate;

        updateDateLabel();

        String currentCategory = currentExpense.category;
        if (currentCategory == null || currentCategory.trim().isEmpty()) {
            currentCategory = "未分類";
        }
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(currentCategory)) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        if (currentExpense.imageUri != null) {
            selectedImageUri = Uri.parse(currentExpense.imageUri);
            imagePreview.setImageURI(selectedImageUri);
        }

    }

    private void updateDateLabel() {

        SimpleDateFormat df =
                new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        txtDate.setText(df.format(new Date(selectedDate)));

    }

    private void saveExpense() {

        String amountText = editAmount.getText().toString().trim();

        if (amountText.isEmpty()) {

            editAmount.setError("請輸入金額");
            return;

        }

        int amount = Integer.parseInt(amountText);

        currentExpense.amount = amount;
        currentExpense.expenseDate = selectedDate;

        currentExpense.category = spinnerCategory.getSelectedItem().toString();

        if (selectedImageUri != null) {
            currentExpense.imageUri = selectedImageUri.toString();
        }

        repository.updateExpenseAsync(currentExpense);

        finish();

    }
}