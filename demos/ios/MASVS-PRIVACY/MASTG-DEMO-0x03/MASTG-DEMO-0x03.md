---
platform: ios
title: Extracting Privacy-Relevant Entitlements from the App Code Signature
id: MASTG-DEMO-0x03
code: [swift, xml]
test: MASTG-TEST-0x03
---

## Sample

This sample uses the same app as @MASTG-DEMO-0x01. The app binary is signed with the additional `com.apple.developer.healthkit` entitlement, which allows the app to request user authorization for HealthKit access. The entitlement does not by itself prove that the app has accessed health data, because HealthKit access still requires runtime authorization for specific data types.

{{ ../MASTG-DEMO-0x01/MastgTest.swift }}

{{ ../MASTG-DEMO-0x01/Info.plist }}

{{ ../MASTG-DEMO-0x01/MASTestApp.entitlements # entitlements_reversed.plist }}

## Steps

1. Use @MASTG-TECH-0111 to extract the entitlements from the signed app bundle.
2. Run `run.sh` to print the extracted entitlements in a readable format.

{{ run.sh }}

## Observation

The output shows the entitlements embedded in the app:

{{ output.txt }}

## Evaluation

The test case fails because the app is signed with the `com.apple.developer.healthkit` entitlement, which may be excessive for the sample's shown functionality. The sample app does not present any health, fitness, or wellness feature that would justify enabling HealthKit.

This static entitlement finding should be evaluated against the app's stated functionality. Runtime use of the entitlement-backed APIs is covered separately in @MASTG-DEMO-0x04.
