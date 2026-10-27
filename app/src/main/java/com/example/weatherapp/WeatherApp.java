package com.example.weatherapp;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class WeatherApp extends Application {
    @Override public void onCreate() {
        super.onCreate();
        boolean dark = getSharedPreferences("weather_app", MODE_PRIVATE).getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
