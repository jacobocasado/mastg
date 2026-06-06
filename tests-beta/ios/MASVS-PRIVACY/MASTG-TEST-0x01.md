---
title: Purpose Strings in Info.plist
platform: ios
id: MASTG-TEST-0x01
type: [static, config, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0077]
---

## Overview

If an iOS app declares protected-resource purpose strings that do not match its actual features, users can be prompted to grant unnecessary access to personal data such as location, contacts, photos, or health information. Once granted, that access broadens the app's privacy exposure and increases the amount of sensitive data that could be collected, mishandled, or exposed if the app or one of its components is compromised. This test checks whether the app declares only the purpose strings it really needs and whether the strings honestly describe the corresponding feature. The iOS permission model, including the relationship between purpose strings and protected-resource APIs, is described in @MASTG-KNOW-0077.

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` file to a readable format if needed.
4. Use @MASTG-TECH-0154 to inspect all `*UsageDescription` keys.
5. Review each declared purpose string against the app's exposed features.

## Observation

The output should contain the list of purpose strings declared by the app together with the corresponding user-facing explanation. Common keys include:

- `NSCameraUsageDescription`
- `NSMicrophoneUsageDescription`
- `NSPhotoLibraryAddUsageDescription`
- `NSPhotoLibraryUsageDescription`
- `NSLocationWhenInUseUsageDescription`
- `NSLocationAlwaysAndWhenInUseUsageDescription`
- `NSContactsUsageDescription`
- `NSCalendarsUsageDescription`
- `NSHealthShareUsageDescription`
- `NSHealthUpdateUsageDescription`
- `NSMotionUsageDescription`
- `NSBluetoothAlwaysUsageDescription`
- `NSFaceIDUsageDescription`

## Evaluation

The test case fails if the app declares purpose strings that are not justified by its features or if the strings misrepresent what the app is doing.

Consider the following when evaluating:

- Does the permission align with the app's stated purpose? For example, a flashlight app requesting `NSContactsUsageDescription` is suspicious.
- Does the purpose string provide a clear and honest explanation to the user instead of a generic or misleading message?
- Could the app use a narrower alternative instead? For example, prefer [`PHPickerViewController`](https://developer.apple.com/documentation/photokit/phpickerviewcontroller) or [`PhotosPicker`](https://developer.apple.com/documentation/photosui/photospicker) over broad photo library access when the user only needs to select specific photos.

Also consider the sensitivity of the requested data:

- Location permissions such as `NSLocationAlwaysAndWhenInUseUsageDescription` provide broad access to user location and should be scrutinized carefully.
- Health-related permissions (`NSHealthShareUsageDescription`, `NSHealthClinicalHealthRecordsShareUsageDescription`) grant access to sensitive medical data.
- Photo library access (`NSPhotoLibraryUsageDescription`) may expose personal photos accessible by other apps.

For each permission that accesses sensitive data, verify that the app handles this data securely (see @MASTG-TEST-0215 for data storage tests).
