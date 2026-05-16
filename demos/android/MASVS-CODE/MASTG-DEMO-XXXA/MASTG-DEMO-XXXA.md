---
id: MASTG-DEMO-XXXA
title: Internal Activity Communication via Implicit Intent
platform: android
code: [kotlin, xml]
tools: [semgrep]
kind: fail
---

## Sample

The following code implements an application that attempts to invoke an implicit intent to open an internal activity. This approach is insecure because a system dialog (picker) will open if multiple apps handle the action, and a user might accidentally choose the wrong app, leading to intent hijacking. For more details on this mechanism and dynamic exploitation, see **[MASTG-KNOW-XXXA](../../../knowledge/android/MASVS-CODE/MASTG-KNOW-XXXA.md)**.

The intent in this sample is implicit because it relies solely on `setAction` without explicitly defining the target component. To secure internal communication, developers should use explicit intents by specifying the target using `setPackage`, `setComponent`, or the explicit `Intent(Context, Class)` constructor. In this demo, we use a custom @MASTG-TOOL-0110 rule to statically check for these insecure patterns.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code to detect the use of implicit intents for internal communication.

{{ rule.yaml }}

{{ run.sh }}

## Observation

The @MASTG-TOOL-0110 scan identifies the vulnerable code block where the implicit intent is created and used to start an activity.

{{ output.txt }}

## Evaluation

The test case fails because the app uses an implicit intent (`INTERNAL_ACTION`) to start a component that is intended to be internal. Static analysis confirms the insecure code pattern. The potential for intent hijacking and dynamic exploitation is further documented in **[MASTG-KNOW-XXXA](../../../knowledge/android/MASVS-CODE/MASTG-KNOW-XXXA.md)**.
