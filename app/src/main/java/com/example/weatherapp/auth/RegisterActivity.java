package com.example.weatherapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ImageButton btnBack, btnTogglePassword, btnToggleConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean passVisible = false;
    private boolean confirmPassVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        btnBack = findViewById(R.id.btnBack);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        btnBack.setOnClickListener(v -> finish());

        btnTogglePassword.setOnClickListener(v -> {
            passVisible = !passVisible;
            etPassword.setInputType(passVisible ?
                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        });

        btnToggleConfirmPassword.setOnClickListener(v -> {
            confirmPassVisible = !confirmPassVisible;
            etConfirmPassword.setInputType(confirmPassVisible ?
                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnRegister.setOnClickListener(v -> performRegister());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void performRegister() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etFullName.setError(getString(R.string.error_name_empty));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_email_empty));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_password_empty));
            return;
        }
        if (!password.equals(confirmPass)) {
            etConfirmPassword.setError(getString(R.string.error_password_mismatch));
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Đang đăng ký...");

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        // Save user to Firestore
                        User user = new User(firebaseUser.getUid(), name, email);
                        db.collection("users")
                            .document(firebaseUser.getUid())
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnRegister.setEnabled(true);
                                btnRegister.setText(getString(R.string.btn_register));
                                Toast.makeText(RegisterActivity.this,
                                    "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    }
                } else {
                    btnRegister.setEnabled(true);
                    btnRegister.setText(getString(R.string.btn_register));
                    Toast.makeText(RegisterActivity.this,
                        getString(R.string.error_register_failed) + ": " +
                        (task.getException() != null ? task.getException().getMessage() : ""),
                        Toast.LENGTH_LONG).show();
                }
            });
    }
}
