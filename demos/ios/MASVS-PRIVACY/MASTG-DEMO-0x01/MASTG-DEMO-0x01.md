---
platform: ios
title: Unjustified Access to User Data due to Excessive Purpose Strings
id: MASTG-DEMO-0x01
code: [swift, xml]
test: MASTG-TEST-0x02
---

## Sample

The app declares purpose strings for location, contacts, photos, and camera in `Info.plist`. When the **Start** button is tapped, the app reaches authorization related APIs for location, contacts, and photos.

Although this is a dummy app, the location, contacts, and photos calls are part of the sample's intended behavior. They represent the protected resource functionality that the **Start** flow is designed to demonstrate. Therefore, those purpose strings are considered justified for this sample because the declared resources are actually used by the app flow.

The issue is `NSCameraUsageDescription`: the app declares a camera purpose string, but no camera authorization or access API is triggered. This creates unjustified protected resource exposure because the app declares that it may request camera access even though the sample has no camera related functionality. The declaration alone does not grant camera access.

{{ MastgTest.swift # Info.plist }}

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve `./Payload/MASTestApp.app/Info.plist` and save it as `Info.plist` in this demo directory.
3. Use @MASTG-TECH-0138 to convert `Info.plist` to a readable format if needed.
4. Use @MASTG-TECH-0154 to inspect the purpose strings by running `run.sh`.

{{ run.sh }}

5. Install the app on a device using @MASTG-TECH-0056.
6. Make sure @MASTG-TOOL-0039 is installed on your machine and `frida-server` is running on the device.
7. Run `run_frida.sh` to spawn the app with Frida.
8. Tap the **Start** button to trigger the authorization checks.
9. Stop the script by pressing `Ctrl+C`.

{{ run_frida.sh # script.js }}

## Observation

The output reveals the purpose strings declared in the app's `Info.plist` file.

{{ output_purpose_strings.txt }}

The purpose strings declared in `Info.plist` are:

- `NSLocationWhenInUseUsageDescription`
- `NSContactsUsageDescription`
- `NSPhotoLibraryUsageDescription`
- `NSCameraUsageDescription`

The Frida script output reveals the protected resource APIs reached at runtime.

{{ output_frida.txt }}

The runtime trace shows calls to:

- `CLLocationManager.requestWhenInUseAuthorization`
- `CNContactStore.authorizationStatusForEntityType`
- `PHPhotoLibrary.authorizationStatusForAccessLevel`

## Evaluation

The test case fails because the app declares `NSCameraUsageDescription` without a reasonable connection to the app's demonstrated functionality. The **Start** flow exercises location, contacts, and photos, but it does not request, check, or access the camera.

The location, contacts, and photos purpose strings are justified in this sample because they match APIs reached by the intended app flow:

- `NSLocationWhenInUseUsageDescription` matches `CLLocationManager.requestWhenInUseAuthorization`.
- `NSContactsUsageDescription` matches `CNContactStore.authorizationStatusForEntityType`.
- `NSPhotoLibraryUsageDescription` matches `PHPhotoLibrary.authorizationStatusForAccessLevel`.

The camera purpose string is not justified: `NSCameraUsageDescription` is declared in `Info.plist` but no camera related runtime API or related user visible feature is observed.

Declaring `NSCameraUsageDescription` does not grant camera access by itself. The app would still need to call a camera related API and the user would still need to grant access. The issue is that the app declares a privacy sensitive capability that is not tied to any observed or user visible camera feature.
