---
platform: android
title: References to Obfuscation Mechanisms in Native Libraries
id: MASTG-TEST-0x02
type: [static]
weakness: MASWE-0089
best-practices: [MASTG-BEST-0029]
profiles: [R]
knowledge: [MASTG-KNOW-0033]
---

## Overview

If first-party native libraries that implement security-relevant logic are not obfuscated, reverse engineering of packaged or loaded native code can expose business logic, device attestation and environment checks, integrity checks, and other implementation details that help an attacker understand the app and model attacks.

This test checks whether the obfuscation techniques applied to first-party native libraries prevent straightforward identification, correlation, and reverse engineering of security-relevant logic through strings, constants, call structure, or control flow.

Use @MASTG-KNOW-0033 as reference for common native obfuscation mechanisms and indicators to look for.

!!! note
    If the app does not include first-party native libraries, this test is not applicable.

## Steps

1. Use @MASTG-TECH-0157 to extract the native libraries from the app package.
2. Perform @MASTG-TECH-0018 to disassemble the native libraries that may contain security-relevant logic.
3. Perform @MASTG-TECH-0024 to review the disassembled native code.
4. Use @MASTG-KNOW-0033 as reference to identify whether the reviewed native code shows indicators of obfuscation, and whether those indicators prevent straightforward identification and correlation of security-relevant logic. Perform @MASTG-TOOL-0009 on the APK to obtain compiler, obfuscator, or packer hints that can help guide the manual review.
5. Correlate the findings and determine whether the obfuscation applied to the native layer still allows security-relevant logic to be identified and reverse engineered with reasonable effort.

## Observation

The output should contain the identified first-party native libraries, the candidate native code locations, the indicators used to correlate them with security-relevant functionality, and any findings showing whether that logic can still be identified and reverse engineered with reasonable effort.

## Evaluation

The test case fails if the app's first-party native libraries allow an attacker to identify, correlate, and reverse engineer security-relevant logic with reasonable effort despite the obfuscation mechanisms present.

The test case also fails if the observed protections are too weak to prevent straightforward recovery of strings, constants, call edges, or control flow that allow security-relevant functionality to be located and understood.Findings should be interpreted in the context of the app's threat model and the sensitivity of the reviewed code.
