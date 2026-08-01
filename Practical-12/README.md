# Practical 12 — REST API Data using Retrofit + JSON Parsing

**Unit:** Data Management, Networking and Deployment | **CO:** 3 | **Hrs:** 2

## Aim
To develop an Android application that fetches and displays data from a REST API using JSON parsing with the help of the Retrofit library.

## Dependencies (app/build.gradle)
```gradle
implementation "com.squareup.retrofit2:retrofit:2.11.0"
implementation "com.squareup.retrofit2:converter-gson:2.11.0"
```

## AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Code
See [src/User.java](./src/User.java), [src/ApiService.java](./src/ApiService.java), [src/RetrofitClient.java](./src/RetrofitClient.java), [src/ApiActivity.java](./src/ApiActivity.java)

## Output
The app calls `https://jsonplaceholder.typicode.com/users` and displays a list of user names & emails in a RecyclerView.
