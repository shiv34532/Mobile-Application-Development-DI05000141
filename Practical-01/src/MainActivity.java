package com.gpb.helloworldapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClick = findViewById(R.id.btnClick);
        btnClick.setOnClickListener(v ->
                Toast.makeText(this, "Welcome to Android Development!", Toast.LENGTH_SHORT).show());
    }
}
