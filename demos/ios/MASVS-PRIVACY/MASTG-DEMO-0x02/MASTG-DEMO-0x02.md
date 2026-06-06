---
platform: ios
title: Runtime Permission Usage Verification with Frida
id: MASTG-DEMO-0x02
code: [swift]
test: MASTG-TEST-0x02
---

## Sample

This sample uses the same code as @MASTG-DEMO-0x01, which declares multiple protected-resource purpose strings in `Info.plist` and reaches authorization-related APIs for location, contacts, and photos. This demo uses Frida to trace those authorization APIs at runtime and compare them with the declared purpose strings.

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

A single run cannot prove that the camera permission is unused, since optional or dormant flows may exercise it later. Confirm the finding with static review as described in the test's "Further Validation Required" guidance before concluding the permission is unnecessary.
