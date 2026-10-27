package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean navigated = false;
    private final ExecutorService startupExecutor = Executors.newSingleThreadExecutor();
    private final Runnable continueToApp = this::openNextScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check login status and navigate after delay
        handler.postDelayed(continueToApp, SPLASH_DELAY);

        // Start button click (optional, goes straight through)
        findViewById(R.id.btnStart).setOnClickListener(v -> openNextScreen());
    }

    private void openNextScreen() {
        if (navigated || isFinishing()) return;
        navigated = true;
        handler.removeCallbacks(continueToApp);
        startupExecutor.execute(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Class<?> destination = currentUser != null ? MainActivity.class : LoginActivity.class;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isFinishing() || isDestroyed()) return;
                startActivity(new Intent(this, destination));
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(continueToApp);
        startupExecutor.shutdownNow();
        super.onDestroy();
    }
}
