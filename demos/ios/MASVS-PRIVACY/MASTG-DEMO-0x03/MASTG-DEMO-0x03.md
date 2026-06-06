---
platform: ios
title: Extracting Privacy-Relevant Entitlements from the App Code Signature
id: MASTG-DEMO-0x03
code: [swift, xml]
test: MASTG-TEST-0x03
---

## Sample

This sample uses the same app as @MASTG-DEMO-0x01. The app binary is signed with the `com.apple.developer.healthkit` entitlement, which allows the app to request user authorization for HealthKit access. This dummy app does not need the information provided by such entitlement for its functionality.

{{ ../MASTG-DEMO-0x01/MastgTest.swift }}

{{ ../MASTG-DEMO-0x01/Info.plist }}

{{ ../MASTG-DEMO-0x01/MASTestApp.entitlements # entitlements_reversed.plist }}

## Steps

1. Use @MASTG-TECH-0111 to extract the entitlements from the signed app bundle.
2. Run `run.sh` to print the extracted entitlements in a readable format.

{{ run.sh }}

## Observation

The output shows the entitlements embedded in the app:

{{ output.txt }}

## Evaluation

The test case fails because the app is signed with the `com.apple.developer.healthkit` entitlement, which is for the sample's shown functionality. The sample app does not present any health, fitness, or wellness feature that would justify enabling HealthKit.

!!! note
    Each entitlement should be evaluated against the app's stated functionality. If the app is a simple utility that doesn't need location, contacts, or photos, the related entitlements would be considered excessive and represent a privacy concern.
