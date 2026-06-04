---
platform: android
title: Exported Activities That Expose Sensitive Functionality
id: MASTG-TEST-0x01
type: [static, config, code, manual]
weakness: MASWE-0x01
best-practices: [MASTG-BEST-0x01]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0x01, MASTG-KNOW-0020]
---

## Overview

Android apps declare [activities](../../../knowledge/android/MASVS-PLATFORM/MASTG-KNOW-0x01.md) in the `AndroidManifest.xml` file. An activity becomes reachable by any other app on the device when it sets `android:exported="true"` or declares an `<intent-filter>` without setting `android:exported="false"`. See @MASTG-KNOW-0x01 for details on the `android:exported` attribute and @MASTG-KNOW-0020 for the IPC model.

If an exported activity performs or grants access to sensitive functionality, another app can start it directly with an `Intent` and reach that functionality without going through the app's intended flow. For example, an activity that displays account data or settings can be launched directly, bypassing a login screen that would normally protect it. The activity may also act on attacker-controlled data passed in the intent.

This test checks whether the app exposes sensitive functionality through exported activities.

**Example Attack Scenario:**

Suppose a banking app protects its account screen behind a login activity but also declares an account-details activity that is exported (for example, because it declares an `<intent-filter>` without setting `android:exported="false"`).

1. An attacker reverse engineers the app and finds the exported account-details activity (see @MASTG-TECH-0x01).
2. The attacker writes a malicious app that calls `startActivity` with an explicit intent targeting that activity by its component name.
3. The account-details activity starts directly, without going through the login activity.
4. The account-details activity displays the victim's account data without requiring authentication.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0x01 to list the exported activities.
4. Use @MASTG-TECH-0014 to inspect the code of each exported activity.

## Observation

The output should contain a list of exported activities and the relevant parts of their implementation.

## Evaluation

The test case fails if any exported activity exposes sensitive functionality, for example by displaying sensitive data, performing a security-relevant action, or allowing a caller to bypass authentication.

**Further Validation Required:**

Inspect each exported activity using @MASTG-TECH-0023 to determine whether it exposes sensitive functionality:

- Determine whether the activity displays or returns sensitive data (for example, account details, messages, or stored secrets).
- Determine whether the activity performs a security-relevant action (for example, changing settings or credentials).
- Determine whether starting the activity directly bypasses an authentication step, such as a login or PIN screen, that the app relies on elsewhere.
- Determine whether the activity is protected by an appropriate `android:permission` that restricts which apps can start it.
