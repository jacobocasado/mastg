---
id: MASTG-DEMO-0136
title: Internal Activity Communication via Implicit Intent
platform: android
code: [kotlin, xml]
test: MASTG-TEST-0372
kind: fail
---

## Sample

The following sample app attempts to start `InternalActivity` for app-internal communication by sending an implicit intent with the custom action `org.owasp.mastestapp.INTERNAL_ACTION`. This approach is insecure because another app can register a matching `<intent-filter>` and become a candidate during Android's intent resolution. For more details on the intent resolution mechanism, see @MASTG-KNOW-0025.

The intent in this sample is implicit because it relies on `setAction` without explicitly defining the target package or component. For the secure alternative, see @MASTG-BEST-0056.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml # AndroidManifest_reversed.xml }}

!!! note
    @MASTG-DEMO-0140 provides an example attacker app that declares a matching `<intent-filter>` for `org.owasp.mastestapp.INTERNAL_ACTION`. It demonstrates how another app can become a candidate for handling this sample app's implicit intent.

## Steps

Let's use @MASTG-TECH-0014 with an @MASTG-TOOL-0110 rule to scan the reverse-engineered code for implicit intents used for internal communication.

{{ rule.yaml }}

{{ run.sh }}

## Observation

The @MASTG-TOOL-0110 output reports one `startActivity` dispatch in `MastgTest_reversed.java`:

- Intent creation: `new Intent()`.
- Action: `org.owasp.mastestapp.INTERNAL_ACTION`.
- Extras visible in the reported block: `user_id` and `session_token`.
- Dispatch API: `context.startActivity(implicitIntent)`.
- Recipient restriction: none visible before dispatch. The code does not call `setPackage`, `setClass`, `setClassName`, or `setComponent`.

{{ output.txt }}

## Evaluation

The test case fails because the app uses an implicit intent (`org.owasp.mastestapp.INTERNAL_ACTION`) for app-internal communication with `InternalActivity`.

The action is app-specific and the manifest declares `InternalActivity` as the component intended to handle it, but the dispatch does not name that component or restrict the target package. Another app can declare a matching `<intent-filter>` and become a candidate during Android intent resolution.
