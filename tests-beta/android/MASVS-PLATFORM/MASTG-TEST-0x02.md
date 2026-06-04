---
platform: android
title: Exported Services That Expose Sensitive Functionality
id: MASTG-TEST-0x02
type: [static, config, code, manual]
weakness: MASWE-0062
best-practices: [MASTG-BEST-0x02]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0x02, MASTG-KNOW-0020]
---

## Overview

Android apps declare [services](../../../knowledge/android/MASVS-PLATFORM/MASTG-KNOW-0x02.md) in the `AndroidManifest.xml` file. A service becomes reachable by any other app on the device when it sets `android:exported="true"` or declares an `<intent-filter>` without setting `android:exported="false"`. See @MASTG-KNOW-0x02 for details on the `android:exported` attribute and bound-service interfaces, and @MASTG-KNOW-0020 for the IPC model.

If an exported service performs or grants access to sensitive functionality, another app can start or bind to it and invoke that functionality. For example, a bound service that exposes a `Messenger` or AIDL interface can let a caller change credentials, retrieve stored secrets, or trigger privileged operations, especially when the service doesn't verify the caller's permission before processing a transaction.

This test checks whether the app exposes sensitive functionality through exported services.

**Example Attack Scenario:**

Suppose a password-manager app uses a bound service with a `Messenger` interface to change the master password, and the service is exported with no `android:permission`.

1. An attacker reverse engineers the app and identifies the exported service and the message format it expects (see @MASTG-TECH-0x02).
2. The attacker writes a malicious app that binds to the service and sends a message that sets a new master password.
3. The service processes the request without verifying the caller, so it resets the password.
4. The attacker now controls the victim's master password and can unlock the password vault.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0x02 to list the exported services.
4. Use @MASTG-TECH-0014 to inspect the code of each exported service.

## Observation

The output should contain a list of exported services and the relevant parts of their implementation, including the interface they expose and any permission checks they perform.

## Evaluation

The test case fails if any exported service exposes sensitive functionality, for example by returning sensitive data or performing a security-relevant action, without verifying that the caller is authorized.

**Further Validation Required:**

Inspect each exported service using @MASTG-TECH-0023 to determine whether it exposes sensitive functionality:

- Determine whether the service returns sensitive data or performs a security-relevant action (for example, changing a password or PIN) in response to a request.
- Determine whether the service verifies the caller's permission at runtime (for example, with `checkCallingPermission` or `enforceCallingPermission`) before processing the request.
- Determine whether the service is protected by an appropriate `android:permission` that restricts which apps can start or bind to it.
