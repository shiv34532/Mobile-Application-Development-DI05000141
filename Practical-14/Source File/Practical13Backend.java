package com.example.arpitapplication;

import com.example.arpitapplication.R;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Practical13Backend extends AppCompatActivity {

    private EditText etTaskMessage, etCustomDelay;
    private RadioGroup rgDelay;
    private Spinner spTimeUnit;
    private LinearLayout layoutCustomDelay;
    private Button btnScheduleWork, btnCancelWork;
    private TextView tvWorkStateBadge, tvWorkId, tvStatusDetail;

    private UUID currentWorkId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical13_frontend);

        // Seamless Dynamic Status Bar: Indigo background with bright white icons (no white gap!)
        setupStatusBar(Color.parseColor("#4338CA"), true);

        // Safe Area Insets: Ensure header text starts safely below status bar & camera cutout
        View headerBanner = findViewById(R.id.p13HeaderBanner);
        if (headerBanner != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(headerBanner, (v, insets) -> {
                androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
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

        // Bind Views
        etTaskMessage = findViewById(R.id.etTaskMessage);
        etCustomDelay = findViewById(R.id.etCustomDelay);
        spTimeUnit = findViewById(R.id.spTimeUnit);
        rgDelay = findViewById(R.id.rgDelay);
        layoutCustomDelay = findViewById(R.id.layoutCustomDelay);
        btnScheduleWork = findViewById(R.id.btnScheduleWork);
        btnCancelWork = findViewById(R.id.btnCancelWork);
        tvWorkStateBadge = findViewById(R.id.tvWorkStateBadge);
        tvWorkId = findViewById(R.id.tvWorkId);
        tvStatusDetail = findViewById(R.id.tvStatusDetail);

        // Populate Time Unit Spinner (Seconds, Minutes, Hours)
        String[] timeUnits = {"Seconds (s)", "Minutes (min)", "Hours (hr)"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeUnits);
        spTimeUnit.setAdapter(unitAdapter);

        // Toggle custom delay input visibility
        rgDelay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbDelayCustom) {
                    layoutCustomDelay.setVisibility(View.VISIBLE);
                    etCustomDelay.requestFocus();
                } else {
                    layoutCustomDelay.setVisibility(View.GONE);
                }
            }
        });

        // Request POST_NOTIFICATIONS permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Schedule WorkManager Task
        btnScheduleWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleBackgroundTask();
            }
        });

        // Cancel WorkManager Task
        btnCancelWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentWorkId != null) {
                    WorkManager.getInstance(Practical13Backend.this).cancelWorkById(currentWorkId);
                    Toast.makeText(Practical13Backend.this, "Active task cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Dynamic Status Bar Configurator:
     * - Dark background (e.g. #4338CA): Icons are WHITE (setAppearanceLightStatusBars(false))
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

    private void scheduleBackgroundTask() {
        String message = etTaskMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            etTaskMessage.setError("Please enter a notification message");
            etTaskMessage.requestFocus();
            return;
        }

        // Determine selected delay and display text (Seconds, Minutes, Hours)
        long delaySeconds = 10;
        String delayDisplay = "10 seconds";
        int checkedId = rgDelay.getCheckedRadioButtonId();

        if (checkedId == R.id.rbDelay10s) {
            delaySeconds = 10;
            delayDisplay = "10 seconds";
        } else if (checkedId == R.id.rbDelay1m) {
            delaySeconds = 60;
            delayDisplay = "1 minute";
        } else if (checkedId == R.id.rbDelay5m) {
            delaySeconds = 300;
            delayDisplay = "5 minutes";
        } else if (checkedId == R.id.rbDelayCustom) {
            String customStr = etCustomDelay.getText().toString().trim();
            if (TextUtils.isEmpty(customStr)) {
                etCustomDelay.setError("Please enter duration");
                etCustomDelay.requestFocus();
                return;
            }
            long customVal;
            try {
                customVal = Long.parseLong(customStr);
                if (customVal <= 0) {
                    etCustomDelay.setError("Duration must be > 0");
                    etCustomDelay.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etCustomDelay.setError("Invalid number");
                etCustomDelay.requestFocus();
                return;
            }

            int unitPos = spTimeUnit.getSelectedItemPosition();
            if (unitPos == 1) { // Minutes
                delaySeconds = customVal * 60;
                delayDisplay = customVal + (customVal == 1 ? " minute" : " minutes");
            } else if (unitPos == 2) { // Hours
                delaySeconds = customVal * 3600;
                delayDisplay = customVal + (customVal == 1 ? " hour" : " hours");
            } else { // Seconds
                delaySeconds = customVal;
                delayDisplay = customVal + (customVal == 1 ? " second" : " seconds");
            }
        }

        // Pass message payload to Worker via InputData
        Data inputData = new Data.Builder()
                .putString("MESSAGE_KEY", message)
                .build();

        // Build OneTimeWorkRequest with initial delay
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
                .setInputData(inputData)
                .build();

        currentWorkId = workRequest.getId();

        // Enqueue task into WorkManager
        WorkManager.getInstance(this).enqueue(workRequest);

        tvWorkId.setText(currentWorkId.toString());
        btnCancelWork.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Task scheduled with " + delayDisplay + " delay! Close app to test.", Toast.LENGTH_LONG).show();

        // Live observation of WorkManager state
        observeWork(currentWorkId, delayDisplay);
    }

    private void observeWork(UUID workId, final String delayDisplay) {
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workId).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null) {
                    WorkInfo.State state = workInfo.getState();

                    switch (state) {
                        case ENQUEUED:
                            tvWorkStateBadge.setText("⏳ ENQUEUED");
                            tvWorkStateBadge.setBackgroundResource(R.drawable.bg_pill_blue);
                            tvWorkStateBadge.setTextColor(Color.parseColor("#1E40AF"));
                            tvStatusDetail.setText("Task is queued in WorkManager! Waiting " + delayDisplay + " before sending notification...");
                            break;

                        case RUNNING:
                            tvWorkStateBadge.setText("⚙️ RUNNING");
                            tvWorkStateBadge.setBackgroundResource(R.drawable.bg_pill_red);
                            tvWorkStateBadge.setTextColor(Color.parseColor("#991B1B"));
                            tvStatusDetail.setText("Background Worker is executing right now...");
                            break;

                        case SUCCEEDED:
                            tvWorkStateBadge.setText("✅ SUCCEEDED");
                            tvWorkStateBadge.setBackgroundResource(R.drawable.bg_pill_green);
                            tvWorkStateBadge.setTextColor(Color.parseColor("#065F46"));
                            tvStatusDetail.setText("Notification dispatched successfully to phone tray!");
                            btnCancelWork.setVisibility(View.GONE);
                            break;

                        case CANCELLED:
                            tvWorkStateBadge.setText("❌ CANCELLED");
                            tvWorkStateBadge.setBackgroundResource(R.drawable.bg_pill_gray);
                            tvWorkStateBadge.setTextColor(Color.parseColor("#64748B"));
                            tvStatusDetail.setText("Task was cancelled by user.");
                            btnCancelWork.setVisibility(View.GONE);
                            break;

                        case FAILED:
                            tvWorkStateBadge.setText("🔴 FAILED");
                            tvWorkStateBadge.setBackgroundResource(R.drawable.bg_pill_red);
                            tvWorkStateBadge.setTextColor(Color.parseColor("#991B1B"));
                            tvStatusDetail.setText("Task execution failed.");
                            btnCancelWork.setVisibility(View.GONE);
                            break;
                    }
                }
            }
        });
    }

    // ==========================================
    // BACKGROUND WORKER (Executes even if app is closed)
    // ==========================================
    public static class NotificationWorker extends Worker {

        public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            String message = getInputData().getString("MESSAGE_KEY");
            if (message == null || message.trim().isEmpty()) {
                message = "WorkManager delayed task completed!";
            }

            // Dispatch notification to user
            sendSystemNotification(getApplicationContext(), message);

            return Result.success();
        }

        private void sendSystemNotification(Context context, String message) {
            String channelId = "workmanager_alert_channel";
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Create notification channel for Android 8.0+ (Oreo)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "WorkManager Alerts",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notifications from background WorkManager tasks");
                channel.enableVibration(true);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }

            // Open Practical13Backend on notification tap
            Intent intent = new Intent(context, Practical13Backend.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("⏰ WorkManager Alert!")
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            if (manager != null) {
                manager.notify((int) System.currentTimeMillis(), builder.build());
            }
        }
    }
}
