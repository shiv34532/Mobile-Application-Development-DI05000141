package com.example.arpitapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Practical5CalcBackend extends AppCompatActivity {

    private TextView tvDisplay, tvLivePreview;
    private HorizontalScrollView scrollDisplay;
    private StringBuilder currentExpression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical5_calc_frontend);

        tvDisplay = findViewById(R.id.tvDisplay);
        tvLivePreview = findViewById(R.id.tvLivePreview);
        scrollDisplay = findViewById(R.id.scrollDisplay);

        // Digits
        int[] numericButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                currentExpression.append(button.getText().toString());
                updateDisplayAndPreview();
            }
        };

        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        // Operators (+, -, ×, /)
        findViewById(R.id.btnAdd).setOnClickListener(v -> appendOperator("+"));
        findViewById(R.id.btnSub).setOnClickListener(v -> appendOperator("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> appendOperator("×"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> appendOperator("/"));

        // Clear Button (C)
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            currentExpression.setLength(0);
            tvDisplay.setText("");
            tvLivePreview.setText("");
        });

        // Equal Button (=) with smooth slide-up animation into main expression line
        findViewById(R.id.btnEqual).setOnClickListener(v -> {
            String evaluated = calculateResult(currentExpression.toString());
            if (!evaluated.isEmpty() && !evaluated.startsWith("Error") && !evaluated.startsWith("Cannot")) {
                // Smooth animation: Live preview text slides up into main display
                tvLivePreview.animate()
                        .translationY(-35f)
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> {
                            currentExpression.setLength(0);
                            currentExpression.append(evaluated);
                            tvDisplay.setText(evaluated);
                            tvLivePreview.setText("");
                            tvLivePreview.setTranslationY(0f);
                            tvLivePreview.setAlpha(1.0f);
                        })
                        .start();
            } else if (evaluated.startsWith("Cannot") || evaluated.startsWith("Error")) {
                tvLivePreview.setText(evaluated);
            }
        });
    }

    private void appendOperator(String op) {
        if (currentExpression.length() == 0) return;

        // Prevent duplicate consecutive operators
        char lastChar = currentExpression.charAt(currentExpression.length() - 1);
        if (lastChar == '+' || lastChar == '-' || lastChar == '×' || lastChar == 'x' || lastChar == '/') {
            currentExpression.setCharAt(currentExpression.length() - 1, op.charAt(0));
        } else {
            currentExpression.append(op);
        }
        updateDisplayAndPreview();
    }

    private void updateDisplayAndPreview() {
        String expr = currentExpression.toString();
        tvDisplay.setText(expr);

        // Auto-scroll to the right so new typed numbers are always visible
        if (scrollDisplay != null) {
            tvDisplay.post(() -> scrollDisplay.fullScroll(View.FOCUS_RIGHT));
        }

        String resultStr = calculateResult(expr);
        if (resultStr.isEmpty()) {
            tvLivePreview.setText("");
        } else if (resultStr.startsWith("Cannot") || resultStr.startsWith("Error")) {
            tvLivePreview.setText(resultStr);
        } else {
            tvLivePreview.setText("= " + resultStr);
        }
    }

    /**
     * Exception-safe expression calculator with live evaluation & zero error handling.
     */
    private String calculateResult(String expr) {
        if (expr == null || expr.trim().isEmpty()) return "";

        // Remove trailing operators for live evaluation
        String evalExpr = expr;
        while (evalExpr.endsWith("+") || evalExpr.endsWith("-") || evalExpr.endsWith("×") || evalExpr.endsWith("x") || evalExpr.endsWith("/")) {
            evalExpr = evalExpr.substring(0, evalExpr.length() - 1);
        }
        if (evalExpr.isEmpty()) return "";

        try {
            double res = eval(evalExpr);

            if (Double.isInfinite(res) || Double.isNaN(res)) {
                return "Cannot divide by 0";
            }

            // Format result: if whole number, omit trailing .0
            if (res == (long) res) {
                return String.format("%d", (long) res);
            } else {
                return String.valueOf(res);
            }
        } catch (ArithmeticException ae) {
            return "Cannot divide by 0";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Expression parser supporting +, -, ×, / with precedence and exception handling.
     */
    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('×') || eat('x') || eat('X') || eat('*')) {
                        x *= parseFactor();
                    } else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) throw new ArithmeticException("Division by zero");
                        x /= divisor;
                    } else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected character");
                }

                return x;
            }
        }.parse();
    }
}
