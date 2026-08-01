package com.gpb.dataapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        EditText etName = findViewById(R.id.etName);
        Button btnSaveNext = findViewById(R.id.btnSaveNext);

        btnSaveNext.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            prefs.edit().putString("username", etName.getText().toString()).apply();

            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        });
    }
}
