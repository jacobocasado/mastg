---
platform: android
title: Detecting Emulator Detection Checks with Frida
id: MASTG-DEMO-0x86
code: [kotlin]
test: MASTG-TEST-0x49
tools: [MASTG-TOOL-0031]
---

### Sample

The snippet below shows sample code that performs common emulator indicator checks and logs the queried values and matches against common emulator values (see @MASTG-KNOW-0031 for more information about common emulator checks and emulator values).

The checks cover several categories (build properties, telephony identifiers, package visibility, and OpenGL renderer information).

Notes about the checks performed:

- The sample avoids `PackageManager.getInstalledPackages()` because Android 11+ requires the `QUERY_ALL_PACKAGES` permission to access the full installed app inventory. Google Play treats that inventory as sensitive and allows it only for apps with a strong, declared need. Instead, this demo uses launcher package queries and explicit checks for known emulator packages.
- The sample avoids Play Integrity checks (@MASTG-KNOW-0035) because they require Play Console configuration and server-side verification, which breaks the self-contained requirement for MASTG demos.
- The manifest declares `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` so the runtime permission prompts can be shown before querying telephony values, and it includes `<queries>` entries for package visibility checks.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

1. Install the app on the device (@MASTG-TECH-0005). It does not need to be an emulated device.
2. Start Frida (@MASTG-TOOL-0031) on the device and run `run.sh` to spawn the app.
3. Open the app and grant the `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` permissions when prompted, then tap **Start**.
4. Stop the Frida session by pressing `Ctrl+C`.

{{ run.sh }}

{{ script.js }}

### Observation

The output shows all runtime calls to emulator detection APIs, such as `TelephonyManager` identifiers, `PackageManager` queries, `ActivityManager.getRunningServices`, `GLES20.glGetString`, and the `Build` checks performed by the app.

{{ output.txt }}

### Evaluation

The test passes because Frida confirms that the app performs emulator detection checks at runtime (by triggering several functions related to emulator detection) and queries the expected indicators.

