---
platform: ios
title: Runtime Use of Entitlement-Backed APIs with Frida
id: MASTG-DEMO-0x04
code: [swift, xml]
test: MASTG-TEST-0x04
---

## Sample

This sample uses the same app as @MASTG-DEMO-0x01. The app is signed with the `com.apple.developer.healthkit` entitlement, but the Swift code does not import HealthKit, instantiate `HKHealthStore`, or request access to HealthKit data types.

The static entitlement extraction in @MASTG-DEMO-0x03 shows what the app declares in its code signature. This runtime demo traces representative HealthKit APIs associated with that entitlement while exercising the app and verifies if the related APIs are called.

{{ ../MASTG-DEMO-0x01/MastgTest.swift }}

{{ ../MASTG-DEMO-0x01/MASTestApp.entitlements # ../MASTG-DEMO-0x03/entitlements_reversed.plist }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Tap the **Start** button to exercise the sample flow.
5. Stop the script by pressing `Ctrl+C`.

{{ script.js # run.sh }}

## Observation

The output shows the HealthKit runtime hooks or class lookup result captured while exercising the app:

{{ output.txt }}

## Evaluation

The test case fails because the app is signed with the `com.apple.developer.healthkit` entitlement, but the exercised runtime flow does not show any HealthKit API use such as `HKHealthStore`.