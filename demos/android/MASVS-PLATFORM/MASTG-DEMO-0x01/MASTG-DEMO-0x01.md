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

An external app can start `SecretActivity` directly, but that does not automatically let the external app read the activity's UI contents or steal the displayed data programmatically. Android does not normally return another activity's screen text to the caller.

The security issue is that the protected screen becomes reachable without completing the PIN challenge. This can still expose sensitive data to anyone using the device, to screen capture or accessibility based threats, or to any flow where the attacker can trick the user into opening the activity. If the activity also returns data through results, sends broadcasts, writes files, accepts attacker controlled extras, or performs account actions on launch, the impact could be higher.

In this sample, the finding is an authentication bypass because `SecretActivity` displays sensitive account data without verifying that the user completed the PIN challenge. The direct launch proves unauthorized access to the protected screen, even though the calling app does not automatically receive the displayed data.

## Fix

There are two ways to fix this, and the right choice depends on whether `SecretActivity` needs to be reachable by external apps at all.

**Option 1: Set `android:exported="false"` (recommended for most apps)**

If `SecretActivity` has no legitimate reason to be started by another app, simply prevent external apps from reaching it:

```xml
<activity
    android:name="org.owasp.mastestapp.MastgTest$SecretActivity"
    android:exported="false" />
```

Trying to start `SecretActivity` again with `adb` after this change will fail with an error, confirming that the activity is no longer reachable from outside the app:

```bash
adb shell am start -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\\$SecretActivity'
Starting: Intent { cmp=org.owasp.mastestapp/.MastgTest$SecretActivity }

Exception occurred while executing 'start':
java.lang.SecurityException: Permission Denial: starting Intent { flg=0x10000000 xflg=0x4 cmp=org.owasp.mastestapp/.MastgTest$SecretActivity } from null (pid=29738, uid=2000) not exported from uid 10225
```

This is the right choice for the vast majority of activities that display sensitive data or are part of an internal authentication flow. Android 12 and later require you to explicitly set `android:exported` on any activity with an `<intent-filter>`; setting it to `false` on activities that don't need it is the minimal, correct fix.

**Option 2: Keep `android:exported="true"` but enforce a `android:permission`**

If the activity must be reachable by a trusted partner app (for example, a companion widget or a deep-link handler used by a first-party browser), you can keep it exported but gate access with a custom signature-level permission:

```xml
<!-- Declare the permission in the app's manifest -->
<permission
    android:name="org.owasp.mastestapp.ACCESS_SECRET"
    android:protectionLevel="signature" />

<!-- Require it on the activity -->
<activity
    android:name="org.owasp.mastestapp.MastgTest$SecretActivity"
    android:exported="true"
    android:permission="org.owasp.mastestapp.ACCESS_SECRET" />
```

With `protectionLevel="signature"`, only apps signed with the same certificate are granted the permission automatically. A real-world example is a banking app that exposes a payment-confirmation activity to its own companion wearable app — both are signed with the bank's certificate, so only the wearable can start the activity, while any third-party app is rejected by the OS before `onCreate` is even called.

Trying to start `SecretActivity` again with `adb` after this change will fail with a different error, confirming that the activity is still exported but now requires a permission that the calling app does not have:

```bash
adb shell am start -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\\$SecretActivity'
Starting: Intent { cmp=org.owasp.mastestapp/.MastgTest$SecretActivity }

Exception occurred while executing 'start':
java.lang.SecurityException: Permission Denial: starting Intent { flg=0x10000000 xflg=0x4 cmp=org.owasp.mastestapp/.MastgTest$SecretActivity } from null (pid=29880, uid=2000) requires org.owasp.mastestapp.ACCESS_SECRET
```

**Why not rely solely on the PIN check in the calling activity?:**

Enforcing authentication only in `PinEntryActivity` and trusting that `SecretActivity` is always reached through it is a broken client-side control. Android's activity model makes no such guarantee: any exported activity can be started directly. Authentication state must be checked inside the activity that performs the sensitive operation, or the activity must not be exported.
