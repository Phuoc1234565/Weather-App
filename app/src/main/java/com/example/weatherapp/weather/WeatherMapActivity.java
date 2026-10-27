package com.example.weatherapp.weather;

import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.location.SearchLocationActivity;
import android.view.inputmethod.EditorInfo;

public class WeatherMapActivity extends AppCompatActivity {

    private ImageButton btnMyLocation;
    private EditText etSearchMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_map);

        btnMyLocation = findViewById(R.id.btnMyLocation);
        etSearchMap = findViewById(R.id.etSearchMap);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        etSearchMap.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                startActivity(new Intent(this, SearchLocationActivity.class)
                    .putExtra("query", etSearchMap.getText().toString().trim()));
                return true;
            }
            return false;
        });

        btnMyLocation.setOnClickListener(v -> {
            Toast.makeText(this, "Đang định vị vị trí hiện tại...", Toast.LENGTH_SHORT).show();
        });
    }
}
