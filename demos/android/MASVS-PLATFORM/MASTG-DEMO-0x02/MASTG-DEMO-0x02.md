---
title: Credential Change Through an Exported Service
platform: android
id: MASTG-DEMO-0x02
code: [kotlin]
test: MASTG-TEST-0x02
---

## Sample

The sample implements a small password vault. Tapping **Start** opens `VaultActivity`, which displays the password currently stored in the app (`originalPass123` on first run). The app also declares `AuthService`, a started service that reads a new password from the intent extras passed to `onStartCommand` and writes it to shared preferences. `AuthService` is declared as exported in the `AndroidManifest.xml` with no `android:permission`, so any app can start it and reset the password. Tapping **Refresh** in `VaultActivity` then shows the new value.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
2. Use @MASTG-TECH-0x02 to list the exported services.

The `run.sh` script lists the exported services declared in the reverse-engineered manifest.

{{ run.sh }}

## Observation

The output lists the services declared as exported in the manifest.

{{ output.txt }}

`AuthService` is exported (`android:exported="true"`) and declares no `android:permission`.

## Evaluation

The test case fails because `AuthService` exposes a security-relevant operation (changing the vault password) and is exported without verifying the caller.

The service changes the stored password from an intent extra in `onStartCommand` without calling `checkCallingPermission` or otherwise checking the caller:

```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val newPassword = intent?.getStringExtra(KEY_PASSWORD)
    if (newPassword != null) {
        applicationContext.getSharedPreferences(MastgTest.PREFS, Context.MODE_PRIVATE)
            .edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply()
    }
    return START_NOT_STICKY
}
```

Because it's exported and unprotected, any app can start it and overwrite the password.

### Confirm the Exposure

Use @MASTG-TECH-0x02 with `adb` to start `AuthService` directly and pass a new password as an intent extra:

1. Tap **Start** and note the current password shown in `VaultActivity` (`originalPass123`).
2. Start the exported service with a new password:

    ```bash
    adb shell am startservice -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$AuthService' --es org.owasp.mastestapp.PASSWORD hacked123                                             

    Starting service: Intent { cmp=org.owasp.mastestapp/.MastgTest$AuthService (has extras) } 
    ```

3. Return to the app and tap **Refresh**. The vault password now shows `hacked123`, confirming that an external caller changed it through the exported service.

## Fix

There are two ways to fix this, depending on whether `AuthService` needs to accept commands from external apps.

**Option 1: Set `android:exported="false"` (recommended)**

If the service is only ever started by the app itself (the most common case for a password vault), remove external access entirely:

```xml
<service
    android:name="org.owasp.mastestapp.MastgTest$AuthService"
    android:exported="false" />
```

Trying to start `AuthService` again with `adb` after this change will fail with an error, confirming that the service is no longer reachable from outside the app:

```bash
adb shell am startservice -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$AuthService' --es org.owasp.mastestapp.PASSWORD hacked123
Starting service: Intent { cmp=org.owasp.mastestapp/.MastgTest$AuthService (has extras) }
Error: Requires permission not exported from uid 10225
```

This is the correct fix for any service that manages internal state — credentials, session tokens, sync state — that no external app should be able to influence. The OS will reject any external `startService` or `bindService` call before it reaches `onStartCommand`.

**Option 2: Keep `android:exported="true"` but enforce a `android:permission`**

If the service must accept commands from a trusted partner app (for example, a separate authenticator app from the same developer), gate access with a custom signature-level permission:

```xml
<permission
    android:name="org.owasp.mastestapp.USE_AUTH_SERVICE"
    android:protectionLevel="signature" />

<service
    android:name="org.owasp.mastestapp.MastgTest$AuthService"
    android:exported="true"
    android:permission="org.owasp.mastestapp.USE_AUTH_SERVICE" />
```

Trying to start `AuthService` again with `adb` after this change will fail with a different error, confirming that the service is still exported but now requires a permission that the calling app does not have:

```bash
adb shell am startservice -n 'org.owasp.mastestapp/org.owasp.mastestapp.MastgTest\$AuthService' --es org.owasp.mastestapp.PASSWORD hacked123
Starting service: Intent { cmp=org.owasp.mastestapp/.MastgTest$AuthService (has extras) }
Error: Requires permission org.owasp.mastestapp.USE_AUTH_SERVICE
```

A real-world example is an enterprise MDM agent that exposes a configuration service to a companion management app — both are signed with the enterprise certificate, so only the management app can send commands, while any other app on the device is blocked.

**Option 3: Validate the caller inside `onStartCommand`**

If using a system-defined permission such as a dangerous or normal permission is unavoidable, the service can also call `checkCallingOrSelfPermission` to verify the caller at runtime. However, this is harder to get right than a manifest-level permission and should be treated as an additional defence-in-depth measure rather than the primary control.