package com.example.arpitapplication;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Practical17Backend extends AppCompatActivity {

    private TextView tvAppId, tvVersionInfo, tvBuildVariant, tvSdkInfo;
    private TextView tvApkSize, tvInstallDate, tvApkPath;
    private Button btnRefreshInspector, btnShareApk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical17_frontend);

        // Dynamic Status Bar: Emerald Dark (#064E3B) with white icons
        setupStatusBar(Color.parseColor("#064E3B"), true);

        // Safe Area Insets: Ensure header text starts safely below status bar & camera cutout
        View headerBanner = findViewById(R.id.p17HeaderBanner);
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
        tvAppId = findViewById(R.id.tvAppId);
        tvVersionInfo = findViewById(R.id.tvVersionInfo);
        tvBuildVariant = findViewById(R.id.tvBuildVariant);
        tvSdkInfo = findViewById(R.id.tvSdkInfo);
        tvApkSize = findViewById(R.id.tvApkSize);
        tvInstallDate = findViewById(R.id.tvInstallDate);
        tvApkPath = findViewById(R.id.tvApkPath);
        btnRefreshInspector = findViewById(R.id.btnRefreshInspector);
        btnShareApk = findViewById(R.id.btnShareApk);

        // Load Live Package Metadata
        inspectLiveApk();

        // Refresh Inspector Button
        btnRefreshInspector.setOnClickListener(v -> {
            inspectLiveApk();
            Toast.makeText(this, "🔄 Live APK Inspector Refreshed!", Toast.LENGTH_SHORT).show();
        });

        // Share / Export APK Button
        btnShareApk.setOnClickListener(v -> shareInstalledApk());
    }

    /**
     * Inspects the running application's own package directly from the Android PackageManager.
     */
    private void inspectLiveApk() {
        try {
            PackageManager pm = getPackageManager();
            String packageName = getPackageName();
            PackageInfo pInfo = pm.getPackageInfo(packageName, 0);
            ApplicationInfo appInfo = getApplicationInfo();

            // 1. Application ID
            tvAppId.setText(packageName);

            // 2. Version Code & Version Name
            long versionCode = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    ? pInfo.getLongVersionCode()
                    : pInfo.versionCode;
            tvVersionInfo.setText(String.format(Locale.getDefault(), "%s (Build Code: %d)", pInfo.versionName, versionCode));

            // 3. Build Variant & Debuggable Flag
            boolean isDebuggable = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            if (isDebuggable) {
                tvBuildVariant.setText("🟢 DEBUG (Debug Keystore Signed)");
            } else {
                tvBuildVariant.setText("🛡️ RELEASE (Signed Production APK)");
            }

            // 4. Target & Min SDK
            int targetSdk = appInfo.targetSdkVersion;
            int minSdk = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    ? appInfo.minSdkVersion
                    : 21;
            tvSdkInfo.setText(String.format(Locale.getDefault(), "Target: API %d | Min: API %d", targetSdk, minSdk));

            // 5. Source APK Path & Live File Size
            String sourceDir = appInfo.sourceDir;
            tvApkPath.setText(sourceDir);

            File apkFile = new File(sourceDir);
            if (apkFile.exists()) {
                long sizeBytes = apkFile.length();
                double sizeMb = sizeBytes / (1024.0 * 1024.0);
                tvApkSize.setText(String.format(Locale.getDefault(), "%.2f MB (%s bytes)", sizeMb, String.format(Locale.getDefault(), "%,d", sizeBytes)));
            } else {
                tvApkSize.setText("Source APK accessible");
            }

            // 6. First Install Date & Time
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            String installFormatted = sdf.format(new Date(pInfo.firstInstallTime));
            tvInstallDate.setText(installFormatted);

        } catch (Exception e) {
            tvApkSize.setText("Error reading package info: " + e.getMessage());
        }
    }

    /**
     * Copies the installed base.apk to the app's cache directory and shares it via Android FileProvider.
     */
    private void shareInstalledApk() {
        Toast.makeText(this, "📦 Preparing APK for export...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                ApplicationInfo appInfo = getApplicationInfo();
                File srcApk = new File(appInfo.sourceDir);

                if (!srcApk.exists()) {
                    runOnUiThread(() -> Toast.makeText(this, "Source APK not found", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Copy to cache dir with a clean, branded APK filename
                File cacheDir = getCacheDir();
                File exportApk = new File(cacheDir, "ArpitApp_Exported.apk");

                InputStream in = new FileInputStream(srcApk);
                OutputStream out = new FileOutputStream(exportApk);
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                in.close();
                out.close();

                // Generate Content URI via FileProvider
                Uri apkUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        exportApk
                );

                // Launch Android Share Chooser
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/vnd.android.package-archive");
                shareIntent.putExtra(Intent.EXTRA_STREAM, apkUri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ArpitApplication APK");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is the exported APK from Arpit's Mobile Application Development project!");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                runOnUiThread(() -> {
                    startActivity(Intent.createChooser(shareIntent, "Export & Share APK via..."));
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Export Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    /**
     * Dynamic Status Bar Configurator:
     * - Dark background (e.g. #064E3B): Icons are WHITE (setAppearanceLightStatusBars(false))
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
