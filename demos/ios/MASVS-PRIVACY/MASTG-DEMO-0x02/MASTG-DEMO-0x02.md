---
platform: ios
title: Runtime Use of Protected Resource Authorization APIs with Frida
id: MASTG-DEMO-0x02
code: [swift]
test: MASTG-TEST-0x02
---

## Sample

This sample uses the same app as @MASTG-DEMO-0x01. The app declares purpose strings for location, contacts, photos, and camera in `Info.plist`. The app reaches authorization-related APIs for location, contacts, and photos when the **Start** button is tapped. In this sample, `NSCameraUsageDescription` is declared but no camera authorization API is triggered.

This runtime analysis complements the static `Info.plist` review from @MASTG-DEMO-0x01: purpose strings show what the app declares, while the runtime analysis shows which authorization APIs are exercised in this run.

{{ ../MASTG-DEMO-0x01/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn your app with Frida.
4. Click the **Start** button to trigger the permission checks.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output reveals the authorization APIs being called at runtime:

{{ output.txt }}

The trace shows:

- `CLLocationManager.requestWhenInUseAuthorization` was called, confirming the app actively requests location access at runtime.
- `CNContactStore.authorizationStatusForEntityType` was called, returning `NotDetermined` (0).
- `PHPhotoLibrary.authorizationStatusForAccessLevel` was called, returning `NotDetermined` (0).

## Evaluation

The test case fails because the app declares purpose strings but no related authorization API is triggered at runtime.

Compare these runtime calls with the purpose strings extracted in @MASTG-DEMO-0x01:

- `CLLocationManager.requestWhenInUseAuthorization` (location), `CNContactStore.authorizationStatusForEntityType` (contacts), and `PHPhotoLibrary.authorizationStatusForAccessLevel` (photos) are all reached at runtime, so those purpose strings map to code that actually runs.
- The location API called is `requestWhenInUseAuthorization`, which matches the declared `NSLocationWhenInUseUsageDescription`. If the app had called `requestAlwaysAuthorization` instead, the stricter `NSLocationAlwaysAndWhenInUseUsageDescription` would be required.
- `NSCameraUsageDescription` is declared in `Info.plist` (see @MASTG-DEMO-0x01) but no camera authorization API is reached in this run. A declared permission that is not exercised in the tested flow is an indicator of a potentially unnecessary permission.
