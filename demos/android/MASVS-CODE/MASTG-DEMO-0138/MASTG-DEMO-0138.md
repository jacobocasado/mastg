---
id: MASTG-DEMO-0138
title: Leaking Sensitive Information via Implicit Intents
platform: android
code: [kotlin]
test: MASTG-TEST-0374
kind: fail
---

## Sample

This demo uses the same sample app as @MASTG-DEMO-0136. The app sends `user_id` and `session_token` (which are considered sensitive information) as extras in an implicit intent.

Because the intent is implicit, any application that registers for the action can receive it and extract those values.

{{ ../MASTG-DEMO-0136/MastgTest.kt # ../MASTG-DEMO-0136/MastgTest_reversed.java }}

## Steps

Let's use @MASTG-TECH-0014 with an @MASTG-TOOL-0110 rule to scan the reverse-engineered code for implicit intents that carry sensitive extras.

{{ rule.yaml }}

{{ run.sh }}

## Observation

The @MASTG-TOOL-0110 output reports one `startActivity` dispatch that carries extras in `MastgTest_reversed.java`:

- Intent creation: `new Intent()`.
- Action: `org.owasp.mastestapp.INTERNAL_ACTION`.
- Extras: `user_id` with value `12345` and `session_token` with value `abcde-fghij-12345`.
- Dispatch API: `context.startActivity(implicitIntent)`.
- Recipient restriction: none visible before dispatch. The code does not call `setPackage`, `setClass`, `setClassName`, or `setComponent`.

{{ output.txt }}

## Evaluation

The test case fails because the app attaches sensitive extras (`user_id` and `session_token`) to an implicit intent and dispatches it without constraining the recipient.

The code does not name a target package or component and does not verify the selected handler's identity before dispatch. Any app that declares a matching `<intent-filter>` for `org.owasp.mastestapp.INTERNAL_ACTION` can become a candidate and receive the full extras `Bundle`.
