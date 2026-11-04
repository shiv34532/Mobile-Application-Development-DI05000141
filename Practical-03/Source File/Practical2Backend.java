package com.example.arpitapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Practical2Backend extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etRetypePassword, etContact;
    private Spinner spinnerGender;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical2_frontend);

        // Link UI Elements
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRetypePassword = findViewById(R.id.etRetypePassword);
        etContact = findViewById(R.id.etContact);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Setup Spinner for Gender
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender.setAdapter(adapter);

        // Submit Button Click Logic
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = etFirstName.getText().toString().trim();
                String lName = etLastName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String retypePassword = etRetypePassword.getText().toString().trim();
                String contact = etContact.getText().toString().trim();
                
                if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || password.isEmpty() || retypePassword.isEmpty() || contact.isEmpty()) {
                    Toast.makeText(Practical2Backend.this, "Please fill in all details!", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(retypePassword)) {
                    Toast.makeText(Practical2Backend.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Practical2Backend.this, "Registration Successful for " + fName + " " + lName, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
