package com.example.weatherapp.model;

public class DailyForecast {
    private String dayName;
    private double tempLow;
    private double tempHigh;
    private String condition;
    private String weatherIcon;

    public DailyForecast() {}

    public DailyForecast(String dayName, double tempLow, double tempHigh, String condition, String weatherIcon) {
        this.dayName = dayName;
        this.tempLow = tempLow;
        this.tempHigh = tempHigh;
        this.condition = condition;
        this.weatherIcon = weatherIcon;
    }

    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }
    public double getTempLow() { return tempLow; }
    public void setTempLow(double tempLow) { this.tempLow = tempLow; }
    public double getTempHigh() { return tempHigh; }
    public void setTempHigh(double tempHigh) { this.tempHigh = tempHigh; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getWeatherIcon() { return weatherIcon; }
    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }
}
