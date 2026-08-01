# Practical 10 — Navigation between Activities + SharedPreferences

**Unit:** Data Management, Networking and Deployment | **CO:** 3 | **Hrs:** 2

## Aim
To navigate between multiple activities and perform store-retrieve operations of simple data in Android using SharedPreferences.

## Theory
`SharedPreferences` stores primitive key-value data persistently (survives app restarts). Activities navigate using `Intent` objects, optionally carrying data via `putExtra`.

## Code
See [src/FirstActivity.java](./src/FirstActivity.java), [src/SecondActivity.java](./src/SecondActivity.java)

## Output
FirstActivity takes a username, saves it to SharedPreferences, and navigates to SecondActivity, which reads and displays the stored username.
