# Practical 16 — Read and Respond to Accelerometer Sensor Data

**Unit:** Advanced Android Features and App Deployment | **CO:** 4 | **Hrs:** 2

## Aim
To develop an Android application that reads and responds to data from mobile sensors such as the accelerometer.

## Theory
`SensorManager` provides access to device sensors. The accelerometer (`Sensor.TYPE_ACCELEROMETER`) returns X, Y, Z axis force values, useful for detecting tilt, shake, or orientation changes.

## Code
See [src/AccelerometerActivity.java](./src/AccelerometerActivity.java)

## Output
The screen live-updates X, Y, Z accelerometer readings; a "Shake detected!" Toast appears when the device is shaken vigorously.
