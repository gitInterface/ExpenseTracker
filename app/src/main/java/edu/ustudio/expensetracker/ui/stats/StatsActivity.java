package edu.ustudio.expensetracker.ui.stats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ustudio.expensetracker.MainActivity;
import edu.ustudio.expensetracker.R;
import edu.ustudio.expensetracker.data.model.CategoryTotal;
import edu.ustudio.expensetracker.data.repository.ExpenseRepository;
import edu.ustudio.expensetracker.ui.classify.ClassifyActivity;
import edu.ustudio.expensetracker.ui.settings.SettingsActivity;

public class StatsActivity extends AppCompatActivity {

    private TextView txtSelectedYear;
    private TextView txtSelectedMonth;
    private View layoutYearPicker;
    private View layoutMonthPicker;
    private List<String> availableYears = new ArrayList<>();
    private String selectedYear;
    private String selectedMonth;
    private TextView txtTopCategory;
    private TextView txtRecordCount;
    private LinearLayout layoutCategoryList;
    private TextView txtMonthTotal;
    private PieChart pieChartCategory;
    private ExpenseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        txtSelectedYear = findViewById(R.id.txtSelectedYear);
        txtSelectedMonth = findViewById(R.id.txtSelectedMonth);
        layoutYearPicker = findViewById(R.id.layoutYearPicker);
        layoutMonthPicker = findViewById(R.id.layoutMonthPicker);

        txtTopCategory = findViewById(R.id.txtTopCategory);
        txtRecordCount = findViewById(R.id.txtRecordCount);

        layoutCategoryList = findViewById(R.id.layoutCategoryList);

        txtMonthTotal = findViewById(R.id.txtMonthTotal);
        pieChartCategory = findViewById(R.id.pieChartCategory);

        repository = new ExpenseRepository(this);

        setupBottomNav();
        setupYearMonthPicker();
        setupPieChart();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setSelectedItemId(R.id.nav_stats);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_stats) {
                return true;
            }

            if (item.getItemId() == R.id.nav_classify) {
                startActivity(new Intent(this, ClassifyActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }

    private void setupYearMonthPicker() {

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int currentYear = calendar.get(java.util.Calendar.YEAR);
        int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1;

        selectedYear = String.valueOf(currentYear);
        selectedMonth = String.format(java.util.Locale.getDefault(), "%02d", currentMonth);

        repository.getAvailableYearsAsync(years -> runOnUiThread(() -> {

            if (years == null || years.isEmpty()) {
                availableYears = new ArrayList<>();
                availableYears.add(String.valueOf(currentYear));
            } else {
                availableYears = new ArrayList<>(years);
            }

            if (!availableYears.contains(selectedYear)) {
                selectedYear = availableYears.get(0);
            }

            txtSelectedYear.setText(selectedYear);
            int monthIndex = Integer.parseInt(selectedMonth) - 1;

            String[] displayMonths = {
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            };

            txtSelectedMonth.setText(displayMonths[monthIndex]);

            layoutYearPicker.setOnClickListener(v -> showYearDialog());
            layoutMonthPicker.setOnClickListener(v -> showMonthDialog());

            loadStats(selectedYear, selectedMonth);

        }));
    }

    private void showYearDialog() {

        String[] items = availableYears.toArray(new String[0]);

        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Year")
                .setItems(items, (dialog, which) -> {
                    selectedYear = items[which];
                    txtSelectedYear.setText(selectedYear);
                    loadStats(selectedYear, selectedMonth);
                })
                .show();
    }

    private void showMonthDialog() {

        String[] displayMonths = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        String[] valueMonths = {
                "01", "02", "03", "04", "05", "06",
                "07", "08", "09", "10", "11", "12"
        };

        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Month")
                .setItems(displayMonths, (dialog, which) -> {

                    selectedMonth = valueMonths[which];   // 用來查 DB
                    txtSelectedMonth.setText(displayMonths[which]); // 顯示用

                    loadStats(selectedYear, selectedMonth);

                })
                .show();
    }

    private int getCategoryColor(String category) {

        if (category == null) return 0xFF9E9E9E;

        switch (category) {
            case "餐飲":
                return 0xFFFF7A59; // 橘
            case "交通":
                return 0xFF3B82F6; // 藍
            case "購物":
                return 0xFFEC4899; // 粉
            case "娛樂":
                return 0xFFF59E0B; // 黃
            case "醫療":
                return 0xFF22C55E; // 綠
            case "美容":
                return 0xFF8B5CF6; // 紫
            case "帳單":
                return 0xFF14B8A6; // 青
            case "其他":
                return 0xFF7C7C7C; // 深灰
            case "未分類":
            default:
                return 0xFFB0B0B0; // 淺灰
        }
    }

    private void setupPieChart() {

        pieChartCategory.setUsePercentValues(true);
        pieChartCategory.setDrawHoleEnabled(true);
        pieChartCategory.setHoleRadius(58f);
        pieChartCategory.setTransparentCircleRadius(62f);

        pieChartCategory.setDrawEntryLabels(false);
        pieChartCategory.setDrawCenterText(true);
        pieChartCategory.setCenterText("Spending");
        pieChartCategory.setCenterTextSize(16f);
        pieChartCategory.setCenterTextColor(0xFF1A1423);

        pieChartCategory.setEntryLabelTextSize(12f);
        pieChartCategory.setEntryLabelColor(0xFFFFFFFF);

        pieChartCategory.setRotationEnabled(true);
        pieChartCategory.setHighlightPerTapEnabled(true);

        pieChartCategory.setExtraOffsets(12f, 12f, 12f, 12f);

        com.github.mikephil.charting.components.Description description =
                new com.github.mikephil.charting.components.Description();
        description.setText("");
        pieChartCategory.setDescription(description);

        com.github.mikephil.charting.components.Legend legend = pieChartCategory.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
        legend.setTextColor(0xFF4A4453);
        legend.setVerticalAlignment(com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private void loadStats(String year, String month) {
        repository.getMonthTotalByYearMonthAsync(year, month, total ->
                runOnUiThread(() -> txtMonthTotal.setText("$"+String.format("%d", total)))
        );

        repository.getCategoryTotalsByYearMonthAsync(year, month, list ->
                runOnUiThread(() -> renderPieChart(list))
        );

        repository.getRecordCountByYearMonthAsync(year, month, count ->
                runOnUiThread(() -> txtRecordCount.setText(String.valueOf(count)))
        );
    }

    private void renderPieChart(List<CategoryTotal> list) {
        if (list == null || list.isEmpty()) {
            pieChartCategory.clear();
            pieChartCategory.setCenterText("No Data");
            pieChartCategory.invalidate();

            txtTopCategory.setText("-");
            txtRecordCount.setText("0");

            layoutCategoryList.removeAllViews();
            return;
        }

        java.util.ArrayList<com.github.mikephil.charting.data.PieEntry> entries = new java.util.ArrayList<>();

        int totalAmount = 0;
        for (CategoryTotal item : list) {
            String label = (item.category == null || item.category.isEmpty()) ? "未分類" : item.category;
            entries.add(new com.github.mikephil.charting.data.PieEntry(item.total, label));
            totalAmount += item.total;
        }
        pieChartCategory.setCenterText("$" + String.format("%d", totalAmount));

        String topCategory = (list.get(0).category == null || list.get(0).category.isEmpty())
                ? "未分類"
                : list.get(0).category;

        txtTopCategory.setText(topCategory);

        com.github.mikephil.charting.data.PieDataSet dataSet =
                new com.github.mikephil.charting.data.PieDataSet(entries, "");

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(8f);

        ArrayList<Integer> colors = new ArrayList<>();
        for (CategoryTotal item : list) {
            String name = (item.category == null || item.category.isEmpty())
                    ? "未分類"
                    : item.category;

            colors.add(getCategoryColor(name));
        }

        dataSet.setColors(colors);

        com.github.mikephil.charting.data.PieData data =
                new com.github.mikephil.charting.data.PieData(dataSet);

        data.setValueTextSize(12f);
        data.setValueTextColor(0xFFFFFFFF);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter(pieChartCategory));

        pieChartCategory.setData(data);
        pieChartCategory.highlightValues(null);
        pieChartCategory.invalidate();

        pieChartCategory.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutCubic);
        renderCategoryList(list);
    }

    private void renderCategoryList(List<CategoryTotal> list) {

        layoutCategoryList.removeAllViews();

        int totalAmount = 0;
        for (CategoryTotal item : list) {
            totalAmount += item.total;
        }

        for (CategoryTotal item : list) {

            String name = (item.category == null || item.category.isEmpty())
                    ? "未分類"
                    : item.category;

            int amount = item.total;

            float percent = totalAmount == 0 ? 0 : (amount * 100f / totalAmount);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 18, 0, 18);

            TextView txtName = new TextView(this);
            int color = getCategoryColor(name);
            txtName.setText("● " + name);
            txtName.setTextSize(14);
            txtName.setTextColor(color);
            txtName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView txtAmount = new TextView(this);
            txtAmount.setText("$" + amount);
            txtAmount.setTextSize(14);
            txtAmount.setTextColor(0xFF4A4453);

            TextView txtPercent = new TextView(this);
            txtPercent.setText(String.format(java.util.Locale.getDefault(), "%.0f%%", percent));
            txtPercent.setTextSize(14);
            txtPercent.setTextColor(0xFF7A7283);
            txtPercent.setPadding(16, 0, 0, 0);

            row.addView(txtName);
            row.addView(txtAmount);
            row.addView(txtPercent);

            layoutCategoryList.addView(row);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
            ));
            divider.setBackgroundColor(0xFFD8D2E0);

            layoutCategoryList.addView(divider);
        }
    }
}