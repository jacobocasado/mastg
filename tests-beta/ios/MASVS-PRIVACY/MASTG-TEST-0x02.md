---
title: Runtime Use of Protected Resource Authorization APIs
platform: ios
id: MASTG-TEST-0x02
type: [dynamic, hooks, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0077]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x01.

If an iOS app checks or requests access to protected resources in contexts that do not match its declared purpose strings or feature set, it may access personal data unexpectedly. This test verifies which authorization APIs the app actually reaches at runtime and whether those calls match the declared purpose strings and user-visible functionality.

Typical authorization APIs to monitor include:

- `CLLocationManager.requestWhenInUseAuthorization()`, `CLLocationManager.requestAlwaysAuthorization()`, or `CLLocationManager.authorizationStatus()`
- `AVCaptureDevice.requestAccess(for:completionHandler:)` or `AVCaptureDevice.authorizationStatus(for:)`
- `CNContactStore.requestAccess(for:completionHandler:)` or `CNContactStore.authorizationStatus(for:)`
- `PHPhotoLibrary.requestAuthorization(for:handler:)` or `PHPhotoLibrary.authorizationStatus(for:)`
- `HKHealthStore.requestAuthorization(toShare:read:completion:)` or `HKHealthStore.authorizationStatus(for:)`
- `CBManager.authorization`

See @MASTG-KNOW-0077 for the mapping between protected resources, purpose string keys, and framework APIs.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of authorization-related methods that were called during app usage. For each observed call, record:

- Method names and classes
- Return values (authorization status)
- Call stack (backtrace) to understand the context

## Evaluation

The test case fails if runtime traces show permission checks or requests that do not match the app's declared features or its stated purpose strings.

Examples include:

- The app requests or checks access for a protected resource in a feature that users would not reasonably expect.
- The app requests broader access than needed, for example "always" location access when "when in use" would suffice.
- The backtrace shows sensitive resource access in code paths that are unrelated to the feature described in the purpose string.

**Further Validation Required:**

Use the observed backtraces to inspect the relevant code and determine:

- whether the traced authorization calls lead to actual access to the protected resource,
- whether the surrounding feature genuinely requires that access, and
- whether the app could use a narrower or user-selected alternative instead.

