package com.example.arpitapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Practical14Backend extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView tvLatitude, tvLongitude, tvAccuracy, tvAltitude, tvSpeed, tvProvider;
    private TextView tvAddress, tvGpsStatusBadge, tvGeocodeBadge;
    private Button btnGetLocation, btnOpenMaps, btnShareLocation;

    private LocationManager locationManager;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private String resolvedAddressText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical14_frontend);

        // Seamless Dynamic Status Bar: Royal Blue (#2563EB) with bright white icons
        setupStatusBar(Color.parseColor("#2563EB"), true);

        // Safe Area Insets: Ensure header text starts safely below status bar & camera cutout
        View headerBanner = findViewById(R.id.p14HeaderBanner);
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
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvAltitude = findViewById(R.id.tvAltitude);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvProvider = findViewById(R.id.tvProvider);
        tvAddress = findViewById(R.id.tvAddress);
        tvGpsStatusBadge = findViewById(R.id.tvGpsStatusBadge);
        tvGeocodeBadge = findViewById(R.id.tvGeocodeBadge);

        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnOpenMaps = findViewById(R.id.btnOpenMaps);
        btnShareLocation = findViewById(R.id.btnShareLocation);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Fetch location button
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndFetchLocation();
            }
        });

        // Open in Google Maps button
        btnOpenMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps();
            }
        });

        // Share Location button
        btnShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCurrentLocation();
            }
        });

        // Automatically request on launch
        checkPermissionAndFetchLocation();
    }

    /**
     * Checks runtime location permissions and requests if needed.
     */
    private void checkPermissionAndFetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            startLocationRetrieval();
        }
    }

    /**
     * Checks if GPS is turned ON and fetches the real device location.
     */
    private void startLocationRetrieval() {
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            showGpsDisabledDialog();
            return;
        }

        tvGpsStatusBadge.setText("🛰️ Acquiring Fix...");
        tvGpsStatusBadge.setBackgroundResource(R.drawable.bg_pill_blue);
        tvGpsStatusBadge.setTextColor(Color.parseColor("#1E40AF"));
        Toast.makeText(this, "Acquiring GPS location fix...", Toast.LENGTH_SHORT).show();

        try {
            // First, retrieve last known location for instant display
            Location bestLocation = null;
            if (isGpsEnabled) {
                Location gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (gpsLoc != null) bestLocation = gpsLoc;
            }
            if (isNetworkEnabled) {
                Location netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (netLoc != null && (bestLocation == null || netLoc.getTime() > bestLocation.getTime())) {
                    bestLocation = netLoc;
                }
            }

            if (bestLocation != null) {
                updateLocationUI(bestLocation);
            }

            // Request fresh live updates
            if (isGpsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, this);
            } else if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 2, this);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Location permission error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the UI elements with new location data.
     */
    private void updateLocationUI(Location location) {
        if (location == null) return;

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        tvLatitude.setText(String.format(Locale.getDefault(), "%.6f°", currentLatitude));
        tvLongitude.setText(String.format(Locale.getDefault(), "%.6f°", currentLongitude));

        if (location.hasAccuracy()) {
            tvAccuracy.setText(String.format(Locale.getDefault(), "± %.1f meters", location.getAccuracy()));
        } else {
            tvAccuracy.setText("Estimated");
        }

        if (location.hasAltitude()) {
            tvAltitude.setText(String.format(Locale.getDefault(), "%.1f meters (sea level)", location.getAltitude()));
        } else {
            tvAltitude.setText("Unavailable");
        }

        if (location.hasSpeed()) {
            double speedKmh = location.getSpeed() * 3.6; // Convert m/s to km/h
            tvSpeed.setText(String.format(Locale.getDefault(), "%.1f km/h", speedKmh));
        } else {
            tvSpeed.setText("Stationary (0.0 km/h)");
        }

        String providerName = location.getProvider();
        if (providerName != null) {
            tvProvider.setText(providerName.toUpperCase());
        }

        tvGpsStatusBadge.setText("🟢 GPS Locked");
        tvGpsStatusBadge.setBackgroundResource(R.drawable.bg_pill_green);
        tvGpsStatusBadge.setTextColor(Color.parseColor("#065F46"));

        // Reverse geocode to resolve street address asynchronously
        resolveStreetAddress(currentLatitude, currentLongitude);
    }

    /**
     * Uses Android Geocoder to convert (Latitude, Longitude) into real street address.
     */
    private void resolveStreetAddress(double lat, double lng) {
        tvAddress.setText("Resolving street address from coordinates...");
        tvGeocodeBadge.setText("Resolving...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(Practical14Backend.this, Locale.getDefault());
                String addressString = "Address could not be determined.";
                boolean success = false;

                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        StringBuilder sb = new StringBuilder();

                        // Build clean address format
                        if (address.getAddressLine(0) != null) {
                            sb.append(address.getAddressLine(0));
                        } else {
                            if (address.getFeatureName() != null) sb.append(address.getFeatureName()).append(", ");
                            if (address.getSubLocality() != null) sb.append(address.getSubLocality()).append(", ");
                            if (address.getLocality() != null) sb.append(address.getLocality()).append(", ");
                            if (address.getAdminArea() != null) sb.append(address.getAdminArea()).append(" - ");
                            if (address.getPostalCode() != null) sb.append(address.getPostalCode());
                        }

                        addressString = sb.toString();
                        success = true;
                    }
                } catch (IOException e) {
                    addressString = "Coordinates: (" + String.format(Locale.getDefault(), "%.4f", lat) + ", "
                            + String.format(Locale.getDefault(), "%.4f", lng) + ")\n(Connect to Internet to resolve full street address)";
                }

                final String finalAddress = addressString;
                final boolean finalSuccess = success;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resolvedAddressText = finalAddress;
                        tvAddress.setText(finalAddress);
                        if (finalSuccess) {
                            tvGeocodeBadge.setText("✅ Resolved");
                            tvGeocodeBadge.setBackgroundResource(R.drawable.bg_pill_green);
                            tvGeocodeBadge.setTextColor(Color.parseColor("#065F46"));
                        } else {
                            tvGeocodeBadge.setText("Offline");
                            tvGeocodeBadge.setBackgroundResource(R.drawable.bg_pill_gray);
                            tvGeocodeBadge.setTextColor(Color.parseColor("#64748B"));
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Opens Google Maps app (or browser) with a pin at the exact GPS coordinates.
     */
    private void openGoogleMaps() {
        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            Toast.makeText(this, "Please fetch your GPS location first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // URI with pin label
        String uriString = "geo:" + currentLatitude + "," + currentLongitude + "?q=" + currentLatitude + "," + currentLongitude + "(My+Location)";
        Uri gmmIntentUri = Uri.parse(uriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback to web browser Google Maps
            String webUri = "https://www.google.com/maps/search/?api=1&query=" + currentLatitude + "," + currentLongitude;
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
            startActivity(webIntent);
        }
    }

    /**
     * Shares current coordinates & street address via Android Share Sheet (WhatsApp, SMS, etc.).
     */
    private void shareCurrentLocation() {
        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            Toast.makeText(this, "No location data to share yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String shareBody = "📍 My Current Location (Arpit):\n"
                + "• Latitude: " + currentLatitude + "\n"
                + "• Longitude: " + currentLongitude + "\n"
                + "• Address: " + resolvedAddressText + "\n"
                + "• Google Maps: https://maps.google.com/?q=" + currentLatitude + "," + currentLongitude;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Current GPS Location");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Share Location Via"));
    }

    /**
     * Dialog shown when device GPS / Location switch is turned OFF.
     */
    private void showGpsDisabledDialog() {
        new AlertDialog.Builder(this)
                .setTitle("📍 Location Services Disabled")
                .setMessage("GPS / Location is currently turned off on your device. Please turn it ON to retrieve your real-time coordinates.")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted!", Toast.LENGTH_SHORT).show();
                startLocationRetrieval();
            } else {
                Toast.makeText(this, "Permission denied. Cannot retrieve GPS location.", Toast.LENGTH_LONG).show();
                tvGpsStatusBadge.setText("❌ Denied");
                tvGpsStatusBadge.setBackgroundResource(R.drawable.bg_pill_red);
                tvGpsStatusBadge.setTextColor(Color.parseColor("#991B1B"));
            }
        }
    }

    // ==========================================
    // LocationListener Callbacks
    // ==========================================
    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateLocationUI(location);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        tvGpsStatusBadge.setText("🛰️ GPS Enabled");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        tvGpsStatusBadge.setText("⚠️ GPS Disabled");
        tvGpsStatusBadge.setBackgroundResource(R.drawable.bg_pill_gray);
        tvGpsStatusBadge.setTextColor(Color.parseColor("#64748B"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop updates to preserve battery
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationRetrieval();
        }
    }

    /**
     * Dynamic Status Bar Configurator:
     * - Dark background (e.g. #2563EB): Icons are WHITE (setAppearanceLightStatusBars(false))
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
