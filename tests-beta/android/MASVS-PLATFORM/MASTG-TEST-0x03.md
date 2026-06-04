---
platform: android
title: Exported Broadcast Receivers That Expose Sensitive Functionality
id: MASTG-TEST-0x03
type: [static, config, code, manual]
weakness: MASWE-0063
best-practices: [MASTG-BEST-0x03]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0x03, MASTG-KNOW-0020]
---

## Overview

Android apps declare [broadcast receivers](../../../knowledge/android/MASVS-PLATFORM/MASTG-KNOW-0x03.md) in the `AndroidManifest.xml` file or register them at runtime with `Context.registerReceiver`. A manifest receiver becomes reachable by any other app on the device when it sets `android:exported="true"` or declares an `<intent-filter>` without setting `android:exported="false"`. See @MASTG-KNOW-0x03 for details and @MASTG-KNOW-0020 for the IPC model.

If an exported receiver performs sensitive functionality in `onReceive`, another app can deliver a crafted broadcast to trigger it, potentially with attacker-controlled data. For example, a receiver that reads extras from the intent and uses them to send messages, change state, or disclose data can be abused by any app that knows the action it listens for.

This test checks whether the app exposes sensitive functionality through exported broadcast receivers.

**Example Attack Scenario:**

Suppose a banking app declares a broadcast receiver that resets the user's password based on extras in the received intent, and the receiver is exported with no `android:permission`.

1. An attacker reverse engineers the app and finds the exported receiver, the action it listens for, and the extras it reads (see @MASTG-TECH-0x03).
2. The attacker writes a malicious app that sends a broadcast targeting the receiver explicitly, with attacker-chosen extras.
3. The receiver acts on the unvalidated extras and resets the password (and may disclose the old one to the log).
4. The attacker takes over the account without any interaction from the victim.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0x03 to list the exported broadcast receivers, including context-registered receivers found in the code.
4. Use @MASTG-TECH-0014 to inspect the `onReceive` implementation of each exported receiver.

## Observation

The output should contain a list of exported broadcast receivers and the relevant parts of their `onReceive` implementation, including how they use data from the received intent and any permission they require.

## Evaluation

The test case fails if any exported broadcast receiver exposes sensitive functionality, for example by performing a security-relevant action or disclosing sensitive data when it receives a broadcast, without restricting which apps can deliver to it.

**Further Validation Required:**

Inspect each exported broadcast receiver using @MASTG-TECH-0023 to determine whether it exposes sensitive functionality:

- Determine whether `onReceive` performs a security-relevant action or discloses sensitive data based on the received intent (for example, reading extras and using them to send a message or change state).
- Determine whether the receiver validates the data it reads from the intent before acting on it.
- Determine whether the receiver is protected by an appropriate `android:permission`, or registered with `RECEIVER_NOT_EXPORTED`, that restricts which apps can deliver broadcasts to it.
