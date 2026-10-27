package com.example.weatherapp.model;

public class NotificationSettings {
    private String userId;
    private boolean rainAlert;
    private boolean heatAlert;
    private boolean stormAlert;
    private boolean dailyReminder;
    private String dailyReminderTime; // HH:mm

    public NotificationSettings() {}

    public NotificationSettings(String userId) {
        this.userId = userId;
        this.rainAlert = true;
        this.heatAlert = true;
        this.stormAlert = false;
        this.dailyReminder = false;
        this.dailyReminderTime = "07:00";
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public boolean isRainAlert() { return rainAlert; }
    public void setRainAlert(boolean rainAlert) { this.rainAlert = rainAlert; }
    public boolean isHeatAlert() { return heatAlert; }
    public void setHeatAlert(boolean heatAlert) { this.heatAlert = heatAlert; }
    public boolean isStormAlert() { return stormAlert; }
    public void setStormAlert(boolean stormAlert) { this.stormAlert = stormAlert; }
    public boolean isDailyReminder() { return dailyReminder; }
    public void setDailyReminder(boolean dailyReminder) { this.dailyReminder = dailyReminder; }
    public String getDailyReminderTime() { return dailyReminderTime; }
    public void setDailyReminderTime(String dailyReminderTime) { this.dailyReminderTime = dailyReminderTime; }
}
