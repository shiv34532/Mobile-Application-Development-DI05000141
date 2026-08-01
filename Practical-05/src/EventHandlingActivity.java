package com.gpb.uiapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EventHandlingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        EditText etInput = findViewById(R.id.etInput);
        TextView tvCharCount = findViewById(R.id.tvCharCount);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText("Characters: " + s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v ->
                Toast.makeText(this, "You typed: " + etInput.getText().toString(), Toast.LENGTH_SHORT).show());
    }
}
