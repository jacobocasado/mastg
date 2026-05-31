---
title: Authentication Bypass Through an Exported Activity
platform: android
id: MASTG-DEMO-0x01
code: [kotlin, xml]
status: draft
note: Draft. The sample code and manifest must be integrated into the MASTestApp and validated, and a run.sh plus output artifacts must be added before publishing. See the "Finish Demo Draft MASTG-DEMO-0x01" follow-up.
---

## Sample

The sample declares an activity that shows stored account data. The activity is exported through an `<intent-filter>` and doesn't re-check authentication, so any app can start it directly and bypass the login screen. The sample is inspired by the password-list activity in the "Sieve" app but is original.

{{ AndroidManifest.xml # MastgTest_PasswordListActivity.kt }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0x01 to list the exported activities.
3. Use @MASTG-TECH-0023 to inspect the code of each exported activity.

## Observation

The output should contain the `PasswordListActivity` declaration with `android:exported="true"` (via its intent filter) and no `android:permission`, and its implementation, which displays stored credentials without checking the authentication state.

## Evaluation

The test case fails because `PasswordListActivity` is exported and exposes sensitive functionality: it displays stored credentials and can be started directly with `adb shell am start`, bypassing the login screen.
