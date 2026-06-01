---
title: Sensitive Action Through an Exported Broadcast Receiver
platform: android
id: MASTG-DEMO-0x03
code: [kotlin]
test: MASTG-TEST-0x03
---

## Sample

The sample implements a small password vault. Tapping **Start** opens `VaultActivity`, which displays the password currently stored in the app (`originalPass123` on first run). The app also declares `PasswordResetReceiver`, a broadcast receiver that changes the stored password from an unvalidated `newpass` intent extra and logs the old password. `PasswordResetReceiver` is declared as exported in the `AndroidManifest.xml` with no `android:permission`, so any app can send the broadcast and reset the password. Tapping **Refresh** in `VaultActivity` then shows the new value.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0x03 to list the exported broadcast receivers.

The `run.sh` script lists the exported receivers declared in the reverse-engineered manifest.

{{ run.sh }}

## Observation

The output lists the broadcast receivers declared as exported in the manifest.

{{ output.txt }}

`PasswordResetReceiver` is exported (`android:exported="true"`) and declares no `android:permission`.

## Evaluation

The test case fails because `PasswordResetReceiver` performs a security-relevant action and is exported without restricting who can deliver to it.

The `onReceive` method changes the stored password from an unvalidated intent extra and discloses the old password to the log:

```kotlin
override fun onReceive(context: Context, intent: Intent) {
    val newPassword = intent.getStringExtra("newpass") ?: return
    val prefs = context.getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
    val oldPassword = prefs.getString(MastgTest.KEY_PASSWORD_STORE, "")
    Log.d("MASTG-DEMO", "Password changed from $oldPassword to $newPassword")
    prefs.edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply()
}
```

Because it's exported and unprotected, any app can send the broadcast and reset the password.

The output also lists `androidx.profileinstaller.ProfileInstallReceiver`. This receiver is added by the AndroidX Profile Installer library and, although exported, is protected by `android:permission="android.permission.DUMP"`, a signature/privileged permission that ordinary apps can't hold. It is development tooling and is not reported as vulnerable in this test case.

### Confirm the Exposure

You can use @MASTG-TECH-0x03 with @MASTG-TOOL-0004 to deliver the broadcast and trigger the action.

1. Tap **Start** and note the current password shown in `VaultActivity` (`originalPass123`).
2. Send the broadcast, targeting the receiver explicitly so it's delivered on modern Android:

    ```bash
    adb shell am broadcast -a org.owasp.mastestapp.RESET_PASSWORD -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$PasswordResetReceiver' --es newpass hacked123

    Broadcasting: Intent { act=org.owasp.mastestapp.RESET_PASSWORD flg=0x400000 cmp=org.owasp.mastestapp/.MastgTest$PasswordResetReceiver (has extras) }                                                                                     
    Broadcast completed: result=0
    ```

3. Return to the app and tap **Refresh**. The vault password now shows `hacked123`, confirming that an external app triggered the change.

The disclosed old password is also visible in the log:

```bash
adb logcat -s MASTG-DEMO
```

Output:

```
06-01 09:26:01.334 30881 30881 D MASTG-DEMO: Password changed from originalPass123 to hacked123
```

## Fix

There are two ways to fix this, depending on whether `PasswordResetReceiver` needs to accept broadcasts from external apps.

**Option 1: Set `android:exported="false"` (recommended)**

If the receiver only needs to handle broadcasts sent by the app itself (for example, using `LocalBroadcastManager` or an explicit internal intent), remove external access entirely:

```xml
<receiver
    android:name="org.owasp.mastestapp.MastgTest$PasswordResetReceiver"
    android:exported="false" />
```

Trying to send the broadcast again with `adb` after this change will not neccessarily produce an error, but the password will not change and the log will not show the old password, confirming that the receiver is no longer reachable from outside the app.

```bash
adb shell am broadcast -a org.owasp.mastestapp.RESET_PASSWORD -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$PasswordResetReceiver' --es newpass hacked123
Broadcasting: Intent { act=org.owasp.mastestapp.RESET_PASSWORD flg=0x400000 cmp=org.owasp.mastestapp/.MastgTest$PasswordResetReceiver (has extras) }
Broadcast completed: result=0
```

This is the correct fix for any receiver that reacts to internal app events. No other app can send a broadcast to a non-exported receiver. Since Android 8.0 (API 26), implicit broadcast receivers must be registered at runtime anyway, so most password-change or credential-reset events should be handled through explicit internal intents rather than exported static receivers.

**Option 2: Keep `android:exported="true"` but require a `android:permission`**

If the receiver must handle broadcasts from a trusted partner app (for example, a companion lock-screen app from the same developer that can trigger a remote wipe or credential reset), gate delivery with a custom signature-level send permission:

```xml
<!-- Declare the permission -->
<permission
    android:name="org.owasp.mastestapp.SEND_PASSWORD_RESET"
    android:protectionLevel="signature" />

<!-- Require it on the receiver -->
<receiver
    android:name="org.owasp.mastestapp.MastgTest$PasswordResetReceiver"
    android:exported="true"
    android:permission="org.owasp.mastestapp.SEND_PASSWORD_RESET" />
```

Trying to send the broadcast again with `adb` after this change will also not produce an error, but it won't have any effect:

```bash
adb shell am broadcast -a org.owasp.mastestapp.RESET_PASSWORD -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$PasswordResetReceiver' --es newpass hacked123
Broadcasting: Intent { act=org.owasp.mastestapp.RESET_PASSWORD flg=0x400000 cmp=org.owasp.mastestapp/.MastgTest$PasswordResetReceiver (has extras) }
Broadcast completed: result=0
```

With `protectionLevel="signature"`, the OS only delivers the broadcast if the sending app is signed with the same certificate. A real-world example is an enterprise remote-wipe receiver that only responds to broadcasts from the company's own device management app. Any third-party app that sends the broadcast without holding the permission is silently ignored.

**Additional fix: Also remove sensitive data from logs**

Regardless of whether the receiver itself is protected, no credentials must be written to the app logs, which are readable by any app that holds `READ_LOGS` (granted to shell and ADB).
