package com.example.weatherapp.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.adapter.LocationSuggestionAdapter;
import com.example.weatherapp.adapter.SearchHistoryAdapter;
import com.example.weatherapp.model.Location;
import com.example.weatherapp.weather.WeatherDetailActivity;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchLocationActivity extends AppCompatActivity {
    private static final String PREFS = "weather_app";
    private static final String HISTORY_KEY = "search_history";
    private EditText etSearch;
    private RecyclerView rvSearchResults, rvSearchHistory;
    private LinearLayout llSearchHistory, llPopularTags;
    private View noResults;
    private LocationSuggestionAdapter searchAdapter;
    private SearchHistoryAdapter historyAdapter;
    private final List<Location> allLocations = new ArrayList<>();
    private final List<String> historyList = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        etSearch = findViewById(R.id.etSearch);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        rvSearchHistory = findViewById(R.id.rvSearchHistory);
        llSearchHistory = findViewById(R.id.llSearchHistory);
        llPopularTags = findViewById(R.id.llPopularTags);
        noResults = findViewById(R.id.tvNoSearchResults);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        fillLocations();
        loadHistory();
        searchAdapter = new LocationSuggestionAdapter(new ArrayList<>());
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(searchAdapter);
        historyAdapter = new SearchHistoryAdapter(historyList);
        rvSearchHistory.setLayoutManager(new LinearLayoutManager(this));
        rvSearchHistory.setAdapter(historyAdapter);
        searchAdapter.setOnItemClickListener(location -> openDetail(location.getCityName()));
        historyAdapter.setOnItemClickListener(this::openDetail);
        historyAdapter.setOnRemoveClickListener((query, position) -> {
            if (position >= 0 && position < historyList.size()) {
                historyList.remove(position);
                historyAdapter.notifyItemRemoved(position);
                saveHistory();
                updateHistoryVisibility();
            }
        });
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH && searchAdapter.getItemCount() > 0) {
                for (Location location : allLocations) {
                    if (normalize(location.getCityName()).contains(normalize(etSearch.getText().toString())) ||
                        normalize(location.getProvince()).contains(normalize(etSearch.getText().toString()))) {
                        openDetail(location.getCityName());
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                        return true;
                    }
                }
            }
            return false;
        });
        bindPopularTags(llPopularTags);
        bindPopularTags(findViewById(R.id.llPopularTagsSecondary));
        String query = getIntent().getStringExtra("query");
        if (query != null && !query.trim().isEmpty()) {
            etSearch.setText(query);
            etSearch.setSelection(etSearch.length());
        } else filter("");
    }

    private void fillLocations() {
        allLocations.add(new Location("Hà Nội", "Hà Nội", 21.0285, 105.8542));
        allLocations.add(new Location("Hồ Chí Minh", "TP. Hồ Chí Minh", 10.8231, 106.6297));
        allLocations.add(new Location("Đà Nẵng", "Đà Nẵng", 16.0544, 108.2022));
        allLocations.add(new Location("Hải Phòng", "Hải Phòng", 20.8449, 106.6881));
        allLocations.add(new Location("Cần Thơ", "Cần Thơ", 10.0452, 105.7469));
        allLocations.add(new Location("Đà Lạt", "Lâm Đồng", 11.9404, 108.4583));
        allLocations.add(new Location("Nha Trang", "Khánh Hòa", 12.2388, 109.1967));
        allLocations.add(new Location("Huế", "Thừa Thiên Huế", 16.4637, 107.5909));
        allLocations.add(new Location("Vũng Tàu", "Bà Rịa - Vũng Tàu", 10.4114, 107.1362));
        allLocations.add(new Location("Phú Quốc", "Kiên Giang", 10.2899, 103.9840));
        allLocations.add(new Location("Hạ Long", "Quảng Ninh", 20.9500, 107.0730));
        allLocations.add(new Location("Sa Pa", "Lào Cai", 22.3364, 103.8438));
        allLocations.add(new Location("Quy Nhơn", "Bình Định", 13.7820, 109.2196));
        allLocations.add(new Location("Biên Hòa", "Đồng Nai", 10.9574, 106.8426));
        allLocations.add(new Location("Buôn Ma Thuột", "Đắk Lắk", 12.6667, 108.0500));
        allLocations.add(new Location("Vinh", "Nghệ An", 18.6796, 105.6813));
        allLocations.add(new Location("Pleiku", "Gia Lai", 13.9833, 108.0000));
    }

    private void bindPopularTags(LinearLayout row) {
        if (row == null) return;
        for (int i = 0; i < row.getChildCount(); i++) {
            View chip = row.getChildAt(i);
            if (chip instanceof TextView) chip.setOnClickListener(v -> {
                etSearch.setText(((TextView) v).getText());
                etSearch.setSelection(etSearch.length());
            });
        }
    }

    private void loadHistory() {
        String saved = getSharedPreferences(PREFS, MODE_PRIVATE).getString(HISTORY_KEY, "");
        if (!saved.trim().isEmpty()) {
            for (String city : saved.split("\\|")) if (!city.trim().isEmpty() && !historyList.contains(city)) historyList.add(city);
        }
    }

    private void saveHistory() {
        StringBuilder value = new StringBuilder();
        for (String city : historyList) { if (value.length() > 0) value.append('|'); value.append(city.replace("|", "")); }
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(HISTORY_KEY, value.toString()).apply();
    }

    private void updateHistoryVisibility() {
        llSearchHistory.setVisibility(View.VISIBLE);
        findViewById(R.id.tvNoSearchHistory).setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
        rvSearchHistory.setVisibility(historyList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void filter(String query) {
        if (query.trim().isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            noResults.setVisibility(View.GONE);
            llSearchHistory.setVisibility(View.VISIBLE);
            updateHistoryVisibility();
            return;
        }
        String normalized = normalize(query);
        List<Location> filtered = new ArrayList<>();
        for (Location loc : allLocations) {
            if (normalize(loc.getCityName()).contains(normalized) || normalize(loc.getProvince()).contains(normalized)) filtered.add(loc);
        }
        searchAdapter.updateData(filtered);
        rvSearchResults.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
        noResults.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        llSearchHistory.setVisibility(View.GONE);
    }

    private String normalize(String value) {
        String decomposed = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}+", "").replace('đ', 'd').replace('Đ', 'D').toLowerCase(Locale.ROOT).trim();
    }

    private void openDetail(String cityName) {
        historyList.remove(cityName);
        historyList.add(0, cityName);
        while (historyList.size() > 8) historyList.remove(historyList.size() - 1);
        saveHistory();
        historyAdapter.updateData(historyList);
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString("selected_city", cityName).apply();
        Intent intent = new Intent(this, WeatherDetailActivity.class);
        intent.putExtra("cityName", cityName);
        startActivity(intent);
    }
}
