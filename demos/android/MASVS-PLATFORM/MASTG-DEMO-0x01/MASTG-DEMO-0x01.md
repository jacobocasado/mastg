---
title: Authentication Bypass Through an Exported Activity
platform: android
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
---

## Sample

The sample implements a two-activity login flow. Tapping **Start** in the main screen launches `PinEntryActivity`, which prompts for a PIN (4321) before proceeding to `SecretActivity`. `SecretActivity` displays sensitive account data and is meant to be reachable only after the user passes the PIN check. However, `SecretActivity` is declared as exported in the `AndroidManifest.xml` with no `android:permission`, so any app (or `adb`) can start `SecretActivity` directly, bypassing the PIN gate entirely.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0x01 to list the exported activities.

The `run.sh` script lists the exported activities declared in the manifest.

{{ run.sh }}

## Observation

The output lists the activities declared as exported in the manifest.

{{ output.txt }}

`SecretActivity` is exported (`android:exported="true"`) and declares no `android:permission`. `PinEntryActivity` is not exported (`android:exported="false"`) and can only be reached through the app's own flow.

## Evaluation

The test case fails because `SecretActivity` exposes sensitive functionality and is exported without any permission protection.

The activity displays account data in `onCreate` without checking whether the user completed the PIN challenge:

```kotlin
class SecretActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... displays account number, balance, and recovery PIN ...
    }
}
```

Although `PinEntryActivity` enforces a PIN before launching `SecretActivity`, the protection is client-side only. Because `SecretActivity` is exported and unprotected, any app can start it directly, bypassing `PinEntryActivity` entirely.

The output also lists other exported activities. These are triaged but not reported as vulnerable in this test case.

`MainActivity` is the launcher activity. Launcher activities normally need to be exported so Android and the launcher can start the app. [Android's guidance](https://developer.android.com/about/versions/12/behavior-changes-12#exported) says activities with the `LAUNCHER` category should use `android:exported="true"`, while most other components should use `false`.

`androidx.activity.ComponentActivity` is commonly added by the Compose UI test manifest as a generic host activity for Compose tests. This is expected in debug or test builds, but should be reviewed if it appears in a production build.

`androidx.compose.ui.tooling.PreviewActivity` is a Compose tooling activity used by Android Studio to run composable previews. It is not part of the app's authentication flow and should normally be treated as development tooling unless the tested build is intended for production.

### Confirm the Exposure

You can use @MASTG-TECH-0x01 to start `SecretActivity` directly and confirm that the sensitive screen is reachable without entering the PIN:

```bash
adb shell am start -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\\$SecretActivity'
```

The secret screen appears without any PIN prompt, confirming the authentication bypass.


An external app can start `SecretActivity` directly, but that does not automatically let the external app read the activity’s UI contents or steal the displayed data programmatically. Android does not normally return another activity’s screen text to the caller.

The security issue is that the protected screen becomes reachable without completing the PIN challenge. This can still expose sensitive data to anyone using the device, to screen capture or accessibility based threats, or to any flow where the attacker can trick the user into opening the activity. If the activity also returns data through results, sends broadcasts, writes files, accepts attacker controlled extras, or performs account actions on launch, the impact could be higher.

In this sample, the finding is an authentication bypass because `SecretActivity` displays sensitive account data without verifying that the user completed the PIN challenge. The direct launch proves unauthorized access to the protected screen, even though the calling app does not automatically receive the displayed data.
