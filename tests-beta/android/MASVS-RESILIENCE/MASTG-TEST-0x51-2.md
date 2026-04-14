---
platform: android
title: References to Obfuscation Mechanisms in Native Libraries
id: MASTG-TEST-0x51-2
type: [static]
weakness: MASWE-0089
best-practices: [MASTG-BEST-0029]
prerequisites:
- identify-security-relevant-contexts
profiles: [R]
knowledge: [MASTG-KNOW-0033]
---

## Overview

If first-party native libraries that implement security-relevant logic are not obfuscated, reverse engineering of packaged or loaded native code can expose business logic, device attestation and environment checks, integrity checks, and other implementation details that help an attacker understand the app and model attacks.

This test checks whether the native libraries that contain security-relevant logic in the app are protected with native obfuscation techniques, or whether they remain easy to analyze through symbols, strings, constants, call structure, or control flow.

Use @MASTG-KNOW-0033 as reference for common native obfuscation mechanisms and indicators to look for.

!!! note
    If the app does not include first-party native libraries, this test is not applicable.

## Steps

1. Perform @MASTG-TECH-0029 to list first-party native libraries packaged in the APK.
2. Perform @MASTG-TECH-0018 to disassemble the native libraries that implement security-relevant logic.
3. Perform @MASTG-TECH-0024 to review the disassembled native code.
4. Perform @MASTG-TECH-0140 to inspect symbols and debugging information that may make the native code easier to analyze.
5. Use @MASTG-KNOW-0033 as reference to identify whether the reviewed native code shows indicators of obfuscation, or whether security-relevant logic remains easy to understand and correlate. @MASTG-TOOL-0009 can be used in the APK to obtain compiler, obfuscator, or packer hints that can help guide the manual review.
6. Correlate the findings and determine whether security-relevant native code remains readily understandable enough to support reverse engineering of the application.

## Observation

The output should contain the identified first-party native libraries, the reviewed security-relevant code locations, and any indicators showing whether those locations are obfuscated or remain easy to understand and correlate.

## Evaluation

The test case fails if first-party native libraries containing security-relevant logic remain sufficiently readable that an attacker can directly understand the logic, correlate it with sensitive functionality, or use it to model attacks.

The test case also fails if security-relevant native logic is present but the observed protections are too weak to prevent straightforward recovery of strings, constants, symbols, call edges, or control flow relevant to reverse engineering.
