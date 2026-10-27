package com.example.weatherapp.model;

public class HourlyForecast {
    private String time;
    private double temperature;
    private String condition;
    private String weatherIcon;

    public HourlyForecast() {}

    public HourlyForecast(String time, double temperature, String condition, String weatherIcon) {
        this.time = time;
        this.temperature = temperature;
        this.condition = condition;
        this.weatherIcon = weatherIcon;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getWeatherIcon() { return weatherIcon; }
    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }
}
