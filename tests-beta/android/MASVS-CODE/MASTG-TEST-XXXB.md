---
title: Internal Component Unintentionally Exported
platform: android
id: MASTG-TEST-XXXB
type: [static]
weakness: MASWE-0066
best-practices: [MASTG-BEST-XXXA]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

Activities intended for internal use within an application should not be exported. An exported activity can be launched by any other application on the device, potentially exposing sensitive functionality or data to unauthorized parties. While some activities must be exported (like the main launcher activity), internal activities should have `android:exported="false"` in the `AndroidManifest.xml`. If an activity has an `<intent-filter>`, it is exported by default on older Android versions, and must be explicitly marked as exported or not on newer versions. This test identifies exported activities and evaluates if they should have been kept internal.

## Steps

1. Extract the `AndroidManifest.xml` file using @MASTG-TECH-0007.
2. Search for all `<activity>` tags where `android:exported` is set to `true`.
3. For each exported activity, determine if it is intended to be accessible by other apps (e.g., launcher, deep link handlers) or if it performs internal-only tasks.

## Observation

The output should contain a list of all exported activities found in the manifest.

## Evaluation

The test case fails if any activity intended for internal use is found to be exported (`android:exported="true"`). For example, if an activity that processes sensitive user data or performs administrative tasks is accessible to external apps, it poses a significant security risk and should be marked as `android:exported="false"`.
