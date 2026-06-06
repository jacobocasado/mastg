---
title: Runtime Use of Entitlement-Backed APIs
platform: ios
id: MASTG-TEST-0x04
type: [dynamic, hooks, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0077]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x03.

If an iOS app is signed with privacy-relevant entitlements but does not use the framework APIs, shared containers, or system entry points enabled by those entitlements, the app may carry unnecessary capability exposure.

This test verifies whether the app reaches entitlement-backed APIs at runtime and whether that runtime behavior supports the entitlements declared in the app's code signature.

Typical entitlement-backed APIs and entry points to monitor include:

- `HKHealthStore.requestAuthorization(toShare:read:completion:)`, `HKHealthStore.authorizationStatus(for:)`, `HKHealthStore.execute(_:)`, or `HKHealthStore.save(_:withCompletion:)` for HealthKit
- `FileManager.containerURL(forSecurityApplicationGroupIdentifier:)` or `UserDefaults(suiteName:)` for App Groups
- `CKContainer` APIs for iCloud and CloudKit containers
- `HMHomeManager` and `HMHomeManager.authorizationStatus` for HomeKit
- `NFCTagReaderSession` or `NFCNDEFReaderSession` for NFC tag reading
- `NSUserActivityTypeBrowsingWeb` continuation handlers for Associated Domains and Universal Links
- `NWConnectionGroup` for multicast networking

See @MASTG-KNOW-0077 for the mapping between Xcode capabilities, signed entitlements, and the framework APIs or entry points that use the corresponding service.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of entitlement-backed APIs or system entry points that were reached during app usage. For each observed call, record:

- Method names and classes
- The entitlement or capability being validated
- Relevant identifiers or arguments, such as HealthKit data types, App Group identifiers, iCloud container identifiers, or associated-domain activities
- Call stack (backtrace) to understand the context

## Evaluation

The test case fails if the app is signed with a privacy-relevant entitlement and runtime analysis shows that the app does not use the APIs or entry points that require that entitlement.

Examples include:

- The app is signed with the HealthKit entitlement but does not reach `HKHealthStore` APIs or HealthKit data types in any relevant flow.
- The app is signed with App Groups but does not use shared container APIs or suite defaults.
- The app is signed with iCloud container entitlements but does not use CloudKit, ubiquitous containers, or iCloud key-value storage.
