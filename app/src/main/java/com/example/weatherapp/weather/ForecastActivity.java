package com.example.weatherapp.weather;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.adapter.DailyForecastAdapter;
import java.util.ArrayList;

public class ForecastActivity extends AppCompatActivity {
    private RecyclerView rvSevenDayForecast;
    private DailyForecastAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        ImageButton btnBack = findViewById(R.id.btnBack);
        rvSevenDayForecast = findViewById(R.id.rvSevenDayForecast);
        btnBack.setOnClickListener(v -> finish());

        adapter = new DailyForecastAdapter(new ArrayList<>());
        rvSevenDayForecast.setLayoutManager(new LinearLayoutManager(this));
        rvSevenDayForecast.setAdapter(adapter);
        loadForecast();
    }

    private void loadForecast() {
        String city = getSharedPreferences("weather_app", MODE_PRIVATE)
            .getString("selected_city", "Hồ Chí Minh");
        OpenWeatherService.fetchByCity(city, new OpenWeatherService.Callback() {
            @Override public void onSuccess(OpenWeatherService.ForecastBundle bundle) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    adapter.updateData(bundle.daily);
                });
            }

            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    Toast.makeText(ForecastActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
