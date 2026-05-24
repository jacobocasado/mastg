---
id: MASTG-DEMO-XXXD
title: Path Traversal via Malicious ContentProvider Filename
platform: android
code: [kotlin]
tools: [MASTG-TOOL-0001, MASTG-TOOL-0145]
kind: fail
---

## Sample

The following sample code demonstrates how an application can be vulnerable when handling results from implicit intents. The application requests a file using a custom implicit intent (`org.owasp.mastestapp.REQUEST_FILE`) and attempts to save it within its internal `filesDir/public/` directory using the filename provided by the returning `ContentProvider`.

However, because the filename (`_display_name`) is used directly in a `File` instantiation without sanitization, an attacker can supply a path-traversal string (like `../private/secret.txt`) to reach outside the intended `public/` directory and overwrite sensitive files in the `private/` folder.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml # AndroidManifest_reversed.xml }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Interact with the app to trigger the file request (e.g., click the **Start** button and select the malicious file provider)
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ hooks.json # run.sh }}

## Observation

The output shows all instances of `File` construction and `FileOutputStream` initialization found at runtime, along with the parameters provided. A backtrace is also provided to help identify the location in the code.

{{ output.json }}

## Evaluation

The test case fails because the application uses the filename returned by the attacker's `ContentProvider` directly in a `File` constructor without sanitization, allowing the attacker to redirect the write operation outside the intended directory.

Two entries in the output confirm the path traversal:

- `java.io.File.$init` called from `onActivityResult` with arguments `files/public` and `../private/secret.txt` — the unsanitized attacker-controlled filename causes the resolved path to escape the `public/` directory.
- `java.io.FileOutputStream.$init` called with the fully resolved path `files/public/../private/secret.txt`, confirming that the file write targets the `private/` directory rather than `public/`.
