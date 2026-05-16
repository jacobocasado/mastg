---
id: MASTG-DEMO-XXXB
title: Unintentionally Exported Activities Detection
platform: android
code: [xml]
tools: [semgrep]
kind: fail
---

## Sample

The following reversed `AndroidManifest.xml` snippet shows multiple activities marked as exported. While `MainActivity` must be exported to be launched by the system, `InternalActivity` is intended for internal use and should not be accessible to other apps. Additionally, some activities from external libraries are exported by default.

{{ AndroidManifest_reversed.xml }}

## Steps

We can use semgrep to identify all activities that are explicitly exported in the manifest.

{{ run.sh }}

## Observation

The semgrep output lists all activities where `android:exported="true"`.

{{ output.txt }}

## Evaluation

The test case fails because `InternalActivity` is an internal component that has been unintentionally exported. This allows any other application on the device to launch this activity, potentially bypassing intended security controls or intercepting implicit intents.

- `MainActivity` is the launcher activity and must be exported.
- `InternalActivity` is an internal component and should have `android:exported="false"`.
- `androidx.compose.p000ui.tooling.PreviewActivity` and `androidx.activity.ComponentActivity` are components introduced by external libraries that are also exported. Developers should review whether these components are strictly necessary in the production build and if their exposure poses any security risk.
