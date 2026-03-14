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
            "餐飲",
            "購物",
            "交通",
            "帳單",
            "醫療",
            "娛樂",
            "美容",
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

        repository.getExpenseByIdAsync(expenseId, expense -> {

            currentExpense = expense;

            runOnUiThread(() -> loadExpense());

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

        editAmount.setText(String.valueOf(currentExpense.amount));

        selectedDate = currentExpense.expenseDate;

        updateDateLabel();

        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(currentExpense.category)) {
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

        int amount = Integer.parseInt(editAmount.getText().toString());

        currentExpense.amount = amount;
        currentExpense.expenseDate = selectedDate;

        String category = spinnerCategory.getSelectedItem().toString();
        currentExpense.category = category;

        if (selectedImageUri != null) {
            currentExpense.imageUri = selectedImageUri.toString();
        }

        repository.updateExpenseAsync(currentExpense);

        finish();

    }
}