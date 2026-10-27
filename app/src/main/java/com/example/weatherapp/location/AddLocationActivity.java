package com.example.weatherapp.location;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.adapter.LocationSuggestionAdapter;
import com.example.weatherapp.model.Location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AddLocationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etSearchAdd;
    private RecyclerView rvSuggestedLocations;
    private LocationSuggestionAdapter adapter;

    private List<Location> allSuggestions = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBack = findViewById(R.id.btnBack);
        etSearchAdd = findViewById(R.id.etSearchAdd);
        rvSuggestedLocations = findViewById(R.id.rvSuggestedLocations);

        btnBack.setOnClickListener(v -> finish());

        setupSuggestions();

        adapter = new LocationSuggestionAdapter(new ArrayList<>(allSuggestions));
        rvSuggestedLocations.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestedLocations.setAdapter(adapter);

        adapter.setOnItemClickListener(this::addLocationToFirestore);

        etSearchAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSuggestions() {
        allSuggestions.add(new Location("Hà Nội", "Hà Nội", 21.0285, 105.8542));
        allSuggestions.add(new Location("Hải Phòng", "Hải Phòng", 20.8449, 106.6881));
        allSuggestions.add(new Location("Đà Nẵng", "Đà Nẵng", 16.0544, 108.2022));
        allSuggestions.add(new Location("TP. Hồ Chí Minh", "TP. Hồ Chí Minh", 10.8231, 106.6297));
        allSuggestions.add(new Location("Cần Thơ", "Cần Thơ", 10.0452, 105.7469));
        allSuggestions.add(new Location("Đà Lạt", "Lâm Đồng", 11.9404, 108.4583));
        allSuggestions.add(new Location("Nha Trang", "Khánh Hòa", 12.2388, 109.1967));
        allSuggestions.add(new Location("Huế", "Thừa Thiên Huế", 16.4637, 107.5909));
    }

    private void filter(String query) {
        if (query.trim().isEmpty()) {
            adapter.updateData(new ArrayList<>(allSuggestions));
        } else {
            List<Location> filtered = new ArrayList<>();
            for (Location loc : allSuggestions) {
                if (loc.getCityName().toLowerCase().contains(query.toLowerCase()) ||
                    loc.getProvince().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(loc);
                }
            }
            adapter.updateData(filtered);
        }
    }

    private void addLocationToFirestore(Location loc) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .add(loc)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Đã thêm " + loc.getCityName() + " vào yêu thích", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi thêm địa điểm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        } else {
            Toast.makeText(this, "Đã thêm " + loc.getCityName(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
