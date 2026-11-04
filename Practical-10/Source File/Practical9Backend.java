    package com.example.arpitapplication;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;

    public class Practical9Backend extends AppCompatActivity {

        public static final String PREFS_NAME = "StudentPrefs";
        public static final String KEY_NAME = "KEY_NAME";
        public static final String KEY_ENROLL = "KEY_ENROLL";
        public static final String KEY_DEPT = "KEY_DEPT";

        private EditText etStudentName, etEnrollmentNo, etDepartment;
        private Button btnSaveData, btnViewSaved, btnClearData;
        private SharedPreferences sharedPreferences;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.practical9_frontend);

            // Bind UI Elements
            etStudentName = findViewById(R.id.etStudentName);
            etEnrollmentNo = findViewById(R.id.etEnrollmentNo);
            etDepartment = findViewById(R.id.etDepartment);
            btnSaveData = findViewById(R.id.btnSaveData);
            btnViewSaved = findViewById(R.id.btnViewSaved);
            btnClearData = findViewById(R.id.btnClearData);

            // Initialize SharedPreferences
            sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

            // Pre-fill fields if data was already saved
            loadExistingData();

            // 1. Save Data to SharedPreferences
            btnSaveData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = etStudentName.getText().toString().trim();
                    String enroll = etEnrollmentNo.getText().toString().trim();
                    String dept = etDepartment.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(enroll) || TextUtils.isEmpty(dept)) {
                        Toast.makeText(Practical9Backend.this, "Please fill all fields first!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_NAME, name);
                    editor.putString(KEY_ENROLL, enroll);
                    editor.putString(KEY_DEPT, dept);
                    editor.apply();

                    Toast.makeText(Practical9Backend.this, "Data Saved Successfully in SharedPreferences!", Toast.LENGTH_SHORT).show();
                }
            });

            // 2. Navigate to Display Activity (Screen 2)
            btnViewSaved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Practical9Backend.this, Practical9DisplayBackend.class);
                    startActivity(intent);
                }
            });

            // 3. Clear Stored Data
            btnClearData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    etStudentName.setText("");
                    etEnrollmentNo.setText("");
                    etDepartment.setText("");

                    Toast.makeText(Practical9Backend.this, "Stored data cleared from SharedPreferences!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void loadExistingData() {
            String savedName = sharedPreferences.getString(KEY_NAME, "");
            String savedEnroll = sharedPreferences.getString(KEY_ENROLL, "");
            String savedDept = sharedPreferences.getString(KEY_DEPT, "");

            if (!TextUtils.isEmpty(savedName)) {
                etStudentName.setText(savedName);
            }
            if (!TextUtils.isEmpty(savedEnroll)) {
                etEnrollmentNo.setText(savedEnroll);
            }
            if (!TextUtils.isEmpty(savedDept)) {
                etDepartment.setText(savedDept);
            }
        }
    }
