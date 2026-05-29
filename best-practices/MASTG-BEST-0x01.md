---
title: Minimize iOS Permissions and Entitlements
alias: minimize-ios-permissions-and-entitlements
id: MASTG-BEST-0x01
platform: ios
knowledge: [MASTG-KNOW-0077]
---

Request only the iOS permissions and app capabilities that the app actually needs, and prefer the narrowest Apple-supported access model for each feature. This reduces unnecessary exposure of personal data and limits the blast radius if the app, an extension, or a shared container is later abused.

Review `Info.plist` purpose strings, the signed entitlements, and any provisioning-profile entitlements together before release. A permission prompt or capability should map to a concrete feature that users can understand and justify. Remove unused or speculative entries instead of keeping them "just in case".

## Prefer Narrower Access Paths

When Apple provides a user-selected or limited-access API, prefer it over broad library or background access. For example, use [`PHPickerViewController`](https://developer.apple.com/documentation/photokit/phpickerviewcontroller) or [`PhotosPicker`](https://developer.apple.com/documentation/photosui/photospicker) when users only need to choose specific photos, and request full photo library access only when the app genuinely needs it.

For location, request [`when in use`](https://developer.apple.com/documentation/corelocation/requesting-authorization-to-use-location-services) access before considering broader background access. For Bluetooth, HealthKit, HomeKit, Siri, or other protected services, enable only the capabilities and usage-description keys that are strictly required for the implemented feature set.

## Minimize Data-Sharing Capabilities

Treat entitlements such as App Groups, iCloud containers, and Associated Domains as sensitive design decisions, not default conveniences. They can create new paths for sharing, syncing, or exposing personal data across apps, extensions, websites, or devices.

Before enabling one of these capabilities, document the exact data flow it unlocks, the minimum set of targets that need it, and the security controls that protect the data afterward. If the same feature can be implemented without broad shared containers or external associations, prefer the narrower design.

!!! note
    Newer Apple privacy mechanisms such as [privacy manifests](https://developer.apple.com/documentation/bundleresources/privacy_manifest_files) and required-reason APIs complement these checks but do not replace purpose strings or entitlements. You still need to minimize and review both.
*** Update File: /tmp/workspace/OWASP/mastg/knowledge/ios/MASVS-PLATFORM/MASTG-KNOW-0077.md

## Modern iOS Permission Model

Current iOS releases combine multiple layers that are easy to confuse during review:

- purpose strings in `Info.plist`, which explain protected-resource access to the user,
- signed entitlements and capabilities, which enable access to specific platform services or cross-app data sharing, and
- newer privacy metadata such as [privacy manifests](https://developer.apple.com/documentation/bundleresources/privacy_manifest_files), which complement but do not replace either of the above.

When reviewing app permissions, inspect all of these layers together. A feature may require a purpose string, an entitlement, both, or neither depending on which API the app uses.

Apple increasingly provides user-selected or reduced-scope alternatives to broad library access. For example, photo selection flows can often use [`PHPickerViewController`](https://developer.apple.com/documentation/photokit/phpickerviewcontroller) or [`PhotosPicker`](https://developer.apple.com/documentation/photosui/photospicker), and many location-driven features can work with [`when in use`](https://developer.apple.com/documentation/corelocation/requesting-authorization-to-use-location-services) access instead of persistent background access.

## Purpose Strings and Entitlements in Practice

The deprecated test @MASTG-TEST-0069 covered four areas that still matter during assessment:

- purpose strings in `Info.plist`,
- the app's signed entitlements,
- the embedded provisioning profile when present, and
- the actual code paths that use the protected resource or capability.

The dedicated v2 tests split these concerns so each one can stay focused:

- @MASTG-TEST-0x01 for `Info.plist` purpose strings,
- @MASTG-TEST-0x02 for runtime authorization APIs, and
- @MASTG-TEST-0x03 for entitlements and related capabilities.

Update the review scope whenever Apple introduces narrower permission models, new capability requirements, or new metadata that affects privacy review.

## Inspecting Purpose Strings

If linking on or after iOS 10, developers must include purpose strings in their app's [`Info.plist`](https://developer.apple.com/documentation/bundleresources/information_property_list) file before accessing protected resources. Otherwise, the access fails and the app may terminate. Apple maintains the current key list in the Bundle Resources documentation, and the set of relevant keys evolves over time as the platform adds new APIs and access models.

Examples include:

- `NSCameraUsageDescription`
- `NSMicrophoneUsageDescription`
- `NSPhotoLibraryUsageDescription`
- `NSPhotoLibraryAddUsageDescription`
- `NSLocationWhenInUseUsageDescription`
- `NSLocationAlwaysAndWhenInUseUsageDescription`
- `NSBluetoothAlwaysUsageDescription`

## Inspecting Entitlements

According to the [Apple Platform Security](https://support.apple.com/guide/security/welcome/web) documentation, entitlements are signed key-value pairs that grant an app access to specific services or behaviors beyond the default sandbox. These values are part of the signed code, so they cannot be modified without resigning the app.

When you only have the built app, inspect both:

- the entitlements embedded in the app's signature, and
- the `embedded.mobileprovision` file, if present, to understand which entitlements the provisioning profile allowed.

Examples of privacy-relevant entitlements include:

- `com.apple.security.application-groups`
- `com.apple.developer.icloud-container-identifiers`
- `com.apple.developer.healthkit`
- `com.apple.developer.homekit`
- `com.apple.developer.associated-domains`

These entitlements do not always generate a user-facing permission prompt, but they can still widen the app's ability to share, sync, or expose personal data.

## Runtime Validation

Static review is not enough on its own. After identifying purpose strings and entitlements, verify the related feature at runtime and determine how the app actually uses the protected resource. This helps you confirm whether the prompt, entitlement, and feature implementation all match the same business need.
