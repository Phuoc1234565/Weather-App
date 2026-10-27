package com.example.weatherapp.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AlertDialog;
import com.example.weatherapp.R;
import com.example.weatherapp.auth.LoginActivity;
import com.example.weatherapp.location.SearchLocationActivity;
import com.example.weatherapp.model.User;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS = "weather_app";
    private TextView tvUserName, tvUserEmail, tvUnit, tvCurrentCity;
    private SwitchMaterial switchNotification, switchDarkMode;
    private LinearLayout llLanguage, llCurrentLocation, llAbout, llRate, llLogout, rowTemperatureUnit, rowNotifications;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean suppressThemeListener;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        initViews();
        loadLocalPreferences();
        loadUserProfile();
        setupListeners();
    }

    @Override protected void onResume() {
        super.onResume();
        if (tvCurrentCity != null) {
            tvCurrentCity.setText(getSharedPreferences(PREFS, MODE_PRIVATE)
                .getString("selected_city", "Hồ Chí Minh"));
            tvUnit.setText(getSharedPreferences(PREFS, MODE_PRIVATE)
                .getBoolean("fahrenheit", false) ? "°F" : "°C");
        }
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUnit = findViewById(R.id.tvUnit);
        tvCurrentCity = findViewById(R.id.tvCurrentCity);
        switchNotification = findViewById(R.id.switchNotification);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        llLanguage = findViewById(R.id.llLanguage);
        llCurrentLocation = findViewById(R.id.llCurrentLocation);
        llAbout = findViewById(R.id.llAbout);
        llRate = findViewById(R.id.llRate);
        llLogout = findViewById(R.id.llLogout);
        rowTemperatureUnit = findViewById(R.id.rowTemperatureUnit);
        rowNotifications = findViewById(R.id.rowNotifications);
    }

    private void loadLocalPreferences() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean dark = prefs.getBoolean("dark_mode", false);
        suppressThemeListener = true;
        switchDarkMode.setChecked(dark);
        switchNotification.setChecked(prefs.getBoolean("notifications_enabled", true));
        tvUnit.setText(prefs.getBoolean("fahrenheit", false) ? "°F" : "°C");
        tvCurrentCity.setText(prefs.getString("selected_city", "Hồ Chí Minh"));
        suppressThemeListener = false;
        // NOTE: Do NOT call AppCompatDelegate.setDefaultNightMode() here.
        // WeatherApp.onCreate() already applies the night mode on app startup.
        // Calling it here causes the Activity to recreate in an infinite loop → ANR.
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        if (currentUser.getEmail() != null) tvUserEmail.setText(currentUser.getEmail());
        db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;
            User user = snapshot.toObject(User.class);
            if (user == null) return;
            if (user.getFullName() != null && !user.getFullName().isEmpty()) tvUserName.setText(user.getFullName());
            if (user.getCurrentCity() != null && !user.getCurrentCity().isEmpty()) tvCurrentCity.setText(user.getCurrentCity());
            // Local preference is authoritative on this device; Firestore profile values are defaults only.
            if (!getSharedPreferences(PREFS, MODE_PRIVATE).contains("dark_mode")) {
                applyDarkMode(user.isDarkModeEnabled(), false);
            }
            if (!getSharedPreferences(PREFS, MODE_PRIVATE).contains("notifications_enabled")) {
                boolean enabled = user.isNotificationsEnabled();
                switchNotification.setChecked(enabled);
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean("notifications_enabled", enabled).apply();
            }
        });
    }

    private void setupListeners() {
        switchDarkMode.setOnCheckedChangeListener((button, enabled) -> {
            if (!suppressThemeListener) applyDarkMode(enabled, true);
        });
        switchNotification.setOnCheckedChangeListener((button, enabled) -> {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean("notifications_enabled", enabled).apply();
            updateUserPref("notificationsEnabled", enabled);
            Toast.makeText(this, enabled ? "Đã bật thông báo thời tiết" : "Đã tắt thông báo thời tiết", Toast.LENGTH_SHORT).show();
        });
        rowNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        rowTemperatureUnit.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Đơn vị nhiệt độ")
            .setSingleChoiceItems(new String[]{"Độ C (°C)", "Độ F (°F)"}, getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean("fahrenheit", false) ? 1 : 0,
                (dialog, which) -> {
                    boolean fahrenheit = which == 1;
                    getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean("fahrenheit", fahrenheit).apply();
                    tvUnit.setText(fahrenheit ? "°F" : "°C");
                    dialog.dismiss();
                }).setNegativeButton("Đóng", null).show());
        llCurrentLocation.setOnClickListener(v -> startActivity(new Intent(this, SearchLocationActivity.class)));
        llLanguage.setOnClickListener(v -> Toast.makeText(this, "Ứng dụng hiện hỗ trợ Tiếng Việt", Toast.LENGTH_SHORT).show());
        llAbout.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("WeatherApp")
            .setMessage("Dự báo thời tiết và theo dõi thời tiết tại các địa điểm bạn quan tâm.\n\nNguồn dữ liệu: OpenWeather (openweathermap.org).")
            .setPositiveButton("Đóng", null).show());
        llRate.setOnClickListener(v -> Toast.makeText(this, "Cảm ơn bạn đã sử dụng WeatherApp!", Toast.LENGTH_SHORT).show());
        llLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void applyDarkMode(boolean enabled, boolean persist) {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean("dark_mode", enabled).apply();
        if (persist) updateUserPref("darkModeEnabled", enabled);
        int targetMode = enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        // Only recreate if the night mode actually needs to change.
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode);
        }
        suppressThemeListener = true;
        switchDarkMode.setChecked(enabled);
        suppressThemeListener = false;
    }

    private void updateUserPref(String field, Object value) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) db.collection("users").document(currentUser.getUid()).update(field, value);
    }
}
