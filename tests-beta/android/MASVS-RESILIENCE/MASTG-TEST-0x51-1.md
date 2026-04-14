---
platform: android
title: References to Obfuscation Mechanisms in the Java/Kotlin Layer
id: MASTG-TEST-0x51-1
type: [static]
weakness: MASWE-0089
best-practices: [MASTG-BEST-0029]
profiles: [R]
knowledge: [MASTG-KNOW-0033]
---

## Overview

If security-relevant Java or Kotlin code is not obfuscated, decompilation of the app's DEX bytecode can expose business logic, device attestation and environment checks, integrity checks, and other implementation details that help an attacker understand the app and model attacks.

This test checks whether the obfuscation techniques applied to the Java or Kotlin layer prevent straightforward identification, correlation, and reverse engineering of security-relevant logic in the decompiled output.

Use @MASTG-KNOW-0033 as reference for common obfuscation mechanisms and indicators to look for.

## Steps

1. Perform @MASTG-TECH-0017 to decompile the app's DEX bytecode.
2. Perform @MASTG-TECH-0023 to review the decompiled Java or Kotlin code. If the decompiled output is incomplete or unreliable, run @MASTG-TECH-0016 to inspect the corresponding Smali code.
3. Use @MASTG-KNOW-0033 as reference to identify whether the reviewed Java or Kotlin code shows indicators of obfuscation, and whether those indicators prevent straightforward identification and correlation of security-relevant logic. Perform @MASTG-TOOL-0009 to obtain compiler, obfuscator, or packer hints that can help guide the manual review.
4. Correlate the findings and determine whether the obfuscation applied to the Java or Kotlin layer still allows security-relevant logic to be identified and reverse engineered with reasonable effort.

## Observation

The output should contain the candidate Java or Kotlin code locations, the indicators used to correlate them with security-relevant functionality, and any findings showing whether that logic can still be identified and reverse engineered with reasonable effort.

## Evaluation

The test case fails if the Java or Kotlin layer allows an attacker to identify, correlate, and reverse engineer security-relevant logic with reasonable effort despite the obfuscation mechanisms present.

The test case also fails if the observed protections are too weak to prevent straightforward recovery of identifiers, strings, control flow, or other implementation details that allow security-relevant functionality to be located and understood. Findings should be interpreted in the context of the app's threat model and the sensitivity of the reviewed code.
