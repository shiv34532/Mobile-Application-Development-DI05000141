package com.example.arpitapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class Practical16Backend extends AppCompatActivity {

    private RadioGroup rgScenarios;
    private RadioButton rbScenario1, rbScenario2, rbScenario3, rbScenario4;
    private Button btnSimulateAI, btnCopyCode;
    private TextView tvToolActiveBadge, tvTerminalTitle;
    private TextView tvPromptHeader, tvPromptText;
    private TextView tvGeneratedCodeHeader, tvGeneratedCode, tvAiInsight;

    // Simulation Data Models
    private static class ScenarioData {
        final String toolBadge;
        final String terminalTitle;
        final String promptHeader;
        final String promptText;
        final String codeHeader;
        final String codeSnippet;
        final String insight;

        ScenarioData(String toolBadge, String terminalTitle, String promptHeader,
                     String promptText, String codeHeader, String codeSnippet, String insight) {
            this.toolBadge = toolBadge;
            this.terminalTitle = terminalTitle;
            this.promptHeader = promptHeader;
            this.promptText = promptText;
            this.codeHeader = codeHeader;
            this.codeSnippet = codeSnippet;
            this.insight = insight;
        }
    }

    private ScenarioData[] scenarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical16_frontend);

        // Dynamic Status Bar: Dark Slate (#0F172A) with white icons
        setupStatusBar(Color.parseColor("#0F172A"), true);

        // Safe Area Insets: Ensure header text starts safely below status bar & camera cutout
        View headerBanner = findViewById(R.id.p16HeaderBanner);
        if (headerBanner != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerBanner, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                int extraTopPadding = (int) (14 * getResources().getDisplayMetrics().density);
                v.setPadding(
                        v.getPaddingLeft(),
                        systemBars.top + extraTopPadding,
                        v.getPaddingRight(),
                        v.getPaddingBottom()
                );
                return insets;
            });
        }

        // Initialize UI Elements
        rgScenarios = findViewById(R.id.rgScenarios);
        rbScenario1 = findViewById(R.id.rbScenario1);
        rbScenario2 = findViewById(R.id.rbScenario2);
        rbScenario3 = findViewById(R.id.rbScenario3);
        rbScenario4 = findViewById(R.id.rbScenario4);
        btnSimulateAI = findViewById(R.id.btnSimulateAI);
        btnCopyCode = findViewById(R.id.btnCopyCode);
        tvToolActiveBadge = findViewById(R.id.tvToolActiveBadge);
        tvTerminalTitle = findViewById(R.id.tvTerminalTitle);
        tvPromptHeader = findViewById(R.id.tvPromptHeader);
        tvPromptText = findViewById(R.id.tvPromptText);
        tvGeneratedCodeHeader = findViewById(R.id.tvGeneratedCodeHeader);
        tvGeneratedCode = findViewById(R.id.tvGeneratedCode);
        tvAiInsight = findViewById(R.id.tvAiInsight);

        // Build Data for the 4 Scenarios
        initScenarios();

        // Listen for Scenario Selection Changes
        rgScenarios.setOnCheckedChangeListener((group, checkedId) -> {
            loadScenario(getSelectedScenarioIndex());
        });

        // Button: Simulate AI Generation with Interactive Feedback
        btnSimulateAI.setOnClickListener(v -> {
            int selectedIndex = getSelectedScenarioIndex();
            loadScenario(selectedIndex);
            Toast.makeText(this, "⚡ AI Generation Completed for Scenario " + (selectedIndex + 1), Toast.LENGTH_SHORT).show();
        });

        // Button: Copy Generated Code to Clipboard
        btnCopyCode.setOnClickListener(v -> {
            String codeToCopy = tvGeneratedCode.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("AI Generated Android Code", codeToCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "📋 Code snippet copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        // Initial Load (Default Scenario 1)
        loadScenario(0);
    }

    private void initScenarios() {
        scenarios = new ScenarioData[4];

        // Scenario 1: Material 3 Login Form (XML)
        scenarios[0] = new ScenarioData(
                "Copilot & Codex",
                "IDE CODE VIEWER • XML LAYOUT",
                "// DEVELOPER PROMPT / CONTEXT:",
                "Create a Material 3 OutlinedTextField login form with Email, Password (toggle enabled), and Login Button in Android XML.",
                "// SIMULATED AI GENERATED OUTPUT (XML):",
                "<!-- Material 3 Login Form generated by AI -->\n" +
                "<com.google.android.material.textfield.TextInputLayout\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"wrap_content\"\n" +
                "    android:hint=\"Email Address\"\n" +
                "    style=\"@style/Widget.Material3.TextInputLayout.OutlinedBox\">\n\n" +
                "    <com.google.android.material.textfield.TextInputEditText\n" +
                "        android:id=\"@+id/etEmail\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:inputType=\"textEmailAddress\" />\n" +
                "</com.google.android.material.textfield.TextInputLayout>\n\n" +
                "<com.google.android.material.textfield.TextInputLayout\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"wrap_content\"\n" +
                "    android:hint=\"Password\"\n" +
                "    app:passwordToggleEnabled=\"true\"\n" +
                "    android:layout_marginTop=\"12dp\"\n" +
                "    style=\"@style/Widget.Material3.TextInputLayout.OutlinedBox\">\n\n" +
                "    <com.google.android.material.textfield.TextInputEditText\n" +
                "        android:id=\"@+id/etPassword\"\n" +
                "        android:layout_width=\"match_parent\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:inputType=\"textPassword\" />\n" +
                "</com.google.android.material.textfield.TextInputLayout>\n\n" +
                "<com.google.android.material.button.MaterialButton\n" +
                "    android:id=\"@+id/btnLogin\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"56dp\"\n" +
                "    android:text=\"Sign In\"\n" +
                "    android:layout_marginTop=\"20dp\"\n" +
                "    app:cornerRadius=\"12dp\" />",
                "GitHub Copilot autocompletes XML tag attributes directly as you type in Android Studio. ChatGPT Codex generates complete, valid XML layouts with Material Design 3 guidelines."
        );

        // Scenario 2: Room Database Entity & DAO (Java)
        scenarios[1] = new ScenarioData(
                "GitHub Copilot",
                "IDE CODE VIEWER • ROOM DATABASE (JAVA)",
                "// DEVELOPER CONTEXT / PROMPT:",
                "Generate an Android Room Database Entity class for 'Student' with auto-generated ID, name, enrollment, and a Room DAO interface with CRUD operations.",
                "// SIMULATED AI GENERATED OUTPUT (JAVA):",
                "// Room Entity Class\n" +
                "@Entity(tableName = \"students\")\n" +
                "public class Student {\n" +
                "    @PrimaryKey(autoGenerate = true)\n" +
                "    public int id;\n\n" +
                "    @ColumnInfo(name = \"student_name\")\n" +
                "    public String name;\n\n" +
                "    @ColumnInfo(name = \"enrollment_no\")\n" +
                "    public String enrollment;\n" +
                "}\n\n" +
                "// Room DAO Interface\n" +
                "@Dao\n" +
                "public interface StudentDao {\n" +
                "    @Insert\n" +
                "    void insertStudent(Student student);\n\n" +
                "    @Query(\"SELECT * FROM students ORDER BY id DESC\")\n" +
                "    List<Student> getAllStudents();\n\n" +
                "    @Delete\n" +
                "    void deleteStudent(Student student);\n" +
                "}",
                "Copilot analyzes the Entity class in your active Android Studio tab and automatically suggests matching DAO query methods with proper SQL syntax when you write the interface name."
        );

        // Scenario 3: Retrofit REST API Client (Java Call)
        scenarios[2] = new ScenarioData(
                "Copilot & Codex",
                "IDE CODE VIEWER • RETROFIT NETWORKING (JAVA)",
                "// DEVELOPER CONTEXT / PROMPT:",
                "Write a Retrofit interface and async enqueue call to fetch a list of user profiles from 'https://jsonplaceholder.typicode.com/users'.",
                "// SIMULATED AI GENERATED OUTPUT (JAVA):",
                "// 1. Retrofit API Service Interface\n" +
                "public interface ApiService {\n" +
                "    @GET(\"users\")\n" +
                "    Call<List<User>> getUsers();\n" +
                "}\n\n" +
                "// 2. Asynchronous Execution in Activity\n" +
                "ApiService apiService = RetrofitClient.getClient().create(ApiService.class);\n" +
                "apiService.getUsers().enqueue(new Callback<List<User>>() {\n" +
                "    @Override\n" +
                "    public void onResponse(Call<List<User>> call, Response<List<User>> response) {\n" +
                "        if (response.isSuccessful() && response.body() != null) {\n" +
                "            List<User> userList = response.body();\n" +
                "            adapter.updateData(userList);\n" +
                "        }\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public void onFailure(Call<List<User>> call, Throwable t) {\n" +
                "        Log.e(\"API_ERROR\", \"Failed to fetch: \" + t.getMessage());\n" +
                "    }\n" +
                "});",
                "Typing 'apiService.getUsers().enqueue(new Callback' allows Copilot to auto-fill both onResponse() and onFailure() callbacks with proper null-safety checks in one Tab stroke."
        );

        // Scenario 4: Fix Android NullPointerException Crash
        scenarios[3] = new ScenarioData(
                "ChatGPT Codex",
                "IDE CODE VIEWER • DEBUGGING & LOGCAT FIX",
                "// DEVELOPER CONTEXT / LOGCAT TRACE:",
                "Diagnose Logcat crash: 'java.lang.NullPointerException: Attempt to invoke virtual method void android.widget.TextView.setText(java.lang.CharSequence) on a null object reference'.",
                "// SIMULATED AI GENERATED DIAGNOSIS & REPAIRED CODE:",
                "// DIAGNOSIS:\n" +
                "// View was referenced before setContentView(), or ID did not match layout XML!\n\n" +
                "@Override\n" +
                "protected void onCreate(Bundle savedInstanceState) {\n" +
                "    super.onCreate(savedInstanceState);\n\n" +
                "    // FIX 1: Ensure setContentView() is called FIRST!\n" +
                "    setContentView(R.layout.activity_main);\n\n" +
                "    // FIX 2: Initialize views after layout inflation\n" +
                "    TextView tvTitle = findViewById(R.id.tvTitle);\n\n" +
                "    // FIX 3: Safe defensive null-check\n" +
                "    if (tvTitle != null) {\n" +
                "        tvTitle.setText(\"Welcome to Android Studio!\");\n" +
                "    } else {\n" +
                "        Log.e(\"DEBUG\", \"View ID R.id.tvTitle not found in layout!\");\n" +
                "    }\n" +
                "}",
                "ChatGPT Codex excels at conversational debugging: when pasted a raw Logcat stack trace, it pinpoints the exact line number, explains the lifecycle cause, and writes corrected defensive code."
        );
    }

    private int getSelectedScenarioIndex() {
        int checkedId = rgScenarios.getCheckedRadioButtonId();
        if (checkedId == R.id.rbScenario2) return 1;
        if (checkedId == R.id.rbScenario3) return 2;
        if (checkedId == R.id.rbScenario4) return 3;
        return 0; // Default scenario 1
    }

    private void loadScenario(int index) {
        if (scenarios == null || index < 0 || index >= scenarios.length) return;
        ScenarioData data = scenarios[index];

        tvToolActiveBadge.setText(data.toolBadge);
        tvTerminalTitle.setText(data.terminalTitle);
        tvPromptHeader.setText(data.promptHeader);
        tvPromptText.setText(data.promptText);
        tvGeneratedCodeHeader.setText(data.codeHeader);
        tvGeneratedCode.setText(data.codeSnippet);
        tvAiInsight.setText(data.insight);
    }

    /**
     * Dynamic Status Bar Configurator:
     * - Dark background (e.g. #0F172A): Icons are WHITE (setAppearanceLightStatusBars(false))
     * - Light background (e.g. #FFFFFF): Icons are DARK/BLACK (setAppearanceLightStatusBars(true))
     */
    private void setupStatusBar(int statusBarColor, boolean isDarkBackground) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(statusBarColor);
        }
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(!isDarkBackground);
        }
    }
}
