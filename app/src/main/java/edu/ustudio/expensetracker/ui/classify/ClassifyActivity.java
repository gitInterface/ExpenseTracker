package edu.ustudio.expensetracker.ui.classify;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.ustudio.expensetracker.MainActivity;
import edu.ustudio.expensetracker.R;
import edu.ustudio.expensetracker.ui.settings.SettingsActivity;
import edu.ustudio.expensetracker.ui.stats.StatsActivity;

public class ClassifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_classify);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setSelectedItemId(R.id.nav_classify);

        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_stats) {
                startActivity(new Intent(this, StatsActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }

            if (item.getItemId() == R.id.nav_classify) {
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
    }
}