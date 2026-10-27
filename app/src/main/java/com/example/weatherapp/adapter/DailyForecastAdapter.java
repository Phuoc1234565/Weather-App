package com.example.weatherapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.model.DailyForecast;
import java.util.List;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ViewHolder> {

    private List<DailyForecast> dailyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DailyForecast forecast);
    }

    public DailyForecastAdapter(List<DailyForecast> dailyList) {
        this.dailyList = dailyList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_daily_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyForecast item = dailyList.get(position);
        holder.tvDayName.setText(item.getDayName());
        boolean fahrenheit = holder.itemView.getContext()
            .getSharedPreferences("weather_app", android.content.Context.MODE_PRIVATE)
            .getBoolean("fahrenheit", false);
        int low = toDisplayedTemperature(item.getTempLow(), fahrenheit);
        int high = toDisplayedTemperature(item.getTempHigh(), fahrenheit);
        holder.tvLowHigh.setText(String.format(java.util.Locale.getDefault(), "%d° - %d°", low, high));
        setWeatherIcon(holder.ivWeatherIcon, item.getCondition(), item.getWeatherIcon());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
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

    private int toDisplayedTemperature(double celsius, boolean fahrenheit) {
        return (int) Math.round(fahrenheit ? celsius * 9.0 / 5.0 + 32.0 : celsius);
    }

    @Override
    public int getItemCount() {
        return dailyList != null ? dailyList.size() : 0;
    }

    public void updateData(List<DailyForecast> newList) {
        this.dailyList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvLowHigh;
        ImageView ivWeatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvLowHigh = itemView.findViewById(R.id.tvLowHigh);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
        }
    }
}
