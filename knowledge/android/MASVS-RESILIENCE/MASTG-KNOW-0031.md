---
masvs_category: MASVS-RESILIENCE
platform: android
title: Emulator Detection
---

In the context of anti-reversing, the goal of emulator detection is to increase the difficulty of running the app on an emulated device.  This increased difficulty forces the reverse engineer to defeat the emulator checks or utilize the physical device, thereby barring the access required for large-scale device analysis.

!!! note
  Emulator detection is inherently a cat-and-mouse game. Detection methods and bypass techniques evolve continuously-determined attackers with sufficient time and resources can circumvent these protections, for example, by developing custom AOSP builds. These techniques should be part of a defense-in-depth strategy, not a standalone solution.

There are several indicators that the device in question is being emulated.

## Build characteristics

Apps can read build properties through [`android.os.Build`](https://developer.android.com/reference/android/os/Build). Emulators often ship with default or vendor-specific values.

| Field | Example emulator values or patterns |
| --- | --- |
| `Build.FINGERPRINT` | starts with `generic`, contains `test-keys`, contains `generic/sdk/generic`, `generic x86`, `vbox86p`, `ttvm` |
| `Build.MODEL` | `sdk`, `google_sdk`, `emulator`, `android sdk built for x86`, `android sdk built for x86_64`, `droid4x`, `tiantianvm`, `genymotion`, `andy`, `nox` |
| `Build.MANUFACTURER` | `unknown`, `genymotion`, `droid4x`, `tiantianvm`, `andy` |
| `Build.HARDWARE` | `goldfish`, `ranchu`, `vbox86`, `nox`, `ttvm` |
| `Build.PRODUCT` | `sdk`, `google_sdk`, `sdk_x86`, `sdk_google`, `vbox86p`, `droid4x`, `andy`, `ttvm`, `nox`, starts with `itoolsavm` |
| `Build.BRAND` / `Build.DEVICE` | start with `generic`, include `generic x86`, `vbox86p`, `ttvm`, `andy`, `nox` |
| `Build.BOARD` | `unknown`, contains `nox` |
| `Build.SERIAL` | `null`, `unknown`, contains `nox` |
| `Build.ID` | `frf91` |
| `Build.RADIO` | `unknown` |
| `Build.TAGS` | `test-keys` |
| `Build.USER` | `android-build` |

Notes: It is recommended to normalize to lowercase when checking these values.

## Telephony characteristics

Check telephony only when the device reports [`FEATURE_TELEPHONY`](https://developer.android.com/reference/android/content/pm/PackageManager#FEATURE_TELEPHONY). Emulators often return fixed identifiers through [`TelephonyManager`](https://developer.android.com/reference/android/telephony/TelephonyManager).

| Method | Example emulator values |
| --- | --- |
| `getLine1Number()` | `15555215554` ... `15555215584` (even suffixes) |
| `getNetworkOperatorName()` | `android` |
| `getVoiceMailNumber()` | `15552175049` |

Permission notes:

- `getLine1Number()` requires `READ_PHONE_NUMBERS` or `READ_SMS`.
- `getVoiceMailNumber()` requires `READ_PHONE_STATE`.

## Package name indicators

Use [`PackageManager`](https://developer.android.com/reference/android/content/pm/PackageManager) to inspect installed packages or launcher activities for known emulator prefixes:

`com.vphone.`, `com.bignox.`, `com.nox.mopen.app`, `me.haima.`, `com.bluestacks`, `cn.itools.`, `com.kop.`, `com.kaopu.`, `com.microvirt.`, `com.bignox.app`, and the exact package `com.google.android.launcher.layouts.genymotion`. Some checks also flag `Build.PRODUCT` starting with `iToolsAVM`.

On Android 11 and later, [package visibility restrictions](https://developer.android.com/training/package-visibility) affect package-based emulator detection. If a package is installed but not visible to the app, [`getPackageInfo`](https://developer.android.com/reference/android/content/pm/PackageManager#getPackageInfo(java.lang.String,%20int)) behaves the same as if the package were not installed, typically by throwing [`PackageManager.NameNotFoundException`](https://developer.android.com/reference/android/content/pm/PackageManager.NameNotFoundException). This can create false negatives for package-based emulator detection.

Developers can query specific packages on Android 11 and later by declaring them in the app manifest using the `<queries>` element:

```xml
<queries>
    <package android:name="com.bignox.app" />
</queries>
```

Otherwise they can use the `QUERY_ALL_PACKAGES` permission, which grants visibility to all installed apps but is [subject to Google Play restrictions](https://support.google.com/googleplay/android-developer/answer/10158779) and may not be justifiable for many use cases.

## Available activities and services

Apps can query launcher activities with [`Intent.ACTION_MAIN`](https://developer.android.com/reference/android/content/Intent#ACTION_MAIN) and [`Intent.CATEGORY_LAUNCHER`](https://developer.android.com/reference/android/content/Intent#CATEGORY_LAUNCHER) and look for emulator package prefixes (often `com.bluestacks.`).

Intent-based discovery is also affected by Android 11+ package visibility restrictions. Apps should declare the matching intent signature in the `<queries>` element so [`queryIntentActivities`](https://developer.android.com/reference/android/content/pm/PackageManager#queryIntentActivities(android.content.Intent,%20int)) can return relevant launcher activities:

```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent>
</queries>
```

[`ActivityManager.getRunningServices`](https://developer.android.com/reference/android/app/ActivityManager#getRunningServices(int)) is restricted on API 26+ and only returns the app's own services.

## File system artifacts

Apps can check for emulator-specific files, sockets, and device nodes using standard file APIs such as [`java.io.File.exists()`](https://developer.android.com/reference/java/io/File#exists()). Common examples include:

| Path | Description |
| --- | --- |
| `/dev/socket/qemud` | QEMU daemon socket |
| `/dev/qemu_pipe` | QEMU communication pipe |
| `/dev/goldfish_pipe` | Goldfish emulator pipe |
| `/sys/qemu_trace` | QEMU trace marker |
| `/dev/socket/genyd` | Genymotion daemon socket |
| `/dev/socket/baseband_genyd` | Genymotion baseband socket |

## OpenGL renderer

Create an EGL context and read [`GLES20.glGetString(GL_RENDERER)`](https://developer.android.com/reference/android/opengl/GLES20#glGetString(int)). Renderer strings that contain `Bluestacks` or `Translator` can indicate emulators.

## Deprecated or advanced techniques

Non-resettable identifiers (IMEI/MEID, SIM serial, subscriber ID) are restricted for third-party apps targeting Android 10+ and require privileged or carrier permissions. In practice, `TelephonyManager.getDeviceId()`, `getSimSerialNumber()`, and `getSubscriberId()` return empty values or fail for regular apps. See [Android 10 privacy changes](https://developer.android.com/about/versions/10/privacy/changes#non-resettable-device-ids).

`netcfg`-based IP detection no longer works on modern Android (deprecated since API 23).

## Google Play Integrity API

The app can also use Google Play Integrity API to obtain details of the device integrity. This API performs checks, including emulator detection. See @MASTG-KNOW-0035 for more details on the Google Play Integrity API.
