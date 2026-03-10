---
title: Runtime Use of Virtual Device Detection Indicators with Frida
platform: ios
code: [swift]
id: MASTG-DEMO-00XX
test: MASTG-TEST-0x92
---

## Sample

The sample executes the local virtual-device indicators described in @MASTG-KNOW-009x and prints the result of each indicator together with a final verdict in the UI.

{{ MastgTest.swift }}

## Steps

1. Install the app on a physical device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Tap **Start** in the app.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

## Observation

{{ output.txt }}

The output should contain runtime evidence for every local indicator executed by the app: `MTLCreateSystemDefaultDevice`, `sysctlbyname("hw.machine")`, and `stat("/usr/libexec/corelliumd")`.

## Evaluation

The test passes because the app executes the selected local indicators from @MASTG-KNOW-009x at runtime and the Frida output shows those calls being triggered.

This demo does not require any extra privacy permissions because it only checks `hw.machine`, Metal availability, and the presence of `/usr/libexec/corelliumd`.
