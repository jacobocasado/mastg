---
platform: ios
title: Identifying Purpose Strings in Info.plist
id: MASTG-DEMO-0x01
code: [swift, xml]
test: MASTG-TEST-0x01
---

## Sample

This showcases how to identify the access to protected resources by an app by identifying purpose strings. The `Info.plist` file of the app declares multiple usage descriptions that the app uses to request permissions.

{{ MastgTest.swift # Info.plist }}

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve `./Payload/MASTestApp.app/Info.plist` and save it as `Info.plist` in this demo directory.
3. Use @MASTG-TECH-0138 to convert `Info.plist` to a readable format if needed.
4. Use @MASTG-TECH-0154 to inspect the purpose strings by running `run.sh`.

{{ run.sh }}

## Observation

The output reveals the purpose strings declared in the app's `Info.plist` file.

{{ output.txt }}

## Evaluation

The test case fails because the app declares multiple purpose strings that may be excessive for its core functionality:

- `NSLocationWhenInUseUsageDescription`: Grants access to user location.
- `NSContactsUsageDescription`: Grants access to the user's contacts.
- `NSPhotoLibraryUsageDescription`: Grants access to the photo library.
- `NSCameraUsageDescription`: Grants access to the device camera.

Each permission should be evaluated against the app's stated functionality. If the app is a simple utility that doesn't need location, contacts, or photos, these permissions would be considered excessive and represent a privacy concern.
