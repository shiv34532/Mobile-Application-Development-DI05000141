package com.example.arpitapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Practical9DisplayBackend extends AppCompatActivity {

    private TextView tvRetrievedName, tvRetrievedEnroll, tvRetrievedDept;
    private Button btnBackToInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical9_display_frontend);

        // Bind UI Elements
        tvRetrievedName = findViewById(R.id.tvRetrievedName);
        tvRetrievedEnroll = findViewById(R.id.tvRetrievedEnroll);
        tvRetrievedDept = findViewById(R.id.tvRetrievedDept);
        btnBackToInput = findViewById(R.id.btnBackToInput);

        // Read data directly from SharedPreferences (Persistent Storage)
        SharedPreferences prefs = getSharedPreferences(Practical9Backend.PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString(Practical9Backend.KEY_NAME, "");
        String enroll = prefs.getString(Practical9Backend.KEY_ENROLL, "");
        String dept = prefs.getString(Practical9Backend.KEY_DEPT, "");

        // Display Data
        tvRetrievedName.setText(!TextUtils.isEmpty(name) ? name : "No Name Saved");
        tvRetrievedEnroll.setText(!TextUtils.isEmpty(enroll) ? enroll : "No Enrollment Saved");
        tvRetrievedDept.setText(!TextUtils.isEmpty(dept) ? dept : "No Department Saved");

        // Back button to navigate back to Screen 1
        btnBackToInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
