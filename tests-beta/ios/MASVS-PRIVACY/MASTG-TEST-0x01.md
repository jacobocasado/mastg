---
title: Purpose Strings in Info.plist
platform: ios
id: MASTG-TEST-0x01
type: [static]
weakness: MASWE-0117
profiles: [P]
---

## Overview

iOS apps must declare the permissions they need in the `Info.plist` file using purpose strings (also known as usage descriptions). These strings explain to users why the app requires access to specific resources such as the camera, location, or contacts.

Since iOS 10, apps must include a purpose string for each protected resource they access. If the string is missing, the app crashes when attempting to access that resource. The purpose strings typically end with `UsageDescription` (e.g., `NSCameraUsageDescription`, `NSLocationWhenInUseUsageDescription`).

This test checks whether the declared purpose strings are appropriate for the app's functionality. Requesting excessive permissions can expose user data unnecessarily and may indicate privacy issues or over-collection of data.

## Steps

1. Extract the `Info.plist` file from the app (see @MASTG-TECH-0058).
2. Convert the `Info.plist` to a readable format if needed (see @MASTG-TECH-0138).
3. Search for all keys ending with `UsageDescription` to identify declared purpose strings.

## Observation

The output should contain the list of purpose strings declared by the app. Common purpose strings include:

- `NSCameraUsageDescription`
- `NSMicrophoneUsageDescription`
- `NSPhotoLibraryUsageDescription`
- `NSLocationWhenInUseUsageDescription`
- `NSLocationAlwaysUsageDescription`
- `NSContactsUsageDescription`
- `NSCalendarsUsageDescription`
- `NSHealthShareUsageDescription`
- `NSHealthUpdateUsageDescription`
- `NSMotionUsageDescription`
- `NSBluetoothAlwaysUsageDescription`
- `NSFaceIDUsageDescription`

## Evaluation

The test fails if the app requests permissions that are not justified by its core functionality.

Consider the following when evaluating:

- Does the permission align with the app's stated purpose? For example, a flashlight app requesting `NSContactsUsageDescription` is suspicious.
- Are there privacy-preserving alternatives? For instance, using [`PHPickerViewController`](https://developer.apple.com/documentation/photokit/phpickerviewcontroller) instead of requesting full photo library access.
- Does the purpose string provide a clear and honest explanation to the user?

Also consider the sensitivity of the requested data:

- Location permissions (`NSLocationAlwaysUsageDescription`) provide continuous access to user location and should be scrutinized carefully.
- Health-related permissions (`NSHealthShareUsageDescription`, `NSHealthClinicalHealthRecordsShareUsageDescription`) grant access to sensitive medical data.
- Photo library access (`NSPhotoLibraryUsageDescription`) may expose personal photos accessible by other apps.

For each permission that accesses sensitive data, verify that the app handles this data securely (see @MASTG-TEST-0215 for data storage tests).
