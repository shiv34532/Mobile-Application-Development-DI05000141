package com.example.arpitapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Practical5RegisterBackend extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etRetypePassword, etContact;
    private Spinner spinnerGender;
    private Button btnSubmit;

    private boolean isFormValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical5_register_frontend);

        // Bind UI elements
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRetypePassword = findViewById(R.id.etRetypePassword);
        etContact = findViewById(R.id.etContact);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Set up Gender Spinner Options (Male, Female, Other)
        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // TextWatcher listener for real-time validation across all fields
        TextWatcher validationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etFirstName.addTextChangedListener(validationWatcher);
        etLastName.addTextChangedListener(validationWatcher);
        etEmail.addTextChangedListener(validationWatcher);
        etPassword.addTextChangedListener(validationWatcher);
        etRetypePassword.addTextChangedListener(validationWatcher);
        etContact.addTextChangedListener(validationWatcher);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkFormValidation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                checkFormValidation();
            }
        });

        // Submit Button Click Event
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFormValid) {
                    // If fields are incomplete/invalid, display prompt toast on clicking grey button
                    Toast.makeText(Practical5RegisterBackend.this, "Please fill the form properly", Toast.LENGTH_SHORT).show();
                } else {
                    // Registration successful
                    String fname = etFirstName.getText().toString().trim();
                    Toast.makeText(Practical5RegisterBackend.this, "Registration Successful for " + fname + "!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Initial validation check
        checkFormValidation();
    }

    /**
     * Validates all registration conditions:
     * 1. First Name & Last Name present
     * 2. Email valid format
     * 3. Password >= 8 characters
     * 4. Re-type Password matches Password
     * 5. Contact number == exactly 10 digits
     */
    private void checkFormValidation() {
        String fname = etFirstName.getText() != null ? etFirstName.getText().toString().trim() : "";
        String lname = etLastName.getText() != null ? etLastName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String pass = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String rePass = etRetypePassword.getText() != null ? etRetypePassword.getText().toString() : "";
        String contact = etContact.getText() != null ? etContact.getText().toString().trim() : "";

        boolean isFirstNameOk = !fname.isEmpty();
        boolean isLastNameOk = !lname.isEmpty();
        boolean isEmailOk = !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean isPassOk = pass.length() >= 8;
        boolean isRetypeOk = !rePass.isEmpty() && rePass.equals(pass);
        boolean isContactOk = contact.length() == 10;

        isFormValid = isFirstNameOk && isLastNameOk && isEmailOk && isPassOk && isRetypeOk && isContactOk;

        if (isFormValid) {
            // All valid -> Change button color to PURPLE
            btnSubmit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C27B0")));
        } else {
            // Invalid -> Keep button color GREY
            btnSubmit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#888888")));
        }
    }
}
