package edu.ustudio.expensetracker.ui.add;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.ustudio.expensetracker.R;
import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;
import edu.ustudio.expensetracker.data.repository.ExpenseRepository;
import edu.ustudio.expensetracker.core.util.ImageStorageHelper;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText editAmount;
    private Button buttonSave;
    private ExpenseRepository repository;
    private android.widget.ImageView imagePreview;
    private android.widget.Button btnPickGallery;
    private android.widget.Button btnTakePhoto;
    private android.net.Uri selectedImageUri = null;
    private TextView txtDate;
    private long selectedDate;
    private androidx.activity.result.ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        editAmount = findViewById(R.id.editAmount);
        imagePreview = findViewById(R.id.imagePreview);
        btnPickGallery = findViewById(R.id.btnPickGallery);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        txtDate = findViewById(R.id.txtDate);
        txtDate.setOnClickListener(v -> {

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(selectedDate);

            new android.app.DatePickerDialog(
                    AddExpenseActivity.this,
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
        selectedDate = System.currentTimeMillis();
        updateDateLabel();
        buttonSave = findViewById(R.id.buttonSave);

        repository = new ExpenseRepository(this);
        pickImageLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imagePreview.setImageURI(uri);
                    }
                }
        );

        btnPickGallery.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
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

    private void saveExpense() {

        int amount = Integer.parseInt(editAmount.getText().toString());

        ExpenseEntity expense = new ExpenseEntity();
        expense.amount = amount;
        expense.createdAt = System.currentTimeMillis();
        expense.expenseDate = selectedDate;
        expense.status = "UNCLASSIFIED";
        expense.needsReview = false;
        if (selectedImageUri != null) {
            String localPath = ImageStorageHelper.copyImageToAppStorage(this, selectedImageUri);
            expense.imageUri = localPath;
        } else {
            expense.imageUri = "";
        }
        expense.category = "";

        repository.insertAsync(expense, id -> {
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });

    }

    private void updateDateLabel() {

        java.text.SimpleDateFormat df =
                new java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());

        txtDate.setText(df.format(new java.util.Date(selectedDate)));

    }
}