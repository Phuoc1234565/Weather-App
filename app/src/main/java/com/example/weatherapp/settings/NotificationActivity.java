package com.example.weatherapp.settings;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.model.NotificationSettings;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private SwitchMaterial switchRain, switchHot, switchStorm, switchDaily;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btnBack);
        switchRain = findViewById(R.id.switchRain);
        switchHot = findViewById(R.id.switchHot);
        switchStorm = findViewById(R.id.switchStorm);
        switchDaily = findViewById(R.id.switchDaily);

        btnBack.setOnClickListener(v -> finish());

        loadSettings();
        setupListeners();
    }

    private void loadSettings() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("notification_settings").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        NotificationSettings settings = doc.toObject(NotificationSettings.class);
                        if (settings != null) {
                            switchRain.setChecked(settings.isRainAlert());
                            switchHot.setChecked(settings.isHeatAlert());
                            switchStorm.setChecked(settings.isStormAlert());
                            switchDaily.setChecked(settings.isDailyReminder());
                        }
                    }
                });
        }
    }

    private void setupListeners() {
        switchRain.setOnCheckedChangeListener((v, isChecked) -> saveSetting("rainAlert", isChecked));
        switchHot.setOnCheckedChangeListener((v, isChecked) -> saveSetting("heatAlert", isChecked));
        switchStorm.setOnCheckedChangeListener((v, isChecked) -> saveSetting("stormAlert", isChecked));
        switchDaily.setOnCheckedChangeListener((v, isChecked) -> saveSetting("dailyReminder", isChecked));
    }

    private void saveSetting(String key, boolean val) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("notification_settings").document(user.getUid())
                .update(key, val)
                .addOnFailureListener(e -> {
                    NotificationSettings newSettings = new NotificationSettings(user.getUid());
                    db.collection("notification_settings").document(user.getUid()).set(newSettings);
                });
        }
        Toast.makeText(this, "Đã cập nhật cài đặt", Toast.LENGTH_SHORT).show();
    }
}
