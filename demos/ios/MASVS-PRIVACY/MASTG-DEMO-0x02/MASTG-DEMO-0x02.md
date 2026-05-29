---
platform: ios
title: Runtime Permission Usage Verification with Frida
id: MASTG-DEMO-0x02
code: [swift]
test: MASTG-TEST-0x02
---

## Sample

This demo uses the same sample as @MASTG-DEMO-0x01.

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

- `CLLocationManager.authorizationStatus` was called, returning `authorizedWhenInUse` (4).
- `CNContactStore.authorizationStatusForEntityType` was called, returning `authorized` (3).
- `PHPhotoLibrary.authorizationStatus` was called, returning `authorized` (3).

## Evaluation

The test fails because the app actively checks all declared permissions at runtime.

Cross-reference these runtime calls with the purpose strings from @MASTG-DEMO-0x01 to verify:

1. Every permission declared in `Info.plist` is actually used.
2. The authorization levels match expected behavior (e.g., location is "when in use" not "always").
3. Permissions are checked in appropriate contexts aligned with the app's stated purpose.

If any declared permissions are never checked at runtime, they may be unnecessarily declared. If permissions are checked in unexpected contexts, this may indicate privacy concerns.
