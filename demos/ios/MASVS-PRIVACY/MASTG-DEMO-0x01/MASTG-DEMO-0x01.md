---
platform: ios
title: Identifying Purpose Strings in Info.plist
id: MASTG-DEMO-0x01
code: [swift, xml]
test: MASTG-TEST-0x01
---

## Sample

The code snippet below shows sample code that accesses protected resources requiring purpose strings. The `Info.plist` file declares multiple usage descriptions that the app uses to request permissions.

{{ MastgTest.swift # Info.plist }}

## Steps

1. Unzip the app package and locate the `Info.plist` file (@MASTG-TECH-0058), which is located in `./Payload/MASTestApp.app/Info.plist`.
2. Convert the `Info.plist` to XML format if needed (@MASTG-TECH-0138).
3. Run `run.sh` to extract purpose strings.

{{ run.sh }}

## Observation

The output reveals the purpose strings declared in the app's `Info.plist` file.

{{ output.txt }}

## Evaluation

The test fails because the app declares multiple purpose strings that may be excessive for its core functionality:

- `NSLocationWhenInUseUsageDescription`: Grants access to user location.
- `NSContactsUsageDescription`: Grants access to the user's contacts.
- `NSPhotoLibraryUsageDescription`: Grants access to the photo library.
- `NSCameraUsageDescription`: Grants access to the device camera.

Each permission should be evaluated against the app's stated functionality. If the app is a simple utility that doesn't need location, contacts, or photos, these permissions would be considered excessive and represent a privacy concern.
