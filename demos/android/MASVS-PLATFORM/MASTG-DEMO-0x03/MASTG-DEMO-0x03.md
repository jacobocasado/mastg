---
title: Sensitive Action Through an Exported Broadcast Receiver
platform: android
id: MASTG-DEMO-0x03
code: [kotlin, xml]
status: draft
note: Draft. The sample code and manifest must be integrated into the MASTestApp and validated, and a run.sh plus output artifacts must be added before publishing. See the "Finish Demo Draft MASTG-DEMO-0x03" follow-up.
---

## Sample

The sample declares a broadcast receiver that reads a phone number and a new password from the received intent and sends the stored password to that number by SMS. The receiver is exported and requires no permission, so any app can trigger it. The sample is inspired by the receiver in the "Android Insecure Bank" app but is original.

{{ AndroidManifest.xml # MastgTest_PasswordResetReceiver.kt }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0x03 to list the exported broadcast receivers.
3. Use @MASTG-TECH-0023 to inspect the `onReceive` implementation of each exported receiver.

## Observation

The output should contain the `PasswordResetReceiver` declaration with `android:exported="true"` and no `android:permission`, and its `onReceive` implementation, which acts on attacker-controlled intent extras.

## Evaluation

The test case fails because `PasswordResetReceiver` is exported, requires no permission, and performs a security-relevant action (disclosing the stored password by SMS) based on unvalidated data from the received intent.
