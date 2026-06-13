---
title: Detecting Virtual Device Detection Checks with Frida
platform: ios
code: [swift]
id: MASTG-DEMO-00XX
test: MASTG-TEST-0x92
kind: pass
---

## Sample

The snippet below shows sample code that performs virtual device indicator checks and logs the queried values and matches against common virtual device values (see @MASTG-KNOW-009x for more information about common virtual device checks and values).

The checks query device properties and the Apple Metal GPU availability as mechanisms to detect all kinds of virtual devices, and a specific check for the @MASTG-TOOL-0108 virtual device.

!!! note
    The sample avoids NFC and Bluetooth checks because they can require additional entitlements, usage descriptions, or user-controlled state, which would make the demo less deterministic.

!!! note
    The sample avoids App Attest checks because they require server-side validation, which breaks the self-contained requirement for MASTG demos.

{{ MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Tap the **Start** button.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

The output shows all virtual device detection API calls captured during app execution.

{{ output.txt }}

The runtime trace shows that tapping the **Start** button triggers the following checks:

- `sysctlbyname("hw.machine")` is called and returns `iPhone10,6`. The trace shows one early framework-level call from CoreFoundation and one app-level call from `VirtualDeviceDetector.currentMachineIdentifier()`, confirming that the sample queries the runtime device model.
- `MTLCreateSystemDefaultDevice()` is called from `VirtualDeviceDetector.metalIndicator()` and returns `available`, confirming that the sample checks for Metal GPU availability.
- `stat("/usr/libexec/corelliumd")` is called from `VirtualDeviceDetector.corelliumDaemonExists()` and returns `missing`, confirming that the sample checks for the specific @MASTG-TOOL-0108 virtual device daemon file.

## Evaluation

The test passes because the output confirms the app implements virtual device detection checks at runtime:

- **`sysctlbyname("hw.machine")` call for device property checks:**
    - The app queries the device model identifier.
    - The reported model can be cross-checked against hardware capabilities observed at runtime.

- **`MTLCreateSystemDefaultDevice()` call for hardware capability checks:**
    - The app checks whether a Metal GPU is available.

- **`stat("/usr/libexec/corelliumd")` call for virtualization-engine file checks:**
    - The app checks for the @MASTG-TOOL-108 (virtual device) daemon file.
