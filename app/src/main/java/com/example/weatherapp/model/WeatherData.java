package com.example.weatherapp.model;

public class WeatherData {
    private String cityName;
    private double temperature;
    private double feelsLike;
    private double tempLow;
    private double tempHigh;
    private String condition;
    private int humidity;
    private double windSpeed;
    private int uvIndex;
    private int cloudCover;
    private long timestamp;
    private String weatherIcon; // icon code

    public WeatherData() {}

    public WeatherData(String cityName, double temperature, double feelsLike,
                       double tempLow, double tempHigh, String condition,
                       int humidity, double windSpeed, int uvIndex) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.tempLow = tempLow;
        this.tempHigh = tempHigh;
        this.condition = condition;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.uvIndex = uvIndex;
        this.timestamp = System.currentTimeMillis();
    }

    // All getters and setters
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }
    public double getTempLow() { return tempLow; }
    public void setTempLow(double tempLow) { this.tempLow = tempLow; }
    public double getTempHigh() { return tempHigh; }
    public void setTempHigh(double tempHigh) { this.tempHigh = tempHigh; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    public int getUvIndex() { return uvIndex; }
    public void setUvIndex(int uvIndex) { this.uvIndex = uvIndex; }
    public int getCloudCover() { return cloudCover; }
    public void setCloudCover(int cloudCover) { this.cloudCover = cloudCover; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getWeatherIcon() { return weatherIcon; }
    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }
}
