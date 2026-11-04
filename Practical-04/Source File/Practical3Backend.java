package com.example.arpitapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.HorizontalScrollView;
import androidx.appcompat.app.AppCompatActivity;

public class Practical3Backend extends AppCompatActivity {

    private TextView tvDisplay;
    private HorizontalScrollView scrollDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical3_frontend);

        tvDisplay = findViewById(R.id.tvDisplay);
        scrollDisplay = findViewById(R.id.scrollDisplay);

        // Buttons array
        int[] buttonIds = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnAdd, R.id.btnSub, R.id.btnMul, R.id.btnDiv, R.id.btnEqual
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String currentText = tvDisplay.getText().toString();
                tvDisplay.setText(currentText + b.getText().toString());
                
                scrollDisplay.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollDisplay.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                });
            }
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(listener);
        }

        // Clear button logic
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDisplay.setText("");
            }
        });
    }
}
