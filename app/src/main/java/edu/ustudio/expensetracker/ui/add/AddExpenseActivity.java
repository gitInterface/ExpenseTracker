package edu.ustudio.expensetracker.ui.add;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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

    private androidx.activity.result.ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        editAmount = findViewById(R.id.editAmount);
        imagePreview = findViewById(R.id.imagePreview);
        btnPickGallery = findViewById(R.id.btnPickGallery);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
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
        buttonSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {

        int amount = Integer.parseInt(editAmount.getText().toString());

        ExpenseEntity expense = new ExpenseEntity();
        expense.amount = amount;
        expense.createdAt = System.currentTimeMillis();
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
}