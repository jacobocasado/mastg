---
title: Credential Change Through an Exported Service
platform: android
id: MASTG-DEMO-0x02
code: [kotlin, xml]
status: draft
note: Draft. The sample code and manifest must be integrated into the MASTestApp and validated, and a run.sh plus output artifacts must be added before publishing. See the "Finish Demo Draft MASTG-DEMO-0x02" follow-up.
---

## Sample

The sample declares a bound service that exposes a `Messenger` interface to change the app's password. The service is exported and doesn't verify the caller's permission, so any app can bind to it and reset the password. The sample is inspired by the `AuthService` in the "Sieve" app but is original.

{{ AndroidManifest.xml # MastgTest_AuthService.kt }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0x02 to list the exported services.
3. Use @MASTG-TECH-0023 to inspect the code of each exported service.

## Observation

The output should contain the `AuthService` declaration with `android:exported="true"` and no `android:permission`, and its `handleMessage` implementation, which changes the stored password without verifying the caller.

## Evaluation

The test case fails because `AuthService` is exported, exposes a security-relevant operation (changing the password), and performs no caller-permission check before doing so.
