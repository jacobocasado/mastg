---
platform: android
title: Emulator Detection Checks
id: MASTG-TEST-0x49
type: [static, dynamic]
weakness: MASWE-0099
mitigations: []
prerequisites: []
profiles: [R]
knowledge: [MASTG-KNOW-0031, MASTG-KNOW-0035]
---

## Overview

This test verifies that the app runs emulator detection checks on Android and modifies its behavior when it detects an emulated device. It focuses on code and runtime checks that look for emulator indicators and how the app reacts to such indicators.
It does not attempt to bypass these checks; it only verifies that the application executes them.

Common checks use [`android.os.Build`](https://developer.android.com/reference/android/os/Build) fields, [`TelephonyManager`](https://developer.android.com/reference/android/telephony/TelephonyManager) identifiers, package visibility via [`PackageManager`](https://developer.android.com/reference/android/content/pm/PackageManager), or OpenGL renderer values from [`GLES20.glGetString`](https://developer.android.com/reference/android/opengl/GLES20#glGetString(int)). If the app uses Play Integrity for emulator detection, it should evaluate `deviceRecognitionVerdict` values such as `MEETS_VIRTUAL_INTEGRITY`. See @MASTG-KNOW-0031 for more detail on common emulation indicators.

Threat model: an attacker runs the app on an emulator and can instrument or hook the process.

## Steps

1. Use @MASTG-TECH-0014 with a tool such as @MASTG-TOOL-0110 to search for emulator indicators from @MASTG-KNOW-0031, such as:
   - `Build` property checks for emulator values (`Build.FINGERPRINT`, `Build.MODEL`, `Build.HARDWARE`, `Build.PRODUCT`, `Build.TAGS`).
   - `TelephonyManager` calls like `getLine1Number`, `getNetworkOperatorName`, or `getVoiceMailNumber` ([TelephonyManager](https://developer.android.com/reference/android/telephony/TelephonyManager)).
   - Package checks using `PackageManager` (installed packages, `queryIntentActivities` with `MAIN/LAUNCHER`, `getRunningServices`) and `Build.PRODUCT` prefixes like `iToolsAVM` ([PackageManager](https://developer.android.com/reference/android/content/pm/PackageManager), [package visibility](https://developer.android.com/training/package-visibility)).
   - OpenGL renderer checks using `GLES20.glGetString(GL_RENDERER)` or EGL context creation ([GLES20](https://developer.android.com/reference/android/opengl/GLES20#glGetString(int))).
   - If the app uses Play Integrity, verify that it evaluates `deviceRecognitionVerdict` and handles `MEETS_VIRTUAL_INTEGRITY` responses ([Play Integrity](https://developer.android.com/google/play/integrity/overview)).
2. Obtain the AndroidManifest.xml using @MASTG-TECH-0117 and verify that package visibility declarations (`<queries>` or `QUERY_ALL_PACKAGES`) and telephony permissions (`READ_PHONE_STATE`, `READ_PHONE_NUMBERS`) match the checks you found.
3. Use @MASTG-TECH-0033 with a tool such as @MASTG-TOOL-0001 to trace runtime calls to emulator detection APIs while running the app on a device and on an emulator. Verify that the declared emulation indicators in the code are dynamically performed and that the application responds to emulator signals (ending its execution, restricting access to certain features, or just informing the user).

## Observation

The output should contain evidence of emulator detection checks, such as code locations, method traces, or logs, and show the app response to emulator signals (warning, restriction, termination, or telemetry).

## Evaluation

The test case fails if the app does not implement emulator detection checks or if those checks never execute at runtime.
