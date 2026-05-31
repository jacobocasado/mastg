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