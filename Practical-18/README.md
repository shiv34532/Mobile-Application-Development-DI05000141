# Practical 18 — Generate and Export APK File

**Unit:** Advanced Android Features and App Deployment | **CO:** 4 | **Hrs:** 2

## Aim
To generate and export an APK file from an Android project for application deployment.

## Steps to Generate a Debug APK
1. Open the project in Android Studio.
2. Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
3. Wait for Gradle build to finish; a notification "APK(s) generated successfully" appears.
4. Click **locate** to find `app-debug.apk` inside `app/build/outputs/apk/debug/`.

## Steps to Generate a Signed Release APK
1. Go to **Build → Generate Signed Bundle / APK**.
2. Choose **APK** → Next.
3. Click **Create new...** to generate a new keystore (or select an existing one):
   - Fill Key store path, password, alias, and validity (25+ years).
4. Select **release** build variant → check **V1 and V2 signature versions**.
5. Click **Finish**; the signed APK is generated at `app/release/app-release.apk`.

## Verifying the APK
```bash
# Install directly on a connected device/emulator
adb install app-release.apk
```

## Output
A distributable `.apk` file is generated, ready to be installed on Android devices or uploaded to the Google Play Console.
