---
title: Purpose Strings for Unjustified Protected Resource Access
platform: ios
id: MASTG-TEST-0x01
type: [static, config, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0077]
---

## Overview

Purpose strings are user-facing explanations that iOS displays when an app requests [access to protected resources](https://developer.apple.com/documentation/uikit/requesting-access-to-protected-resources) such as location, camera, microphone, contacts, photos, health data, Bluetooth, motion, or speech recognition. Unlike entitlements, purpose strings are tied to privacy-sensitive protected resources and runtime authorization prompts.

Usage description keys and runtime APIs are separate parts of the iOS permission model. Usage description keys provide the explanation in `Info.plist`, while framework APIs request or check authorization and access the protected resource.

| Protected resource | Usage description key | Representative APIs |
| --- | --- | --- |
| Location | `NSLocationWhenInUseUsageDescription`, `NSLocationAlwaysAndWhenInUseUsageDescription` | `CLLocationManager.requestWhenInUseAuthorization()`, `CLLocationManager.requestAlwaysAuthorization()`, `CLLocationManager.authorizationStatus()` |
| Camera | `NSCameraUsageDescription` | `AVCaptureDevice.requestAccess(for:completionHandler:)`, `AVCaptureDevice.authorizationStatus(for:)` |
| Contacts | `NSContactsUsageDescription` | `CNContactStore.requestAccess(for:completionHandler:)`, `CNContactStore.authorizationStatus(for:)` |
| Photos | `NSPhotoLibraryUsageDescription`, `NSPhotoLibraryAddUsageDescription` | `PHPhotoLibrary.requestAuthorization(for:handler:)`, `PHPhotoLibrary.authorizationStatus(for:)` |
| Health | `NSHealthShareUsageDescription`, `NSHealthUpdateUsageDescription` | `HKHealthStore.requestAuthorization(toShare:read:completion:)`, `HKHealthStore.authorizationStatus(for:)` |
| Bluetooth | `NSBluetoothAlwaysUsageDescription` | `CBManager.authorization`, `CBCentralManager`, `CBPeripheralManager` |

A declared purpose string is not a privacy violation by itself. The risk is unjustified protected resource exposure. The clearest framing is least privilege: an app should only declare purpose strings for protected resources that are required by its actual features, and each string should be accurate, meaningful, and specific.

See @MASTG-KNOW-0077 for the mapping between purpose string keys and framework APIs that request or check protected resource access.

This test verifies whether the app declares only the purpose strings it needs and whether each string accurately describes why the app needs access to the protected resource.

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` file to a readable format if needed.
4. Use @MASTG-TECH-0154 to inspect all `*UsageDescription` keys.
5. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
6. Use @MASTG-TECH-0066 to look for protected resource authorization APIs in the app binaries.

## Observation

The output should contain:

- The usage description keys declared by the app together with their user-facing purpose strings.
- The protected resource authorization APIs referenced by the app binaries.

## Evaluation

The test case fails if the collected evidence shows that the app declares or references access to a protected resource without a reasonable connection to a user-visible feature, if the purpose string does not accurately describe the related access, or if a reachable API that requires a purpose string does not have a matching purpose string.

A referenced API without a matching purpose string is a strong issue when the API can request or access the protected resource. Apple states that purpose strings must explain why the app needs access and should be accurate, meaningful, and specific. Missing purpose strings can cause access to fail or the app to exit, and App Store Connect may report this as `ITMS-90683: Missing purpose string in Info.plist`.

**Further Validation Required:**

Use the declared purpose strings, referenced APIs, app metadata, and visible app features to determine whether each protected resource access path is justified.

Consider the following when evaluating:

- Is the usage description key, purpose string, and related API surface reasonably connected to the app's stated purpose or visible functionality?
- Does the purpose string provide an accurate, meaningful, and specific explanation for the protected resource access?
- Are there APIs that request or access protected resources without matching purpose strings, and is the relevant code path reachable?
- Does the declared access create broader or more sensitive exposure than the feature requires, or could the app use a narrower alternative such as [`PHPickerViewController`](https://developer.apple.com/documentation/photosui/phpickerviewcontroller) or [`PhotosPicker`](https://developer.apple.com/documentation/photosui/photospicker) instead of `NSPhotoLibraryUsageDescription` for user-selected photos?

Static analysis can find unused code, SDK code, dead code, weak-linked frameworks, dynamically resolved APIs, obfuscated code, native libraries, loaded frameworks, or feature-flagged code paths. Treat missing API references as absence of evidence, not proof that the app never requests the protected resource. Treat referenced APIs without matching purpose strings as a failure only when they can reasonably be connected to reachable protected resource access.

Use dynamic analysis to complement the static analysis and identify authorization APIs that are actually reached at runtime. See @MASTG-TEST-0x02.
