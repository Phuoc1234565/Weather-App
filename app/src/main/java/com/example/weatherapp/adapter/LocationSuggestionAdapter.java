package com.example.weatherapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.model.Location;
import java.util.List;

public class LocationSuggestionAdapter extends RecyclerView.Adapter<LocationSuggestionAdapter.ViewHolder> {

    private List<Location> suggestions;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Location location);
    }

    public LocationSuggestionAdapter(List<Location> suggestions) {
        this.suggestions = suggestions;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_location_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location loc = suggestions.get(position);
        String displayName = loc.getCityName();
        if (loc.getProvince() != null && !loc.getProvince().isEmpty() &&
            !loc.getProvince().equals(loc.getCityName())) {
            displayName += ", " + loc.getProvince();
        }
        holder.tvLocationName.setText(displayName);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(loc);
        });
    }

    @Override
    public int getItemCount() {
        return suggestions != null ? suggestions.size() : 0;
    }

    public void updateData(List<Location> newList) {
        this.suggestions = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocationName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
        }
    }
}
