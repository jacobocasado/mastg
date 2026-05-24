---
platform: ios
title: References to Reverse Engineering Tools Detection with r2
code: [swift]
id: MASTG-DEMO-0117
test: MASTG-TEST-0354
status: draft
---

## Sample

The code snippet below shows sample code that detects the presence of reverse engineering tools at runtime. It checks for known Frida artifacts by iterating over loaded dynamic libraries using `_dyld_image_count` and `_dyld_get_image_name`, and probes TCP port 27042 for a frida-server response.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ reverse_engineering_tools_detection.r2 }}

{{ run.sh }}

## Observation

The output reveals references to known Frida artifact strings (`FridaGadget`, `frida-agent`, `cynject`, `libcycript`) and calls to `_dyld_image_count` and `_dyld_get_image_name` in the app binary. A reference to port `27042` (the default frida-server port) is also present.

{{ output.asm }}

## Evaluation

The test case passes because the output shows that the app implements reverse engineering tools detection: it scans loaded dynamic libraries for Frida-related artifact names using `_dyld_image_count`/`_dyld_get_image_name` and checks whether frida-server is reachable on port 27042.
