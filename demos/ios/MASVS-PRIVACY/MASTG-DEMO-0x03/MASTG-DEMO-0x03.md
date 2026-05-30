---
platform: ios
title: Reviewing Privacy-Relevant Entitlements
id: MASTG-DEMO-0x03
code: [xml]
test: MASTG-TEST-0x03
---

## Sample

The sample below includes the `com.apple.developer.healthkit` entitlement, which allows the app to request user authorization for HealthKit access, which is a privacy-relevant capability. This entitlement does not by itself prove that the app has accessed health data, because HealthKit access still requires runtime authorization for specific data types. The entitlement should be reviewed together with the app’s Info.plist usage descriptions, linked HealthKit APIs, authorization request code, and any evidence of HealthKit data processing or transmission.

For this demo, we assume that the app does not have any health-related features, so this entitlement is unnecessary and represents a privacy concern.

{{ entitlements.plist # entitlements_reversed.plist }}

## Steps

1. Use @MASTG-TECH-0111 to extract the entitlements from the signed app bundle.
2. Run `run.sh` to print the relevant entitlements in a readable format.

{{ run.sh }}

## Observation

The output shows that the app enables several privacy-relevant entitlements:

{{ output.txt }}

## Evaluation

The test case fails because the app includes the `com.apple.developer.healthkit` entitlement. This entitlement allows the app to request user authorization for access to health and activity data stored in the Health app.

The entitlement alone does not prove that the app has accessed health data. The `com.apple.developer.healthkit` entitlement only enables the app to use the HealthKit framework and request authorization from the user. Actual access is controlled through `HKHealthStore.requestAuthorization(toShare:read:)`, where the app must explicitly specify the HealthKit data types it wants to read and/or write. Examples include workout data (`HKWorkoutType.workoutType()`), step count (`HKQuantityTypeIdentifier.stepCount`), heart rate (`HKQuantityTypeIdentifier.heartRate`), sleep analysis (`HKCategoryTypeIdentifier.sleepAnalysis`), active energy burned (`HKQuantityTypeIdentifier.activeEnergyBurned`), body mass (`HKQuantityTypeIdentifier.bodyMass`), and other specific HealthKit types. The user must grant permission for each requested read or write category, and access is limited to the approved data types. However, because HealthKit data is sensitive, the presence of this entitlement should be justified by clear health, fitness, or wellness functionality in the app.

If the app does not provide any health related feature, the entitlement appears unnecessary and should be removed. If the app does provide HealthKit functionality, the review should confirm that access is limited to the specific HealthKit data types needed, that authorization is requested only when needed, and that the purpose is clearly explained to the user.
