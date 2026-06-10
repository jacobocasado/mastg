---
title: Runtime Use of Protected Resource APIs for Unjustified Access
platform: ios
id: MASTG-TEST-0x02
type: [dynamic, hooks, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0077]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x01. See @MASTG-TEST-0x01 for background on the relationship between protected resources, usage description keys, purpose strings, and framework APIs.

This test verifies which protected resource authorization APIs the app actually reaches at runtime, and whether that runtime behavior is justified by the declared purpose strings and user-visible functionality.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` file to a readable format if needed.
4. Use @MASTG-TECH-0154 to inspect all `*UsageDescription` keys.
5. Use @MASTG-TECH-0095 to hook the relevant protected resource APIs.
6. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain:

- The usage description keys declared by the app together with their user-facing purpose strings.
- The authorization-related methods that were called during app usage.

For each observed call, record:

- Method names and classes
- Return values (authorization status)
- Call stack (backtrace) to understand the context
- The user action or app flow that triggered the call

## Evaluation

The test case fails if the collected evidence shows that the app checks, requests, or accesses a protected resource without a reasonable connection to a user-visible feature, if the runtime behavior does not match the declared purpose string, or if an observed API that requires a purpose string is reached without a matching purpose string.

An observed runtime call without a matching purpose string is a strong issue when the API can request or access the protected resource. Missing purpose strings can cause access to fail or the app to exit, and App Store Connect may report this as `ITMS-90683: Missing purpose string in Info.plist`.

**Further Validation Required:**

Use the observed runtime calls, trigger conditions, declared purpose strings, app metadata, visible app features, and backtraces to determine whether each protected resource access path is justified.

Consider the following when evaluating:

- Is the observed authorization call reasonably connected to the user action or feature that triggered it?
- Does the runtime behavior match the declared purpose string, including the protected resource, access level, and user-facing explanation?
- Are there observed calls that request or access protected resources without matching purpose strings?
- Does the observed access create broader or more sensitive exposure than the feature requires, or could the app use a narrower alternative such as [`PHPickerViewController`](https://developer.apple.com/documentation/photosui/phpickerviewcontroller) or [`PhotosPicker`](https://developer.apple.com/documentation/photosui/photospicker) instead of broad photo library access?

Runtime analysis may miss protected resource access in flows that were not triggered, code paths that depend on account state, device state, location, granted permissions, remote configuration, feature flags, backend responses, unavailable hardware, or app extensions. Treat missing runtime calls as absence of evidence, not proof that the app never requests or accesses the protected resource.

Use static analysis to complement the runtime analysis and identify APIs that are present in the app's code but were not observed at runtime. See @MASTG-TEST-0x01.
