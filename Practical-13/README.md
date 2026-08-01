# Practical 13 — Firebase Integration (Cloud Storage / Authentication)

**Unit:** Data Management, Networking and Deployment | **CO:** 3 | **Hrs:** 2

## Aim
To integrate Firebase with an Android application for cloud data storage or authentication.

## Steps
1. Go to [Firebase Console](https://console.firebase.google.com) → Create Project.
2. Register the Android app with your package name and download `google-services.json` into `app/`.
3. Add to project-level `build.gradle`:
```gradle
classpath 'com.google.gms:google-services:4.4.2'
```
4. Add to app-level `build.gradle`:
```gradle
apply plugin: 'com.google.gms.google-services'
implementation platform('com.google.firebase:firebase-bom:33.1.0')
implementation 'com.google.firebase:firebase-auth-ktx'
implementation 'com.google.firebase:firebase-firestore-ktx'
```

## Code
See [src/FirebaseAuthActivity.java](./src/FirebaseAuthActivity.java)

## Output
Users can sign up / log in with email & password via Firebase Authentication, and their data is stored in Cloud Firestore.
