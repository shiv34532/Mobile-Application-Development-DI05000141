package com.example.arpitapplication;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Practical7Backend extends AppCompatActivity {

    private LinearLayout layoutContextTarget;
    private Button btnShowPopupMenu;
    private TextView tvMenuFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical7_frontend);

        // Bind Toolbar and set as Action Bar so 3-dots Options Menu displays at top right
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar.getOverflowIcon() != null) {
            toolbar.getOverflowIcon().setTint(android.graphics.Color.WHITE);
        }

        // Bind UI Elements
        layoutContextTarget = findViewById(R.id.layoutContextTarget);
        btnShowPopupMenu = findViewById(R.id.btnShowPopupMenu);
        tvMenuFeedback = findViewById(R.id.tvMenuFeedback);

        // 1. Register Context Menu on the target layout (Long-Press Target)
        registerForContextMenu(layoutContextTarget);

        // 2. Popup Menu Event Listener on Button Click
        btnShowPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    // =========================================================================
    // 1. OPTIONS MENU IMPLEMENTATION (Top-Right Action Bar 3-Dots Menu)
    // =========================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String feedback = "";
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            feedback = "Options Menu: Selected 'Settings'";
        } else if (id == R.id.menu_share) {
            feedback = "Options Menu: Selected 'Share App'";
        } else if (id == R.id.menu_theme) {
            feedback = "Options Menu: Selected 'Toggle Theme'";
        } else if (id == R.id.menu_about) {
            feedback = "Options Menu: Selected 'About Us'";
        }

        if (!feedback.isEmpty()) {
            tvMenuFeedback.setText(feedback);
            Toast.makeText(this, feedback, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // =========================================================================
    // 2. CONTEXT MENU IMPLEMENTATION (Triggered on Long-Press of target View)
    // =========================================================================
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu Options");
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String feedback = "";
        int id = item.getItemId();

        if (id == R.id.ctx_copy) {
            feedback = "Context Menu: Selected 'Copy Text'";
        } else if (id == R.id.ctx_favorite) {
            feedback = "Context Menu: Selected 'Add to Favorites'";
        } else if (id == R.id.ctx_share) {
            feedback = "Context Menu: Selected 'Share Item'";
        } else if (id == R.id.ctx_delete) {
            feedback = "Context Menu: Selected 'Delete Item'";
        }

        if (!feedback.isEmpty()) {
            tvMenuFeedback.setText(feedback);
            Toast.makeText(this, feedback, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // =========================================================================
    // 3. POPUP MENU IMPLEMENTATION (Triggered on Button Click)
    // =========================================================================
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String feedback = "";
                int id = item.getItemId();

                if (id == R.id.pop_sort_name) {
                    feedback = "Popup Menu: Selected 'Sort by Name (A-Z)'";
                } else if (id == R.id.pop_sort_date) {
                    feedback = "Popup Menu: Selected 'Sort by Date'";
                } else if (id == R.id.pop_filter) {
                    feedback = "Popup Menu: Selected 'Filter Results'";
                } else if (id == R.id.pop_export) {
                    feedback = "Popup Menu: Selected 'Export Data'";
                }

                if (!feedback.isEmpty()) {
                    tvMenuFeedback.setText(feedback);
                    Toast.makeText(Practical7Backend.this, feedback, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }
}
