# Practical 11 — CRUD Operations using Room Database

**Unit:** Data Management, Networking and Deployment | **CO:** 3 | **Hrs:** 2

## Aim
To perform Create, Read, Update, and Delete (CRUD) operations using the Room database library in Android.

## Theory
Room is an abstraction layer over SQLite that provides compile-time verified SQL queries via annotations: `@Entity`, `@Dao`, `@Database`.

## Dependencies (app/build.gradle)
```gradle
implementation "androidx.room:room-runtime:2.6.1"
annotationProcessor "androidx.room:room-compiler:2.6.1"
```

## Code
See [src/Note.java](./src/Note.java), [src/NoteDao.java](./src/NoteDao.java), [src/AppDatabase.java](./src/AppDatabase.java), [src/RoomActivity.java](./src/RoomActivity.java)

## Output
An app that lets the user add, view, update, and delete notes stored persistently in a local SQLite database via Room.
