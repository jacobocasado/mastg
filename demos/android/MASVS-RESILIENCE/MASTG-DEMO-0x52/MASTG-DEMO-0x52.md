---
platform: android
title: Testing for Obfuscation in Native Libraries
id: MASTG-DEMO-0x52
code: [kotlin, cpp]
test: MASTG-TEST-0x51-2
tools: [MASTG-TOOL-0018, MASTG-TOOL-0028]
kind: fail
---

## Sample

The sample code demonstrates an Android app that performs a root detection routine implemented in a native library.

The Java/Kotlin layer loads `librootcheck.so` and calls `findRootArtifactPath()`, a native function that checks common `su` paths such as `/system/bin/su`, `/system/xbin/su`, and `/sbin/su`. If one of those paths is found, the app reports the detection result and closes itself by calling `finishAffinity()`.

The release APK ships a native library, which is a basic form of obfuscation. However, reverse engineering the packaged library still reveals the monitored `su` paths, the `access()`-based checking logic. As a result, an attacker could identify the root detection routine and target it for bypassing.

{{ MastgTest.kt # MastgTest_reversed.java # native-root-check.cpp }}

## Steps

1. Build and install the release APK.
2. Perform @MASTG-TECH-0029 to list first-party native libraries packaged in the APK.
3. Perform @MASTG-TECH-0018 to disassemble the native libraries that may contain security-relevant logic.
4. Perform @MASTG-TECH-0024 to review the disassembled native code.
5. Determine whether the obfuscation applied to the native layer still allows security-relevant logic to be identified and reverse engineered with reasonable effort (Use @MASTG-KNOW-0033 as reference of common obfuscation patterns).

The following script decompiles the APK with `jadx`, extracts `librootcheck.so` from the APK, and runs `radare2` to review the native library.

{{ run.sh }}

## Observation

The output contains the relevant locations in the extracted native library that show the monitored `su` paths and the native function that performs the checks.

{{ output.txt }}

## Evaluation

The test case fails because when performing reverse engineering on the native library it is possible to identify and understand the security-relevant root detection logic with little effort.

In `output.txt`, lines 4 to 6 still reveal the monitored root-related paths `/system/bin/su`, `/sbin/su`, and `/system/xbin/su`, so the strings are not encoded or encrypted.

In `output.txt`, lines 7 to 20 show the disassembled `Java_org_owasp_mastestapp_MastgTest_findRootArtifactPath` function, and lines 20 to 44 show repeated calls to `sym.imp.access` and comparisons against `0xd`, which is enough to understand that the library is checking monitored filesystem paths and treating permission-denied results as positive matches.

In `MastgTest_reversed.java`, lines 17, 29 to 37, 48 to 54, and 57 to 58 show that the Java/Kotlin layer loads `librootcheck.so`, calls the native `findRootArtifactPath()` function, logs the detected path, and closes the app through `finishAffinity()` when the native code reports a match.

```java
public final class MastgTest {
    private final native String findRootArtifactPath();

    public final String mastgTest() {
        ...
        String strFindRootArtifactPath = findRootArtifactPath();
        if (strFindRootArtifactPath != null) {
            String str = "Detected root artifact path '" + strFindRootArtifactPath + "'. The app closes when a monitored su path is found.";
            ...
            closeApp();
        }
    }

    static {
        System.loadLibrary("rootcheck");
    }
}
```

This means that the reverse-engineered native code still makes it straightforward to identify that the app is checking common `su` paths and closing the app when one of them is found.
