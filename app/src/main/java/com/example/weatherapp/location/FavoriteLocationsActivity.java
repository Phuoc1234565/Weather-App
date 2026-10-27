package com.example.weatherapp.location;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.adapter.FavoriteLocationAdapter;
import com.example.weatherapp.model.Location;
import com.example.weatherapp.weather.WeatherDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FavoriteLocationsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnAddLocation;
    private RecyclerView rvFavorites;
    private FavoriteLocationAdapter adapter;
    private List<Location> favoriteList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_locations);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnAddLocation = findViewById(R.id.btnAddLocation);
        rvFavorites = findViewById(R.id.rvFavorites);

        btnBack.setOnClickListener(v -> finish());
        btnAddLocation.setOnClickListener(v -> {
            startActivity(new Intent(this, AddLocationActivity.class));
        });

        adapter = new FavoriteLocationAdapter(favoriteList);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);

        adapter.setOnItemClickListener(location -> {
            Intent intent = new Intent(this, WeatherDetailActivity.class);
            intent.putExtra("cityName", location.getCityName());
            startActivity(intent);
        });

        adapter.setOnDeleteClickListener((location, position) -> {
            deleteFavoriteLocation(location, position);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            setupMockData();
            return;
        }

        db.collection("users")
            .document(currentUser.getUid())
            .collection("favorites")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                favoriteList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Location loc = doc.toObject(Location.class);
                    loc.setId(doc.getId());
                    favoriteList.add(loc);
                }
                if (favoriteList.isEmpty()) {
                    setupMockData();
                } else {
                    adapter.notifyDataSetChanged();
                }
            })
            .addOnFailureListener(e -> {
                setupMockData();
            });
    }

    private void setupMockData() {
        favoriteList.clear();
        favoriteList.add(new Location("Hồ Chí Minh", "TP. Hồ Chí Minh", 10.8231, 106.6297));
        favoriteList.add(new Location("Hà Nội", "Hà Nội", 21.0285, 105.8542));
        favoriteList.add(new Location("Đà Nẵng", "Đà Nẵng", 16.0544, 108.2022));
        favoriteList.add(new Location("Đà Lạt", "Lâm Đồng", 11.9404, 108.4583));
        favoriteList.add(new Location("Cần Thơ", "Cần Thơ", 10.0452, 105.7469));
        adapter.notifyDataSetChanged();
    }

    private void deleteFavoriteLocation(Location location, int position) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && location.getId() != null) {
            db.collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .document(location.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xóa địa điểm", Toast.LENGTH_SHORT).show();
                    adapter.removeItem(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi xóa địa điểm", Toast.LENGTH_SHORT).show();
                });
        } else {
            adapter.removeItem(position);
        }
    }
}
