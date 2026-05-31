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
