package com.example.weatherapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.model.Location;
import java.util.List;

public class FavoriteLocationAdapter extends RecyclerView.Adapter<FavoriteLocationAdapter.ViewHolder> {

    private List<Location> locations;
    private OnDeleteClickListener deleteListener;
    private OnItemClickListener itemClickListener;

    public interface OnDeleteClickListener {
        void onDelete(Location location, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(Location location);
    }

    public FavoriteLocationAdapter(List<Location> locations) {
        this.locations = locations;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_favorite_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location loc = locations.get(position);
        holder.tvCityName.setText(loc.getCityName());
        
        // Populate distinct mock data according to Screen 9 in design
        String cityName = loc.getCityName();
        if (cityName.contains("Hà Nội")) {
            holder.tvCondition.setText("Nhiều mây");
            holder.tvTemperature.setText("28°");
            holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_cloud);
        } else if (cityName.contains("Đà Nẵng")) {
            holder.tvCondition.setText("Trời nắng");
            holder.tvTemperature.setText("30°");
            holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_sun);
        } else if (cityName.contains("Đà Lạt")) {
            holder.tvCondition.setText("Có mưa");
            holder.tvTemperature.setText("22°");
            holder.ivWeatherIcon.setImageResource(R.drawable.ic_humidity);
        } else if (cityName.contains("Cần Thơ")) {
            holder.tvCondition.setText("Trời nắng");
            holder.tvTemperature.setText("31°");
            holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_sun);
        } else {
            holder.tvCondition.setText("Trời nắng");
            holder.tvTemperature.setText("32°");
            holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_sun);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(loc, position);
        });

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) itemClickListener.onItemClick(loc);
        });
    }

    @Override
    public int getItemCount() {
        return locations != null ? locations.size() : 0;
    }

    public void removeItem(int position) {
        locations.remove(position);
        notifyItemRemoved(position);
    }

    public void updateData(List<Location> newList) {
        this.locations = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWeatherIcon;
        TextView tvCityName, tvCondition, tvTemperature;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            tvCondition = itemView.findViewById(R.id.tvCondition);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
