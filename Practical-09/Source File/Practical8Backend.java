package com.example.arpitapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Practical8Backend extends AppCompatActivity {

    private RecyclerView recyclerViewTech;
    private TextView tvDetailTitle, tvDetailDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical8_frontend);

        // Bind UI Elements
        recyclerViewTech = findViewById(R.id.recyclerViewTech);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        // Set LayoutManager for vertical scrolling list
        recyclerViewTech.setLayoutManager(new LinearLayoutManager(this));

        // Prepare data list
        List<Practical8Model> items = new ArrayList<>();
        items.add(new Practical8Model(
                "Android App Development",
                "Java & Kotlin | Mobile OS",
                "Native open-source mobile OS maintained by Google. Powers over 3 billion active devices globally.",
                "A",
                0xFF4CAF50));

        items.add(new Practical8Model(
                "Python Programming",
                "AI, ML & Backend Data Science",
                "High-level versatile programming language. The leading choice for Artificial Intelligence and Machine Learning.",
                "P",
                0xFF0288D1));

        items.add(new Practical8Model(
                "Java Technology",
                "Enterprise, Spring Boot & Backend",
                "Class-based object-oriented programming language designed for 'Write Once, Run Anywhere' cross-platform software.",
                "J",
                0xFFFF9800));

        items.add(new Practical8Model(
                "Web Development",
                "HTML, CSS, JavaScript & React",
                "Modern frontend and backend web architecture used for developing responsive, high-performance web applications.",
                "W",
                0xFFE91E63));

        items.add(new Practical8Model(
                "Flutter & Dart",
                "Cross-Platform Multi-OS Apps",
                "Open-source UI development kit created by Google to build beautiful natively compiled applications from a single codebase.",
                "F",
                0xFF00BCD4));

        items.add(new Practical8Model(
                "Database Systems",
                "SQL, MySQL & Firebase Cloud",
                "Data management and cloud synchronization engines powering persistent app storage and user sessions.",
                "D",
                0xFF9C27B0));

        // Attach Custom Adapter with Item Click Listener
        Practical8Adapter adapter = new Practical8Adapter(items, item -> {
            tvDetailTitle.setText("🚀 " + item.getTitle());
            tvDetailDescription.setText(item.getDescription());
            Toast.makeText(Practical8Backend.this, "Selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerViewTech.setAdapter(adapter);
    }
}
