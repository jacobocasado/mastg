---
masvs_category: MASVS-RESILIENCE
platform: android
title: Emulator Detection
---

In the context of anti-reversing, the goal of emulator detection is to increase the difficulty of running the app on an emulated device, which impedes some tools and techniques reverse engineers like to use. This increased difficulty forces the reverse engineer to defeat the emulator checks or utilize the physical device, thereby barring the access required for large-scale device analysis.

There are several indicators that the device in question is being emulated. Although all these API calls can be hooked, these indicators provide a modest first line of defense.

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

Notes: normalize to lowercase and compare; vendors can change these values on rooted devices or custom ROMs. You can also edit `build.prop` on a rooted device or rebuild AOSP with custom values to bypass static string checks.

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

Hooking frameworks such as Frida or Xposed can hook these APIs and return false values. These are common indicators, but you may encounter different indicators and values in practice.

## Package name indicators

Use [`PackageManager`](https://developer.android.com/reference/android/content/pm/PackageManager) to inspect installed packages or launcher activities for known emulator prefixes:

`com.vphone.`, `com.bignox.`, `com.nox.mopen.app`, `me.haima.`, `com.bluestacks`, `cn.itools.`, `com.kop.`, `com.kaopu.`, `com.microvirt.`, `com.bignox.app`, and the exact package `com.google.android.launcher.layouts.genymotion`. Some checks also flag `Build.PRODUCT` starting with `iToolsAVM`.

Android 11+ filters package visibility. Declare `<queries>` for specific packages or intents, or use `QUERY_ALL_PACKAGES` only when justified; see [package visibility](https://developer.android.com/training/package-visibility).

## Available activities and services

You can query launcher activities with [`Intent.ACTION_MAIN`](https://developer.android.com/reference/android/content/Intent#ACTION_MAIN) and [`Intent.CATEGORY_LAUNCHER`](https://developer.android.com/reference/android/content/Intent#CATEGORY_LAUNCHER) and look for emulator package prefixes (often `com.bluestacks.`). [`ActivityManager.getRunningServices`](https://developer.android.com/reference/android/app/ActivityManager#getRunningServices(int)) is restricted on API 26+ and usually only returns the app's own services.

## OpenGL renderer

Create an EGL context and read [`GLES20.glGetString(GL_RENDERER)`](https://developer.android.com/reference/android/opengl/GLES20#glGetString(int)). Renderer strings that contain `Bluestacks` or `Translator` can indicate emulators.

## Deprecated or advanced techniques

Non-resettable identifiers (IMEI/MEID, SIM serial, subscriber ID) are restricted for third-party apps targeting Android 10+ and typically require privileged or carrier permissions. In practice, `TelephonyManager.getDeviceId()`, `getSimSerialNumber()`, and `getSubscriberId()` return empty values or fail for regular apps. See [Android 10 privacy changes](https://developer.android.com/about/versions/10/privacy/changes#non-resettable-device-ids).

`netcfg`-based IP detection no longer works on modern Android (deprecated since API 23). Vectorization-based detection requires NDK code and per-architecture assembly and is rarely used in apps.

## Google Play Integrity API

The app can also use Google Play Integrity API to obtain details of the device integrity. This API performs checks, including emulation detection. See @MASTG-KNOW-0035 for more details on the Google Play Integrity API.
