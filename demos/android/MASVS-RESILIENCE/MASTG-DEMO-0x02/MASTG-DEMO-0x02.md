---
platform: android
title: Root Detection Logic in Native Layer with Insufficient Obfuscation
id: MASTG-DEMO-0x02
code: [kotlin, cpp]
test: MASTG-TEST-0x02
kind: fail
---

## Sample

The sample code demonstrates an Android app that performs a root detection routine implemented in a native library.

The Java/Kotlin layer loads `librootcheck.so` and calls `findRootArtifactPath()`, a native function that checks common `su` binary paths. Regardless of whether this protection mechanism is sufficient to detect root in a real-world scenario, the purpose of this demo is to show that the obfuscation applied to the native layer is not enough to prevent an attacker from reverse engineering the root detection logic with reasonable effort.

{{ MastgTest.kt # native-root-check.cpp # CMakeLists.txt }}

The app is not obfuscated at the Java/Kotlin layer, and the native library is included in the APK as-is without any protection. See @MASTG-KNOW-0033 for reference on common obfuscation techniques.

## Steps

1. Use `unzip` to extract the native libraries. In this case `lib/arm64-v8a/librootcheck.so`.
2. Use @MASTG-TOOL-0028 to disassemble `librootcheck.so`.
3. Use @MASTG-TECH-0024 to inspect the disassembled native code to identify sections (`iS~dynsym,symtab,rodata,text`) and strings (`izz~su`) that may be relevant for root detection. We also analyze the function `Java_org_owasp_mastestapp_MastgTest_findRootArtifactPath` to understand the root detection logic implemented in the native library.

{{ run.sh }}

## Observation

The output contains the relevant locations in the extracted native library that show the monitored `su` paths and the native function that performs the checks.

{{ output.txt }}

## Evaluation

The test case fails because when performing reverse engineering on the native library it is possible to identify and understand the security-relevant root detection logic with little effort.

In `output.txt`:

- Lines 4 to 6 still reveal the monitored root-related paths `/system/bin/su`, `/sbin/su`, and `/system/xbin/su`, so the strings are not encoded or encrypted.
- Lines 7 to 20 show the disassembled `Java_org_owasp_mastestapp_MastgTest_findRootArtifactPath` function.
- Lines 20 to 44 show repeated calls to `sym.imp.access` and comparisons against `0xd`.

This means that the reverse-engineered native code still makes it straightforward to identify that the app is checking common `su` paths and closing the app when one of them is found.

**Additional Context**:

If we use @MASTG-TECH-0017 to decompile the app, we'll also see that the Java/Kotlin layer isn't obfuscated, and the code helps correlate the native code with the security-relevant logic. For example, in `MastgTest_reversed.java`, lines 17, 29 to 37, 48 to 54, and 57 to 58 show that the Java/Kotlin layer loads `librootcheck.so`, calls the native `findRootArtifactPath()` function, logs the detected path, and closes the app through `finishAffinity()` when the native code reports a match.

{{ MastgTest_reversed.java }}
