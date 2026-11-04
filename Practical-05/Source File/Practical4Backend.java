package com.example.arpitapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Practical4Backend extends AppCompatActivity {

    private EditText etInputText;
    private TextView tvCharCount, tvOutputDisplay;
    private Button btnSubmit, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical4_frontend);

        // Bind UI Elements
        etInputText = findViewById(R.id.etInputText);
        tvCharCount = findViewById(R.id.tvCharCount);
        tvOutputDisplay = findViewById(R.id.tvOutputDisplay);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnClear = findViewById(R.id.btnClear);

        // 1. TextWatcher Event Listener: Live Character Count as user types in EditText
        etInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for character count
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Real-time update of character count
                int length = s.length();
                tvCharCount.setText("Character Count: " + length);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for character count
            }
        });

        // 2. OnClickListener Event Listener for Display / Submit Button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = etInputText.getText().toString().trim();

                if (inputText.isEmpty()) {
                    Toast.makeText(Practical4Backend.this, "Please enter some text first!", Toast.LENGTH_SHORT).show();
                } else {
                    // Copy input text to the output display box
                    tvOutputDisplay.setText(inputText);
                    tvOutputDisplay.setTextColor(0xFF212121);
                    Toast.makeText(Practical4Backend.this, "Text Displayed Successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3. OnClickListener Event Listener for Clear Button
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etInputText.setText("");
                tvOutputDisplay.setText("Output will appear here after clicking 'Display Text'...");
                tvOutputDisplay.setTextColor(0xFF616161);
                tvCharCount.setText("Character Count: 0");
                Toast.makeText(Practical4Backend.this, "Cleared All Inputs!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
