package com.example.arpitapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class Practical6Backend extends AppCompatActivity {

    private Spinner spinnerBrand;
    private ListView listViewModel;
    private TextView tvDetailTitle, tvDetailSpecs;

    // Brands array for Spinner
    private final String[] brands = {"Apple", "Samsung", "Google Pixel", "OnePlus", "Xiaomi"};

    // Models map for each brand
    private final Map<String, String[]> modelsMap = new HashMap<>();

    // Specs map for each phone model
    private final Map<String, String> specsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical6_frontend);

        // Bind UI elements
        spinnerBrand = findViewById(R.id.spinnerBrand);
        listViewModel = findViewById(R.id.listViewModel);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailSpecs = findViewById(R.id.tvDetailSpecs);

        // Initialize Data
        initData();

        // Populate Spinner with Brand names
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, brands);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(spinnerAdapter);

        // 1. Spinner Event Listener (OnItemSelectedListener)
        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBrand = brands[position];

                // Dynamically update ListView adapter based on selected brand
                String[] currentModels = modelsMap.get(selectedBrand);
                if (currentModels != null) {
                    ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                            Practical6Backend.this, android.R.layout.simple_list_item_1, currentModels);
                    listViewModel.setAdapter(listAdapter);
                }

                // Reset detail box text
                tvDetailTitle.setText("Brand: " + selectedBrand);
                tvDetailSpecs.setText("Click any " + selectedBrand + " model above to view full specifications.");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 2. ListView Event Listener (OnItemClickListener)
        listViewModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedModel = (String) parent.getItemAtPosition(position);
                String specInfo = specsMap.get(selectedModel);

                if (specInfo == null) {
                    specInfo = "Specification details unavailable.";
                }

                // Display selected phone details in Card
                tvDetailTitle.setText("📱 " + selectedModel);
                tvDetailSpecs.setText(specInfo);

                // Show Toast feedback
                Toast.makeText(Practical6Backend.this, "Selected: " + selectedModel, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        // Apple Models & Specs
        modelsMap.put("Apple", new String[]{"iPhone 15 Pro Max", "iPhone 15", "iPhone 14 Pro", "iPhone 13"});
        specsMap.put("iPhone 15 Pro Max", "Processor: Apple A17 Pro (3nm)\nRAM: 8 GB | Storage: 256 GB\nCamera: 48 MP Triple Lens\nPrice: ₹1,59,900");
        specsMap.put("iPhone 15", "Processor: Apple A16 Bionic\nRAM: 6 GB | Storage: 128 GB\nDisplay: 6.1\" Super Retina XDR\nPrice: ₹79,900");
        specsMap.put("iPhone 14 Pro", "Processor: Apple A16 Bionic\nRAM: 6 GB | Storage: 128 GB\nFeatures: Dynamic Island\nPrice: ₹1,19,900");
        specsMap.put("iPhone 13", "Processor: Apple A15 Bionic\nRAM: 4 GB | Storage: 128 GB\nBattery: 3240 mAh\nPrice: ₹52,900");

        // Samsung Models & Specs
        modelsMap.put("Samsung", new String[]{"Galaxy S24 Ultra", "Galaxy S23 FE", "Galaxy Z Fold 5", "Galaxy A55"});
        specsMap.put("Galaxy S24 Ultra", "Processor: Snapdragon 8 Gen 3\nRAM: 12 GB | Storage: 512 GB\nCamera: 200 MP + Galaxy AI\nPrice: ₹1,29,999");
        specsMap.put("Galaxy S23 FE", "Processor: Exynos 2200\nRAM: 8 GB | Storage: 128 GB\nDisplay: 120Hz Dynamic AMOLED\nPrice: ₹49,999");
        specsMap.put("Galaxy Z Fold 5", "Processor: Snapdragon 8 Gen 2\nRAM: 12 GB | Storage: 256 GB\nDisplay: 7.6\" Foldable AMOLED\nPrice: ₹1,54,999");
        specsMap.put("Galaxy A55", "Processor: Exynos 1480\nRAM: 8 GB | Storage: 128 GB\nBuild: IP67 Water Resistant\nPrice: ₹39,999");

        // Google Pixel Models & Specs
        modelsMap.put("Google Pixel", new String[]{"Pixel 8 Pro", "Pixel 8a", "Pixel 7a", "Pixel Fold"});
        specsMap.put("Pixel 8 Pro", "Processor: Google Tensor G3\nRAM: 12 GB | Storage: 128 GB\nFeature: Best-in-class AI Camera\nPrice: ₹1,06,999");
        specsMap.put("Pixel 8a", "Processor: Google Tensor G3\nRAM: 8 GB | Storage: 128 GB\nDisplay: 120Hz OLED\nPrice: ₹52,999");
        specsMap.put("Pixel 7a", "Processor: Google Tensor G2\nRAM: 8 GB | Storage: 128 GB\nCamera: 64 MP Dual Rear\nPrice: ₹37,999");
        specsMap.put("Pixel Fold", "Processor: Google Tensor G2\nRAM: 12 GB | Storage: 256 GB\nDesign: Dual Screen Foldable\nPrice: ₹1,73,999");

        // OnePlus Models & Specs
        modelsMap.put("OnePlus", new String[]{"OnePlus 12", "OnePlus 12R", "OnePlus Open", "OnePlus 11"});
        specsMap.put("OnePlus 12", "Processor: Snapdragon 8 Gen 3\nRAM: 12 GB | Storage: 256 GB\nCharging: 100W SUPERVOOC\nPrice: ₹64,999");
        specsMap.put("OnePlus 12R", "Processor: Snapdragon 8 Gen 2\nRAM: 8 GB | Storage: 128 GB\nBattery: 5500 mAh\nPrice: ₹39,999");
        specsMap.put("OnePlus Open", "Processor: Snapdragon 8 Gen 2\nRAM: 16 GB | Storage: 512 GB\nDisplay: Dual ProXDR Displays\nPrice: ₹1,39,999");
        specsMap.put("OnePlus 11", "Processor: Snapdragon 8 Gen 2\nRAM: 8 GB | Storage: 128 GB\nCamera: Hasselblad Optics\nPrice: ₹56,999");

        // Xiaomi Models & Specs
        modelsMap.put("Xiaomi", new String[]{"Xiaomi 14 Ultra", "Xiaomi 13 Pro", "Redmi Note 13 Pro+"});
        specsMap.put("Xiaomi 14 Ultra", "Processor: Snapdragon 8 Gen 3\nRAM: 16 GB | Storage: 512 GB\nCamera: Leica Quad 50 MP\nPrice: ₹99,999");
        specsMap.put("Xiaomi 13 Pro", "Processor: Snapdragon 8 Gen 2\nRAM: 12 GB | Storage: 256 GB\nCamera: 1-inch Leica Sensor\nPrice: ₹74,999");
        specsMap.put("Redmi Note 13 Pro+", "Processor: MediaTek Dimensity 7200 Ultra\nRAM: 8 GB | Storage: 256 GB\nCharging: 120W HyperCharge\nPrice: ₹31,999");
    }
}
