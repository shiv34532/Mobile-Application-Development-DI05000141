# Practical 01 — Android Studio Setup & First App

**Unit:** Mobile Application and Development Tools | **CO:** 1 | **Hrs:** 2

## Aim
To install Android Studio IDE and create, build, and run a basic Android application using an emulator or a real device.

## Theory
Android Studio is the official IDE for Android development, built on JetBrains' IntelliJ IDEA. It bundles the Android SDK, an emulator (AVD Manager), Gradle build system, and a visual layout editor.

## Steps
1. Download Android Studio from https://developer.android.com/studio and install it.
2. During setup, install the Android SDK, SDK Platform-Tools, and an Android Virtual Device (AVD).
3. Open Android Studio → **New Project** → select **Empty Views Activity** → set:
   - Name: `HelloWorldApp`
   - Package name: `com.gpb.helloworldapp`
   - Language: Java
   - Minimum SDK: API 21
4. Click **Finish** and let Gradle sync.
5. Edit `activity_main.xml` and `MainActivity.java` (see below).
6. Create an AVD (Pixel 6, API 34) via **Device Manager**, or connect a real device with USB debugging enabled.
7. Click **Run ▶** to build and launch the app.

## Code

### layout/activity_main.xml
See [layout/activity_main.xml](./layout/activity_main.xml)

### src/MainActivity.java
See [src/MainActivity.java](./src/MainActivity.java)

## Output
A basic app launches showing "Hello, Android World!" with a button that shows a Toast message on click.
