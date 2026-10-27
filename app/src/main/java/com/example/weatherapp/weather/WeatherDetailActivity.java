package com.example.weatherapp.weather;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.adapter.DailyForecastAdapter;
import com.example.weatherapp.adapter.HourlyForecastAdapter;
import com.example.weatherapp.model.WeatherData;
import java.util.ArrayList;

public class WeatherDetailActivity extends AppCompatActivity {
    private ImageButton btnBack, btnShare;
    private TextView tvCityName, tvTemperature, tvCondition, tvFeelsLike;
    private TextView tvHumidity, tvWind, tvUV;
    private RecyclerView rvHourlyForecast, rvDailyForecast;
    private WeatherData currentWeather;
    private boolean useFahrenheit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        useFahrenheit = getSharedPreferences("weather_app", MODE_PRIVATE)
            .getBoolean("fahrenheit", false);
        initViews();
        loadWeather();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvCondition = findViewById(R.id.tvCondition);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        tvUV = findViewById(R.id.tvUV);
        rvHourlyForecast = findViewById(R.id.rvHourlyForecast);
        rvDailyForecast = findViewById(R.id.rvDailyForecast);

        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareWeather());
        rvHourlyForecast.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHourlyForecast.setAdapter(new HourlyForecastAdapter(new ArrayList<>()));
        rvDailyForecast.setLayoutManager(new LinearLayoutManager(this));
        rvDailyForecast.setAdapter(new DailyForecastAdapter(new ArrayList<>()));
    }

    private void loadWeather() {
        String city = getIntent().getStringExtra("cityName");
        if (city == null || city.trim().isEmpty()) {
            city = getSharedPreferences("weather_app", MODE_PRIVATE)
                .getString("selected_city", "Hồ Chí Minh");
        }
        tvCityName.setText(city);
        tvTemperature.setText("—");
        tvCondition.setText("Đang tải thời tiết...");
        tvFeelsLike.setText("");
        tvHumidity.setText("—");
        tvWind.setText("—");
        tvUV.setText("—");

        OpenWeatherService.fetchByCity(city, new OpenWeatherService.Callback() {
            @Override public void onSuccess(OpenWeatherService.ForecastBundle bundle) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    currentWeather = bundle.current;
                    showWeather(bundle.current);
                    ((HourlyForecastAdapter) rvHourlyForecast.getAdapter()).updateData(bundle.hourly);
                    ((DailyForecastAdapter) rvDailyForecast.getAdapter()).updateData(bundle.daily);
                });
            }

            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    tvCondition.setText("Không lấy được thời tiết");
                    tvFeelsLike.setText("Kiểm tra API key hoặc kết nối mạng.");
                    Toast.makeText(WeatherDetailActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showWeather(WeatherData data) {
        tvCityName.setText(data.getCityName());
        tvTemperature.setText(formatTemperature(data.getTemperature()));
        tvCondition.setText(data.getCondition());
        tvFeelsLike.setText("Cảm giác như " + formatTemperature(data.getFeelsLike()));
        tvHumidity.setText(data.getHumidity() + "%");
        tvWind.setText(String.format("%.0f km/h", data.getWindSpeed()));
        tvUV.setText(data.getCloudCover() + "%");
    }

    private String formatTemperature(double celsius) {
        int value = (int) Math.round(useFahrenheit ? celsius * 9.0 / 5.0 + 32.0 : celsius);
        return value + "°";
    }

    private void shareWeather() {
        if (currentWeather == null) {
            Toast.makeText(this, "Thời tiết chưa tải xong", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = currentWeather.getCityName() + ": "
            + formatTemperature(currentWeather.getTemperature()) + ", "
            + currentWeather.getCondition() + " - WeatherApp";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ thời tiết"));
    }
}
