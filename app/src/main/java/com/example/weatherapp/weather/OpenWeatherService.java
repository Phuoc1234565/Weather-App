package com.example.weatherapp.weather;

import com.example.weatherapp.BuildConfig;
import com.example.weatherapp.model.DailyForecast;
import com.example.weatherapp.model.HourlyForecast;
import com.example.weatherapp.model.WeatherData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

/** Loads current conditions and the free 5-day / 3-hour OpenWeather forecast. */
public final class OpenWeatherService {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);
    private static final String API_KEY = BuildConfig.OPENWEATHER_API_KEY.trim();

    private OpenWeatherService() {}

    public interface Callback {
        void onSuccess(ForecastBundle bundle);
        void onError(String message);
    }

    public static final class ForecastBundle {
        public final WeatherData current;
        public final List<HourlyForecast> hourly;
        public final List<DailyForecast> daily;

        ForecastBundle(WeatherData current, List<HourlyForecast> hourly, List<DailyForecast> daily) {
            this.current = current;
            this.hourly = hourly;
            this.daily = daily;
        }
    }

    public static void fetchByCity(String cityName, Callback callback) {
        EXECUTOR.execute(() -> {
            try {
                if (API_KEY.isEmpty() || API_KEY.startsWith("PASTE_")
                    || API_KEY.equals("YOUR_OPENWEATHER_API_KEY")) {
                    throw new IOException("Thiếu API key OpenWeather. Thêm OPENWEATHER_API_KEY vào secrets.properties rồi Sync Gradle.");
                }
                Coordinates coordinates = geocode(cityName);
                JSONObject currentJson = requestJson("https://api.openweathermap.org/data/2.5/weather"
                    + "?lat=" + coordinates.latitude
                    + "&lon=" + coordinates.longitude
                    + "&units=metric&lang=vi&appid=" + API_KEY);
                JSONObject forecastJson = requestJson("https://api.openweathermap.org/data/2.5/forecast"
                    + "?lat=" + coordinates.latitude
                    + "&lon=" + coordinates.longitude
                    + "&units=metric&lang=vi&appid=" + API_KEY);
                callback.onSuccess(parse(cityName, currentJson, forecastJson));
            } catch (Exception error) {
                String message = error.getMessage();
                callback.onError(message == null || message.trim().isEmpty()
                    ? "Không tải được dữ liệu thời tiết từ OpenWeather." : message);
            }
        });
    }

    private static Coordinates geocode(String cityName) throws Exception {
        JSONArray results = requestGeocoding(cityName);
        if (results.length() == 0) {
            String asciiName = Normalizer.normalize(cityName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replace('đ', 'd').replace('Đ', 'D')
                .replaceAll("(?i)^TP\\.\\s*", "").trim();
            if (asciiName.equalsIgnoreCase("Ho Chi Minh")) asciiName = "Ho Chi Minh City";
            results = requestGeocoding(asciiName);
        }
        if (results.length() == 0) throw new IOException("OpenWeather không tìm thấy địa điểm: " + cityName);
        JSONObject first = results.getJSONObject(0);
        return new Coordinates(first.getDouble("lat"), first.getDouble("lon"));
    }

    private static JSONArray requestGeocoding(String cityName) throws Exception {
        String query = URLEncoder.encode(cityName + ",VN", StandardCharsets.UTF_8.name());
        return requestArray("https://api.openweathermap.org/geo/1.0/direct?q="
            + query + "&limit=1&appid=" + API_KEY);
    }

    private static ForecastBundle parse(String requestedCity, JSONObject currentJson,
                                        JSONObject forecastJson) throws Exception {
        JSONObject main = currentJson.getJSONObject("main");
        JSONObject wind = currentJson.optJSONObject("wind");
        JSONObject clouds = currentJson.optJSONObject("clouds");
        JSONArray weatherArray = currentJson.optJSONArray("weather");
        JSONObject condition = weatherArray != null && weatherArray.length() > 0
            ? weatherArray.getJSONObject(0) : new JSONObject();

        int timezoneOffset = forecastJson.optJSONObject("city") == null
            ? 0 : forecastJson.getJSONObject("city").optInt("timezone", 0);
        JSONArray entries = forecastJson.getJSONArray("list");
        List<HourlyForecast> hourly = new ArrayList<>();
        Map<String, DayBucket> byDay = new LinkedHashMap<>();
        long now = System.currentTimeMillis() / 1000L;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ROOT);
        SimpleDateFormat hourFormat = new SimpleDateFormat("H", Locale.ROOT);
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE d/M", new Locale("vi", "VN"));
        TimeZone utc = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(utc);
        timeFormat.setTimeZone(utc);
        hourFormat.setTimeZone(utc);
        weekdayFormat.setTimeZone(utc);
        String localToday = dateFormat.format(new Date((now + timezoneOffset) * 1000L));

        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            long timestamp = entry.getLong("dt");
            long localMillis = (timestamp + timezoneOffset) * 1000L;
            Date localDate = new Date(localMillis);
            JSONObject itemMain = entry.getJSONObject("main");
            JSONObject itemWeather = firstWeather(entry);
            double temperature = itemMain.optDouble("temp", Double.NaN);
            String description = itemWeather.optString("description", "");
            String icon = iconCategory(itemWeather.optString("icon", ""), itemWeather.optInt("id", 800));

            if (timestamp >= now && hourly.size() < 6) {
                hourly.add(new HourlyForecast(timeFormat.format(localDate), temperature, description, icon));
            }

            String key = dateFormat.format(localDate);
            DayBucket bucket = byDay.get(key);
            if (bucket == null) {
                bucket = new DayBucket(key, temperature, itemMain.optDouble("temp_max", temperature),
                    itemMain.optDouble("temp_min", temperature), description, icon);
                byDay.put(key, bucket);
            } else {
                bucket.low = Math.min(bucket.low, itemMain.optDouble("temp_min", temperature));
                bucket.high = Math.max(bucket.high, itemMain.optDouble("temp_max", temperature));
            }
            int hour = Integer.parseInt(hourFormat.format(localDate));
            if (Math.abs(hour - 12) < bucket.conditionDistance) {
                bucket.conditionDistance = Math.abs(hour - 12);
                bucket.condition = description;
                bucket.icon = icon;
            }
        }

        List<DailyForecast> daily = new ArrayList<>();
        String tomorrow = dateFormat.format(new Date((now + timezoneOffset + 86400L) * 1000L));
        for (DayBucket bucket : byDay.values()) {
            if (daily.size() >= 5) break;
            Date date = dateFormat.parse(bucket.date);
            String dayName = bucket.date.equals(localToday) ? "Hôm nay"
                : bucket.date.equals(tomorrow) ? "Ngày mai" : weekdayFormat.format(date);
            daily.add(new DailyForecast(dayName, bucket.low, bucket.high, bucket.condition, bucket.icon));
        }

        String displayName = requestedCity == null || requestedCity.trim().isEmpty()
            ? currentJson.optString("name", "") : requestedCity;
        WeatherData current = new WeatherData(displayName,
            main.optDouble("temp", 0),
            main.optDouble("feels_like", main.optDouble("temp", 0)),
            main.optDouble("temp_min", main.optDouble("temp", 0)),
            main.optDouble("temp_max", main.optDouble("temp", 0)),
            condition.optString("description", "Không rõ"),
            main.optInt("humidity", 0),
            wind == null ? 0 : wind.optDouble("speed", 0) * 3.6,
            0);
        if (!daily.isEmpty()) {
            current.setTempLow(daily.get(0).getTempLow());
            current.setTempHigh(daily.get(0).getTempHigh());
        }
        current.setWeatherIcon(iconCategory(condition.optString("icon", ""), condition.optInt("id", 800)));
        current.setCloudCover(clouds == null ? 0 : clouds.optInt("all", 0));
        current.setTimestamp(currentJson.optLong("dt", now) * 1000L);
        return new ForecastBundle(current, hourly, daily);
    }

    private static JSONObject firstWeather(JSONObject item) throws Exception {
        JSONArray arr = item.optJSONArray("weather");
        return arr != null && arr.length() > 0 ? arr.getJSONObject(0) : new JSONObject();
    }

    private static String iconCategory(String apiIcon, int weatherId) {
        if (weatherId >= 200 && weatherId < 600) return "rainy";
        if (weatherId >= 600 && weatherId < 800) return "cloudy";
        if (weatherId >= 700 && weatherId < 800) return "cloudy";
        if (weatherId >= 801 && weatherId <= 804) return "cloudy";
        return apiIcon.endsWith("n") ? "night" : "sunny";
    }

    private static JSONObject requestJson(String address) throws Exception {
        String body = request(address);
        return new JSONObject(body);
    }

    private static JSONArray requestArray(String address) throws Exception {
        String body = request(address);
        return new JSONArray(body);
    }

    private static String request(String address) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("Accept", "application/json");
        try {
            int status = connection.getResponseCode();
            InputStream stream = status >= 200 && status < 300
                ? connection.getInputStream() : connection.getErrorStream();
            StringBuilder body = new StringBuilder();
            if (stream != null) {
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) body.append(line);
                }
            }
            if (status < 200 || status >= 300) {
                String detail = "";
                try { detail = new JSONObject(body.toString()).optString("message", ""); }
                catch (Exception ignored) { }
                throw new IOException("OpenWeather HTTP " + status
                    + (detail.isEmpty() ? "" : ": " + detail));
            }
            return body.toString();
        } finally {
            connection.disconnect();
        }
    }

    private static final class Coordinates {
        final double latitude;
        final double longitude;
        Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private static final class DayBucket {
        final String date;
        double low;
        double high;
        String condition;
        String icon;
        int conditionDistance = Integer.MAX_VALUE;
        DayBucket(String date, double low, double high, double ignoredLow,
                  String condition, String icon) {
            this.date = date;
            this.low = Math.min(low, ignoredLow);
            this.high = Math.max(high, ignoredLow);
            this.condition = condition;
            this.icon = icon;
        }
    }
}
