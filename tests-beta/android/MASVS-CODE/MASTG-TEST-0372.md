---
title: Implicit Intents Used for Internal App Communication
platform: android
id: MASTG-TEST-0372
type: [static, code, manual]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0056]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

An [implicit intent](https://developer.android.com/guide/components/intents-filters) is an `Intent` that does not name a concrete target component. Instead, it declares an action, and optionally data or categories, and Android resolves it to an installed component with a matching `<intent-filter>`. See @MASTG-KNOW-0025 for background on explicit and implicit intents and intent resolution.

Android apps commonly use implicit intents when they intentionally delegate an action to another app selected by the system or the user. Typical legitimate uses include opening a web page or map location with `ACTION_VIEW`, sharing content with `ACTION_SEND`, requesting a file or image with `ACTION_GET_CONTENT`, or launching a chooser when the app does not need to control which external app handles the request.

The issue appears when an app uses the same mechanism for communication that is expected to stay inside the app, such as starting an internal activity or sending an app-specific action to another trusted component. In that case, intent hijacking can occur: a third-party app declares a matching `<intent-filter>` for the same action and becomes a candidate during Android's intent resolution. If the system presents a chooser, the user may select the third-party app; if only one matching handler exists or a default handler has been set, the intent may be delivered without an explicit user decision.

This test checks whether the app creates and dispatches implicit intents for internal communication. Relevant creation and dispatch patterns include `Intent(String)`, `Intent().setAction(...)`, `startActivity`, `startActivityForResult`, `ActivityResultLauncher.launch`, `startService`, `bindService`, `sendBroadcast`, and related APIs that send an `Intent` without naming a concrete recipient.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain instances where an `Intent` is initialized and dispatched without indicators that constrain the recipient to an explicit component, package, or trusted app boundary. Indicators of a constrained recipient include constructors that name a target class and calls such as `setPackage`, `setClass`, `setClassName`, or `setComponent` before the intent is dispatched.

## Evaluation

The test case fails if the app dispatches an implicit intent without constraining the recipient with an explicit component or package, and the inspected context shows that the intent is intended for app-internal communication or another trusted component within the app's trust boundary.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the intent is intended to stay within the app or a trusted app boundary:

- Determine whether the intent action is app-specific, such as an action using the app's package namespace or names such as `INTERNAL_ACTION`.
- Determine whether the action is handled by one of the app's own manifest-declared components or by code in the same app flow.
- Determine whether the intent carries data, commands, or state changes that are only meaningful to internal components.
- Determine whether the dispatch intentionally hands control to an external app selected by the user, such as standard actions for viewing, sharing, picking content, or opening maps.
- Determine whether the intent is constrained with an explicit component, package, or equivalent recipient restriction before dispatch.
