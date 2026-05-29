---
title: Runtime Permission Usage Verification
platform: ios
id: MASTG-TEST-0x02
type: [dynamic]
weakness: MASWE-0117
profiles: [P]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0313.

While static analysis identifies declared purpose strings, dynamic analysis verifies which permissions the app actually uses at runtime. This helps identify whether the app requests permissions it doesn't use, or whether it accesses protected resources in unexpected ways.

iOS apps check authorization status before accessing protected resources using dedicated APIs from system frameworks. By tracing these APIs at runtime, you can understand which permissions are actually exercised during normal app usage.

## Steps

1. Identify the purpose strings declared by the app (see @MASTG-TEST-0313).
2. Map each purpose string to its corresponding system framework and authorization APIs:
    - Location: `CLLocationManager` methods such as `authorizationStatus`, `requestWhenInUseAuthorization`
    - Camera: `AVCaptureDevice.authorizationStatus(for:)`
    - Contacts: `CNContactStore.authorizationStatus(for:)`
    - Photos: `PHPhotoLibrary.authorizationStatus()`
    - Calendar: `EKEventStore.authorizationStatus(for:)`
    - Microphone: `AVAudioSession.recordPermission`
    - Health: `HKHealthStore.authorizationStatus(for:)`
3. Use @MASTG-TOOL-0001 to trace authorization-related methods.
4. Exercise the app's features that should trigger the identified permissions.

## Observation

The output should contain a list of authorization-related methods that were called during app usage, including:

- Method names and classes
- Return values (authorization status)
- Call stack (backtrace) to understand the context

## Evaluation

The test fails if:

- The app declares permissions it never uses at runtime, indicating unnecessary data collection declarations.
- The app accesses protected resources in contexts that don't match the stated purpose string.
- Authorization checks reveal the app has broader access than expected (e.g., "always" location instead of "when in use").

Cross-reference the runtime observations with the declared purpose strings to ensure the app only accesses resources it has legitimately declared and uses them appropriately.
