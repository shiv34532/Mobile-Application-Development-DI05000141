package com.gpb.uiapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class ListSpinnerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_spinner);

        final String[] fruits = {"Apple", "Banana", "Mango", "Orange", "Grapes"};
        Spinner spinner = findViewById(R.id.spinnerFruits);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fruits));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListSpinnerActivity.this, "Selected: " + fruits[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final List<String> items = Arrays.asList("Item A", "Item B", "Item C", "Item D", "Item E");
        ListView listView = findViewById(R.id.listViewItems);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
        listView.setOnItemClickListener((parent, view, position, id) ->
                Toast.makeText(this, "Clicked: " + items.get(position), Toast.LENGTH_SHORT).show());
    }
}
