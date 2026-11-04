package com.example.arpitapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Practical5LoginBackend extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical5_login_frontend);

        // Bind UI Elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Initially disable button and set color to GREY
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));

        // TextWatcher to listen for real-time changes on Email and Password fields
        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etEmail.addTextChangedListener(inputWatcher);
        etPassword.addTextChangedListener(inputWatcher);

        // Login Click Event Listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = etEmail.getText().toString().trim();
                Toast.makeText(Practical5LoginBackend.this, "Login successful for user: " + userEmail, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Checks if email is valid and password length is >= 8.
     * Toggles button state (Enabled/Disabled) and color (Blue/Grey) dynamically.
     */
    private void checkValidation() {
        String emailInput = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String passwordInput = etPassword.getText() != null ? etPassword.getText().toString() : "";

        boolean isEmailValid = !emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches();
        boolean isPasswordValid = passwordInput.length() >= 8;

        if (isEmailValid && isPasswordValid) {
            // Enable button & turn BLUE
            btnLogin.setEnabled(true);
            btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
        } else {
            // Disable button & turn GREY
            btnLogin.setEnabled(false);
            btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));
        }
    }
}
