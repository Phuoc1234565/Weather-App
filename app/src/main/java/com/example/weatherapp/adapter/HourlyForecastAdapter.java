package com.example.weatherapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.model.HourlyForecast;
import java.util.List;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    private List<HourlyForecast> hourlyList;

    public HourlyForecastAdapter(List<HourlyForecast> hourlyList) {
        this.hourlyList = hourlyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_hourly_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyForecast item = hourlyList.get(position);
        holder.tvTime.setText(item.getTime());
        boolean fahrenheit = holder.itemView.getContext()
            .getSharedPreferences("weather_app", android.content.Context.MODE_PRIVATE)
            .getBoolean("fahrenheit", false);
        double temperature = item.getTemperature();
        if (fahrenheit) temperature = temperature * 9.0 / 5.0 + 32.0;
        holder.tvTemp.setText(String.format(java.util.Locale.getDefault(), "%d°", (int) Math.round(temperature)));
        setWeatherIcon(holder.ivWeatherIcon, item.getCondition(), item.getWeatherIcon());
    }

    private void setWeatherIcon(ImageView iv, String condition, String iconCode) {
        if ("night".equals(iconCode)) {
            iv.setImageResource(R.drawable.ic_weather_moon);
            return;
        }
        if (condition == null) {
            iv.setImageResource(R.drawable.ic_weather_sun);
            return;
        }
        String lower = condition.toLowerCase();
        if (lower.contains("mưa") || lower.contains("rain") || lower.contains("giông") || lower.contains("bão")) {
            iv.setImageResource(R.drawable.ic_humidity);
        } else if (lower.contains("mây") || lower.contains("cloud") || lower.contains("sương")
            || lower.contains("tuyết") || lower.contains("snow") || lower.contains("fog")) {
            iv.setImageResource(R.drawable.ic_weather_cloud);
        } else {
            iv.setImageResource(R.drawable.ic_weather_sun);
        }
    }

    @Override
    public int getItemCount() {
        return hourlyList != null ? hourlyList.size() : 0;
    }

    public void updateData(List<HourlyForecast> newList) {
        this.hourlyList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvTemp;
        ImageView ivWeatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
        }
    }
}
