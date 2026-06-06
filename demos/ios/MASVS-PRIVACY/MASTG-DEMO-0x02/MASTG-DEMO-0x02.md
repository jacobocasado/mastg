---
platform: ios
title: Runtime Permission Usage Verification with Frida
id: MASTG-DEMO-0x02
code: [swift]
test: MASTG-TEST-0x02
---

## Sample

This sample uses the same code as @MASTG-DEMO-0x01. The app declares purpose strings for location, contacts, photos, and camera in `Info.plist`. The app reaches authorization-related APIs for location, contacts, and photos when the **Start** button is tapped.

This runtime view complements the static `Info.plist` review from @MASTG-DEMO-0x01: purpose strings show what the app declares, while the runtime trace shows which authorization APIs are exercised in this run. In this sample, `NSCameraUsageDescription` is declared but no camera authorization API is triggered, which can be considered as an excessive permission and represent a privacy concern.

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

The test case fails because the app reaches protected-resource authorization APIs that must be cross-referenced with its declared purpose strings and actual features.

Compare these runtime calls with the purpose strings extracted in @MASTG-DEMO-0x01:

- `CLLocationManager.requestWhenInUseAuthorization` (location), `CNContactStore.authorizationStatusForEntityType` (contacts), and `PHPhotoLibrary.authorizationStatusForAccessLevel` (photos) are all reached at runtime, so those purpose strings map to code that actually runs.
- The location API called is `requestWhenInUseAuthorization`, which matches the declared `NSLocationWhenInUseUsageDescription`. If the app had called `requestAlwaysAuthorization` instead, the stricter `NSLocationAlwaysAndWhenInUseUsageDescription` would be required.
- `NSCameraUsageDescription` is declared in `Info.plist` (see @MASTG-DEMO-0x01) but no camera authorization API is reached at runtime. A declared permission that is never exercised is an indicator of a potentially unnecessary permission.

A single run cannot prove that the camera permission is unused, since optional or dormant flows may exercise it later. Confirm the finding with static review as described in @MASTG-TEST-0x02 "Further Validation Required" guidance before concluding the permission is unnecessary.
