---
title: References to Implicit Intents Carrying Sensitive Extras
platform: android
id: MASTG-TEST-0374
type: [static, code, manual]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0056]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

An [implicit intent](https://developer.android.com/guide/components/intents-filters) is an `Intent` that does not name a concrete target component. Instead, it declares an action, and optionally data or categories, and Android resolves it to an installed component with a matching `<intent-filter>`. See @MASTG-KNOW-0025 for background on explicit and implicit intents and intent resolution.

Android apps commonly use implicit intents with extras when they intentionally delegate data to another app selected by the system or the user. Typical legitimate uses include sharing user-selected content with `ACTION_SEND`, attaching metadata for an external viewer, or launching a chooser when the user decides which app receives the data.

The issue appears when the app attaches sensitive or security-relevant extras to an implicit intent without constraining the recipient. During intent resolution, any installed app with a matching `<intent-filter>` can become the selected recipient and receive the full extras `Bundle`. This can disclose credentials, session tokens, one-time codes, personal data, account identifiers, or internal state to an untrusted app. Depending on the data, the impact can include privacy leakage, session compromise, account takeover, or unauthorized use of backend APIs.

This test checks whether the app creates and dispatches implicit intents that carry sensitive extras. Relevant patterns include creating an `Intent` with an action, adding extras with `putExtra`, `putExtras`, `replaceExtras`, or a `Bundle`, and dispatching it through APIs such as `startActivity`, `startActivityForResult`, `ActivityResultLauncher.launch`, `startService`, `bindService`, `sendBroadcast`, or related APIs.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain instances where an `Intent` is populated with extras and dispatched without indicators that constrain the recipient to an explicit component, package, or trusted app boundary. Indicators of a constrained recipient include constructors that name a target class and calls such as `setPackage`, `setClass`, `setClassName`, or `setComponent` before the intent is dispatched.

## Evaluation

The test case fails if the app dispatches an implicit intent carrying extras that contain sensitive or security-relevant data, and the recipient is not constrained to an explicit component, package, or trusted app boundary.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the extras contain sensitive data and whether external delivery is intentional:

- Determine whether the extras contain credentials, authentication tokens, session identifiers, one-time codes, personal data, account identifiers, internal commands, or other security-relevant data.
- Determine whether the intent action is app-specific, such as an action using the app's package namespace or names such as `INTERNAL_ACTION`.
- Determine whether the dispatch intentionally shares user-selected data with an external app, such as a standard `ACTION_SEND` or chooser flow.
- Determine whether the intent is constrained with an explicit component, package, or equivalent recipient restriction before dispatch.
