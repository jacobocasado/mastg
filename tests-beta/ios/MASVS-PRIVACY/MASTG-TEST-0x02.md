---
title: Runtime Use of Protected Resource Authorization APIs
platform: ios
id: MASTG-TEST-0x02
type: [dynamic, hooks, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0077]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x01.

If an iOS app checks or requests access to protected resources in contexts that do not match its declared purpose strings or feature set, it may access personal data unexpectedly. This test verifies which authorization APIs the app actually reaches at runtime and whether those calls match the declared permissions and user-visible functionality.

## Steps

1. Use @MASTG-TEST-0x01 to identify the purpose strings declared by the app.
2. Use @MASTG-TECH-0095 to hook the authorization and permission-request APIs associated with those purpose strings.
3. Exercise the app's relevant features and record which authorization APIs are called, their return values, and the backtraces of relevant calls.

## Observation

The output should contain a list of authorization-related methods that were called during app usage, for example:

- Method names and classes
- Return values (authorization status)
- Call stack (backtrace) to understand the context

## Evaluation

The test case fails if runtime traces show permission checks or requests that do not match the app's declared features or its stated purpose strings.

Examples include:

- The app requests or checks access for a protected resource in a feature that users would not reasonably expect.
- The app requests broader access than needed, for example "always" location access when "when in use" would suffice.
- The backtrace shows sensitive resource access in code paths that are unrelated to the feature described in the purpose string.

**Further Validation Required:**

Use the observed backtraces to inspect the relevant code with @MASTG-TECH-0076 and determine:

- whether the traced authorization calls lead to actual access to the protected data or capability,
- whether the surrounding feature genuinely requires that access, and
- whether the app could use a narrower or user-selected alternative instead.

Do not treat the absence of a trace during one execution as proof that a declared permission is unused. Optional flows, dormant code, and region-specific features may require deeper static review.
