package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.adapter.DailyForecastAdapter;
import com.example.weatherapp.adapter.HourlyForecastAdapter;
import com.example.weatherapp.location.FavoriteLocationsActivity;
import com.example.weatherapp.location.SearchLocationActivity;
import com.example.weatherapp.model.WeatherData;
import com.example.weatherapp.settings.SettingsActivity;
import com.example.weatherapp.settings.NotificationActivity;
import com.example.weatherapp.weather.ForecastActivity;
import com.example.weatherapp.weather.WeatherDetailActivity;
import com.example.weatherapp.weather.WeatherMapActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvCityName, tvTemperature, tvWeatherCondition, tvFeelsLike;
    private TextView tvLow, tvHigh, tvHumidity, tvWind, tvUV;
    private View tvSeeHourlyDetail, tvSeeForecast;
    private TextView btnCelsius, btnFahrenheit;
    private ImageButton btnSearch, btnSettings;
    private RecyclerView rvHourlyForecast, rvDailyForecast;
    private BottomNavigationView bottomNavigation;
    private boolean useFahrenheit;

    private HourlyForecastAdapter hourlyAdapter;
    private DailyForecastAdapter dailyAdapter;

    private String requestedCity;
    private WeatherData currentWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        useFahrenheit = getSharedPreferences("weather_app", MODE_PRIVATE)
            .getBoolean("fahrenheit", false);
        initViews();
        setupRecyclerViews();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        useFahrenheit = getSharedPreferences("weather_app", MODE_PRIVATE)
            .getBoolean("fahrenheit", false);
        if (btnCelsius != null) updateTemperatureUnitButtons();
        // Child screens are pushed above Home; restore the Home tab after returning.
        // Temporarily remove listener to avoid re-triggering navigation.
        if (bottomNavigation != null && bottomNavigation.getSelectedItemId() != R.id.nav_home) {
            bottomNavigation.setOnItemSelectedListener(null);
            bottomNavigation.setSelectedItemId(R.id.nav_home);
            setupBottomNavigation();
        }
        loadWeatherData();
    }

    private void initViews() {
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherCondition = findViewById(R.id.tvWeatherCondition);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvLow = findViewById(R.id.tvLow);
        tvHigh = findViewById(R.id.tvHigh);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        tvUV = findViewById(R.id.tvUV);
        tvSeeHourlyDetail = findViewById(R.id.tvSeeHourlyDetail);
        tvSeeForecast = findViewById(R.id.tvSeeForecast);
        rvHourlyForecast = findViewById(R.id.rvHourlyForecast);
        rvDailyForecast = findViewById(R.id.rvDailyForecast);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnSearch = findViewById(R.id.btnSearch);
        btnSettings = findViewById(R.id.btnSettings);
        btnCelsius = findViewById(R.id.btnCelsius);
        btnFahrenheit = findViewById(R.id.btnFahrenheit);

        btnSearch.setOnClickListener(v ->
            startActivity(new Intent(this, SearchLocationActivity.class)));
        tvCityName.setOnClickListener(v ->
            startActivity(new Intent(this, SearchLocationActivity.class)));

        btnSettings.setOnClickListener(v ->
            startActivity(new Intent(this, SettingsActivity.class)));

        tvSeeHourlyDetail.setOnClickListener(v ->
            startActivity(new Intent(this, WeatherDetailActivity.class)));

        tvSeeForecast.setOnClickListener(v ->
            startActivity(new Intent(this, ForecastActivity.class)));
        findViewById(R.id.btnNotifications).setOnClickListener(v ->
            startActivity(new Intent(this, NotificationActivity.class)));
        findViewById(R.id.btnOpenMap).setOnClickListener(v ->
            startActivity(new Intent(this, WeatherMapActivity.class)));
        findViewById(R.id.btnOpenFavorites).setOnClickListener(v ->
            startActivity(new Intent(this, FavoriteLocationsActivity.class)));
        findViewById(R.id.btnDismissAlert).setOnClickListener(v ->
            findViewById(R.id.alertCard).setVisibility(View.GONE));
        btnCelsius.setOnClickListener(v -> setTemperatureUnit(false));
        btnFahrenheit.setOnClickListener(v -> setTemperatureUnit(true));
        updateTemperatureUnitButtons();
    }

    private void setupRecyclerViews() {
        // Hourly Forecast
        hourlyAdapter = new HourlyForecastAdapter(new ArrayList<>());
        rvHourlyForecast.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHourlyForecast.setAdapter(hourlyAdapter);

        // Daily Forecast
        dailyAdapter = new DailyForecastAdapter(new ArrayList<>());
        rvDailyForecast.setLayoutManager(new LinearLayoutManager(this));
        rvDailyForecast.setAdapter(dailyAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_forecast) {
                startActivity(new Intent(this, ForecastActivity.class));
                return true;
            } else if (itemId == R.id.nav_map) {
                startActivity(new Intent(this, WeatherMapActivity.class));
                return true;
            } else if (itemId == R.id.nav_locations) {
                startActivity(new Intent(this, FavoriteLocationsActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadWeatherData() {
        // Keep the selected city across screens and app launches.
        String city = getSharedPreferences("weather_app", MODE_PRIVATE)
            .getString("selected_city", "Hồ Chí Minh");
        if (city.equals(requestedCity) && currentWeatherData != null) return;
        requestedCity = city;
        currentWeatherData = null;
        showLoadingWeather(city);
        com.example.weatherapp.weather.OpenWeatherService.fetchByCity(city,
            new com.example.weatherapp.weather.OpenWeatherService.Callback() {
                @Override public void onSuccess(com.example.weatherapp.weather.OpenWeatherService.ForecastBundle bundle) {
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        String selected = getSharedPreferences("weather_app", MODE_PRIVATE)
                            .getString("selected_city", "Hồ Chí Minh");
                        if (!city.equals(selected)) return;
                        updateUI(bundle.current);
                        hourlyAdapter.updateData(bundle.hourly);
                        dailyAdapter.updateData(bundle.daily);
                    });
                }

                @Override public void onError(String message) {
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        requestedCity = null;
                        showWeatherUnavailable(city, message);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }
            });
    }

    private void updateUI(WeatherData data) {
        currentWeatherData = data;
        tvCityName.setText(data.getCityName());
        tvTemperature.setText(formatTemperature(data.getTemperature()));
        tvWeatherCondition.setText(data.getCondition());
        tvFeelsLike.setText(String.format("Cảm giác như %s", formatTemperature(data.getFeelsLike())));
        tvLow.setText(String.format("↓ %s", formatTemperature(data.getTempLow())));
        tvHigh.setText(String.format("↑ %s", formatTemperature(data.getTempHigh())));
        tvHumidity.setText(data.getHumidity() + "%");
        tvWind.setText(String.format("%.0f km/h", data.getWindSpeed()));
        tvUV.setText(data.getCloudCover() + "%");
        int weatherIcon = "rainy".equals(data.getWeatherIcon()) ? R.drawable.ic_humidity
            : "cloudy".equals(data.getWeatherIcon()) ? R.drawable.ic_weather_cloud
            : "night".equals(data.getWeatherIcon()) ? R.drawable.ic_weather_moon
            : R.drawable.ic_weather_sun;
        ((android.widget.ImageView) findViewById(R.id.ivWeatherIcon)).setImageResource(weatherIcon);
    }

    private void showLoadingWeather(String city) {
        tvCityName.setText(city);
        tvTemperature.setText("—");
        tvWeatherCondition.setText("Đang tải thời tiết...");
        tvFeelsLike.setText("");
        tvLow.setText("↓ —");
        tvHigh.setText("↑ —");
        tvHumidity.setText("—");
        tvWind.setText("—");
        tvUV.setText("—");
    }

    private void showWeatherUnavailable(String city, String message) {
        tvCityName.setText(city);
        tvTemperature.setText("—");
        tvWeatherCondition.setText("Không lấy được thời tiết");
        tvFeelsLike.setText("Kiểm tra API key hoặc kết nối mạng.");
        tvLow.setText("↓ —");
        tvHigh.setText("↑ —");
        tvHumidity.setText("—");
        tvWind.setText("—");
        tvUV.setText("—");
    }

    private String formatTemperature(double celsius) {
        int value = (int) Math.round(useFahrenheit ? celsius * 9.0 / 5.0 + 32.0 : celsius);
        return value + "°";
    }

    private void setTemperatureUnit(boolean fahrenheit) {
        if (useFahrenheit == fahrenheit) return;
        useFahrenheit = fahrenheit;
        getSharedPreferences("weather_app", MODE_PRIVATE).edit()
            .putBoolean("fahrenheit", fahrenheit).apply();
        updateTemperatureUnitButtons();
        if (currentWeatherData != null) updateUI(currentWeatherData);
    }

    private void updateTemperatureUnitButtons() {
        btnCelsius.setBackgroundResource(useFahrenheit ? R.drawable.bg_tag_light : R.drawable.bg_button_blue);
        btnFahrenheit.setBackgroundResource(useFahrenheit ? R.drawable.bg_button_blue : R.drawable.bg_tag_light);
        btnCelsius.setTextColor(getColor(useFahrenheit ? R.color.text_body : R.color.white));
        btnFahrenheit.setTextColor(getColor(useFahrenheit ? R.color.white : R.color.text_body));
    }

}
