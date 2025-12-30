---
platform: android
title: Detecting Emulator Detection checks with Frida
id: MASTG-DEMO-0086
code: [kotlin]
test: MASTG-TEST-0x49
tools: [MASTG-TOOL-0001, MASTG-TOOL-0004]
---

### Sample

This sample app queries emulator indicators from `android.os.Build`, `TelephonyManager`, installed packages, and the `OpenGL` renderer. It logs the raw values and then reports which values match known emulator artifacts. The comparisons are performed on lowercased values to avoid case mismatches. The demo uses Frida to trace the runtime API calls while the checks execute to verify that the application perform such checks at runtime.

Indicators and comparisons included in the sample:

Build properties

| Indicator | Exact match | Prefix match | Contains |
| --- | --- | --- | --- |
| `Build.FINGERPRINT` |  | `generic` | `test-keys`, `generic/sdk/generic`, `generic x86`, `vbox86p`, `ttvm` |
| `Build.MODEL` |  |  | `sdk`, `google_sdk`, `emulator`, `android sdk built for`, `droid4x`, `tiantianvm`, `genymotion`, `andy`, `nox` |
| `Build.MANUFACTURER` |  |  | `unknown`, `genymotion`, `droid4x`, `tiantianvm`, `andy` |
| `Build.HARDWARE` |  |  | `goldfish`, `ranchu`, `vbox86`, `nox`, `ttvm` |
| `Build.PRODUCT` |  | `itoolsavm` | `sdk`, `google_sdk`, `sdk_x86`, `sdk_google`, `vbox86p`, `droid4x`, `andy`, `ttvm`, `nox` |
| `Build.BRAND` |  | `generic` | `generic x86`, `ttvm`, `andy`, `nox` |
| `Build.DEVICE` |  | `generic` | `generic x86`, `vbox86p`, `ttvm`, `andy`, `nox`, `droid4x` |
| `Build.BOARD` | `unknown` |  | `nox` |
| `Build.SERIAL` | `null`, `unknown` |  | `nox` |
| `Build.ID` | `frf91` |  |  |
| `Build.RADIO` | `unknown` |  |  |
| `Build.TAGS` |  |  | `test-keys` |
| `Build.USER` | `android-build` |  |  |

Telephony properties

| Indicator | Exact match | Prefix match | Contains |
| --- | --- | --- | --- |
| `TelephonyManager.getLine1Number` | `15555215554`, `15555215556`, `15555215558`, `15555215560`, `15555215562`, `15555215564`, `15555215566`, `15555215568`, `15555215570`, `15555215572`, `15555215574`, `15555215576`, `15555215578`, `15555215580`, `15555215582`, `15555215584` |  |  |
| `TelephonyManager.getNetworkOperatorName` |  |  | `android` |
| `TelephonyManager.getVoiceMailNumber` | `15552175049` |  |  |

Packages and services

| Indicator | Exact match | Prefix match | Contains |
| --- | --- | --- | --- |
| Installed/launcher packages | `com.google.android.launcher.layouts.genymotion`, `com.nox.mopen.app`, `com.bignox.app`, `com.microvirt` | `com.vphone.`, `com.bignox.`, `com.nox.mopen.app`, `me.haima.`, `com.bluestacks`, `cn.itools.`, `com.kop.`, `com.kaopu.`, `com.microvirt.`, `com.bignox.app` |  |
| Running services |  | `com.bluestacks.` |  |

OpenGL properties

| Indicator | Exact match | Prefix match | Contains |
| --- | --- | --- | --- |
| `OpenGL.GL_RENDERER` |  |  | `bluestacks`, `translator` |

Checks that leverage Play Integrity are intentionally not included because it requires Play Console configuration and server-side verification, which breaks the self-contained requirement of the MASTG demo format.

{{ MastgTest.kt # MastgTest_reversed.java }}

The manifest declares `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` so the runtime permission prompts can be shown before querying telephony values, and it includes `<queries>` entries for package visibility checks.

{{ AndroidManifest.xml # AndroidManifest_reversed.xml }}

### Steps

1. Install the app on the device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0001 installed and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Open the app and tap **Start**. When prompted, grant `READ_PHONE_STATE` and `READ_PHONE_NUMBERS`, then tap **Start** again.
5. Stop the Frida session by pressing `Ctrl+C`.

{{ run.sh # script.js }}

### Observation

The output should show runtime calls to emulator detection APIs, such as `TelephonyManager` identifiers, `PackageManager` queries, `ActivityManager.getRunningServices`, `GLES20.glGetString`, and the `Build` checks performed by the app.

{{ output.txt }}

### Evaluation

The test passes because Frida confirms that the app executes emulator detection checks at runtime and queries the expected indicators. The app also displays that the device has indicators related to emulation when executing this demo in a emulated Android device.

### Environment

- Android: 16 (API 36)
- Device: Android Emulator (model `sdk_gphone64_arm64`)
- IDE: Android Studio (optional; demo built with Gradle 8.10.2)
- Frida: 17.5.2
