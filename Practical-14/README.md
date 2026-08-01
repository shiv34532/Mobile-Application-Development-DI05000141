# Practical 14 — Background Task using WorkManager + Notification

**Unit:** Advanced Android Features and App Deployment | **CO:** 4 | **Hrs:** 2

## Aim
To perform a background task using WorkManager and display a notification or message after a delay.

## Dependencies (app/build.gradle)
```gradle
implementation "androidx.work:work-runtime-ktx:2.9.1"
```

## Code
See [src/NotificationWorker.java](./src/NotificationWorker.java), [src/WorkManagerActivity.java](./src/WorkManagerActivity.java)

## Output
Tapping a button schedules a background worker; after a 10-second delay, a system notification appears titled "Task Complete".
