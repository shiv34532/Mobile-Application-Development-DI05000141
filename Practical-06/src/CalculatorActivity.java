package com.gpb.uiapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {
    private StringBuilder currentInput = new StringBuilder();
    private String operator = "";
    private double firstOperand = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        TextView tvDisplay = findViewById(R.id.tvDisplay);
        int[] digitIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

        for (int index = 0; index < digitIds.length; index++) {
            final int digit = index;
            findViewById(digitIds[index]).setOnClickListener(v -> {
                currentInput.append(digit);
                tvDisplay.setText(currentInput.toString());
            });
        }

        findViewById(R.id.btnPlus).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> setOperator("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> setOperator("/"));

        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            double secondOperand = parseInput();
            double result;
            switch (operator) {
                case "+": result = firstOperand + secondOperand; break;
                case "-": result = firstOperand - secondOperand; break;
                case "*": result = firstOperand * secondOperand; break;
                case "/": result = secondOperand != 0.0 ? firstOperand / secondOperand : Double.NaN; break;
                default: result = secondOperand;
            }
            tvDisplay.setText(String.valueOf(result));
            currentInput = new StringBuilder(String.valueOf(result));
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            currentInput.setLength(0);
            firstOperand = 0.0;
            operator = "";
            tvDisplay.setText("0");
        });
    }

    private void setOperator(String op) {
        firstOperand = parseInput();
        operator = op;
        currentInput.setLength(0);
    }

    private double parseInput() {
        try {
            return Double.parseDouble(currentInput.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
