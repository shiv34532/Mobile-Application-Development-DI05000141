# Practical 15 — Retrieve Device's Current Location using GPS

**Unit:** Advanced Android Features and App Deployment | **CO:** 4 | **Hrs:** 2

## Aim
To develop an Android application that retrieves and displays the device's current location using GPS.

## Dependencies (app/build.gradle)
```gradle
implementation "com.google.android.gms:play-services-location:21.3.0"
```

## AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## Code
See [src/LocationActivity.java](./src/LocationActivity.java)

## Output
The app requests location permission, then displays the device's current latitude and longitude on screen.
