package com.example.weatherapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmailPhone, etPassword;
    private Button btnLogin;
    private CheckBox cbRemember;
    private TextView tvForgotPassword, tvRegister;
    private ImageButton btnBack, btnTogglePassword, btnGoogle, btnFacebook, btnApple;
    private FirebaseAuth mAuth;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        etEmailPhone = findViewById(R.id.etEmailPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRemember = findViewById(R.id.cbRemember);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        btnBack = findViewById(R.id.btnBack);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);

        btnBack.setOnClickListener(v -> finish());

        btnTogglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                etPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmailPhone.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmailPhone.setError("Nhập email để nhận liên kết đặt lại mật khẩu");
                etEmailPhone.requestFocus();
                return;
            }
            mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> Toast.makeText(this,
                    "Đã gửi liên kết đặt lại mật khẩu đến " + email, Toast.LENGTH_LONG).show())
                .addOnFailureListener(error -> Toast.makeText(this,
                    "Không thể gửi email đặt lại: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show());
        });
    }

    private void performLogin() {
        String email = etEmailPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmailPhone.setError(getString(R.string.error_email_empty));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_password_empty));
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                btnLogin.setEnabled(true);
                btnLogin.setText(getString(R.string.btn_login));
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_login_failed) + ": " +
                        (task.getException() != null ? task.getException().getMessage() : ""),
                        Toast.LENGTH_LONG).show();
                }
            });
    }
}
