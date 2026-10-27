package com.example.weatherapp.model;

public class Location {
    private String id;
    private String cityName;
    private String province;
    private double latitude;
    private double longitude;
    private boolean isFavorite;
    private long addedAt;

    public Location() {}

    public Location(String cityName, String province, double latitude, double longitude) {
        this.cityName = cityName;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}
