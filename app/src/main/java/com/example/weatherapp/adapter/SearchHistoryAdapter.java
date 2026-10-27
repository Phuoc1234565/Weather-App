package com.example.weatherapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<String> historyList;
    private OnItemClickListener itemClickListener;
    private OnRemoveClickListener removeClickListener;

    public interface OnItemClickListener {
        void onItemClick(String query);
    }

    public interface OnRemoveClickListener {
        void onRemove(String query, int position);
    }

    public SearchHistoryAdapter(List<String> historyList) {
        this.historyList = historyList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        this.removeClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String query = historyList.get(position);
        holder.tvSearchQuery.setText(query);

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) itemClickListener.onItemClick(query);
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (removeClickListener != null) removeClickListener.onRemove(query, position);
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public void removeItem(int position) {
        historyList.remove(position);
        notifyItemRemoved(position);
    }

    public void updateData(List<String> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSearchQuery;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchQuery = itemView.findViewById(R.id.tvSearchQuery);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
