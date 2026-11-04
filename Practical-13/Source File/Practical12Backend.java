package com.example.arpitapplication;

import com.example.arpitapplication.R;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Practical12Backend extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etEmail, etPassword;
    private Button btnRegister, btnLogin, btnLogout;
    private ProgressBar pbAuthLoading;
    private TextView tvAuthStatus, tvSessionBadge, tvUserEmail, tvUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical12_frontend);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Bind Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        pbAuthLoading = findViewById(R.id.pbAuthLoading);
        tvAuthStatus = findViewById(R.id.tvAuthStatus);
        tvSessionBadge = findViewById(R.id.tvSessionBadge);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserUid = findViewById(R.id.tvUserUid);

        // Set Click Listeners
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Check if user is already logged in from previous session
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);
        tvAuthStatus.setText("⏳ Registering on Firebase Cloud...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setLoading(false);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Practical12Backend.this, "Registered & Logged in successfully!", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                            tvAuthStatus.setText("🔴 " + error);
                            Toast.makeText(Practical12Backend.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);
        tvAuthStatus.setText("⏳ Authenticating with Firebase...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setLoading(false);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Practical12Backend.this, "Login successful! Welcome back.", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Login failed";
                            tvAuthStatus.setText("🔴 " + error);
                            Toast.makeText(Practical12Backend.this, "Login Failed: " + error, Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(this, "Logged out from Firebase", Toast.LENGTH_SHORT).show();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            tvSessionBadge.setText("🟢 Active Session");
            tvSessionBadge.setBackgroundResource(R.drawable.bg_pill_green);
            tvSessionBadge.setTextColor(Color.parseColor("#065F46"));

            tvUserEmail.setText("Email: " + user.getEmail());
            tvUserUid.setText("UID: " + user.getUid());
            tvAuthStatus.setText("🟢 Connected to Firebase Cloud");
            btnLogout.setVisibility(View.VISIBLE);
            etPassword.setText("");
        } else {
            tvSessionBadge.setText("⚪ Logged Out");
            tvSessionBadge.setBackgroundResource(R.drawable.bg_pill_gray);
            tvSessionBadge.setTextColor(Color.parseColor("#64748B"));

            tvUserEmail.setText("User: Not Authenticated");
            tvUserUid.setText("UID: ---");
            tvAuthStatus.setText("Enter credentials to register or login");
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            pbAuthLoading.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
            btnLogin.setEnabled(false);
        } else {
            pbAuthLoading.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            btnLogin.setEnabled(true);
        }
    }
}
