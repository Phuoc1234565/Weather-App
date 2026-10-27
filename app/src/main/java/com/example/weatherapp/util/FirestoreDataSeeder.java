package com.example.weatherapp.util;

import com.example.weatherapp.model.DailyForecast;
import com.example.weatherapp.model.HourlyForecast;
import com.example.weatherapp.model.Location;
import com.example.weatherapp.model.WeatherData;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tiện ích hỗ trợ seed / tạo dữ liệu mẫu lên Firestore
 * Giúp khởi tạo các collections chuẩn theo thiết kế app:
 * 1. "weather_data"
 * 2. "forecast_hourly"
 * 3. "forecast_daily"
 * 4. "locations"
 */
public class FirestoreDataSeeder {

    public static void seedSampleWeather(FirebaseFirestore db) {
        // 1. Collection "weather_data"
        WeatherData hcmWeather = new WeatherData(
                "Hồ Chí Minh",
                32.0,
                35.0,
                26.0,
                34.0,
                "Trời nắng",
                65,
                12.0,
                8
        );
        hcmWeather.setWeatherIcon("sunny");
        db.collection("weather_data").document("ho_chi_minh").set(hcmWeather);

        WeatherData haNoiWeather = new WeatherData(
                "Hà Nội",
                28.0,
                29.0,
                23.0,
                30.0,
                "Nhiều mây",
                70,
                10.0,
                5
        );
        haNoiWeather.setWeatherIcon("cloudy");
        db.collection("weather_data").document("ha_noi").set(haNoiWeather);

        WeatherData daNangWeather = new WeatherData(
                "Đà Nẵng",
                30.0,
                32.0,
                24.0,
                32.0,
                "Trời nắng",
                68,
                14.0,
                7
        );
        daNangWeather.setWeatherIcon("sunny");
        db.collection("weather_data").document("da_nang").set(daNangWeather);

        // 2. Collection "locations" (Danh sách địa điểm gợi ý & tìm kiếm)
        List<Location> defaultLocations = new ArrayList<>();
        defaultLocations.add(new Location("Hồ Chí Minh", "TP. Hồ Chí Minh", 10.8231, 106.6297));
        defaultLocations.add(new Location("Hà Nội", "Hà Nội", 21.0285, 105.8542));
        defaultLocations.add(new Location("Đà Nẵng", "Đà Nẵng", 16.0544, 108.2022));
        defaultLocations.add(new Location("Hải Phòng", "Hải Phòng", 20.8449, 106.6881));
        defaultLocations.add(new Location("Cần Thơ", "Cần Thơ", 10.0452, 105.7469));
        defaultLocations.add(new Location("Đà Lạt", "Lâm Đồng", 11.9404, 108.4583));
        defaultLocations.add(new Location("Nha Trang", "Khánh Hòa", 12.2388, 109.1967));
        defaultLocations.add(new Location("Huế", "Thừa Thiên Huế", 16.4637, 107.5909));

        for (Location loc : defaultLocations) {
            String docId = loc.getCityName().toLowerCase().replace(" ", "_").replace(".", "");
            db.collection("locations").document(docId).set(loc);
        }
    }
}
