---
platform: android
title: References to Obfuscation Mechanisms in the Java/Kotlin Layer
id: MASTG-TEST-0x51-1
type: [static]
weakness: MASWE-0089
best-practices: [MASTG-BEST-0029]
prerequisites:
- identify-security-relevant-contexts
profiles: [R]
knowledge: [MASTG-KNOW-0033]
---

## Overview

If security-relevant Java or Kotlin code is not obfuscated, decompilation of the app's DEX bytecode can expose business logic, device attestation and environment checks, integrity checks, and other implementation details that help an attacker understand the app and model attacks.

This test checks whether security-relevant Java or Kotlin logic is protected with obfuscation techniques, or whether it remains easy to understand and correlate in the decompiled output.

Use @MASTG-KNOW-0033 as reference for common obfuscation mechanisms and indicators to look for.

## Steps

1. Perform @MASTG-TECH-0017 to decompile the app's DEX bytecode.
2. Perform @MASTG-TECH-0023 to review the decompiled Java or Kotlin code. If the decompiled output is incomplete or unreliable, run @MASTG-TECH-0016 to inspect the corresponding Smali code.
3. Use @MASTG-KNOW-0033 as reference to identify whether the reviewed Java or Kotlin code shows indicators of obfuscation, or whether security-relevant logic remains easy to understand and correlate. @MASTG-TOOL-0009 can be used to obtain compiler, obfuscator, or packer hints that can help guide the manual review.
4. Correlate the findings and determine whether security-relevant Java or Kotlin logic remains readily understandable enough to support attack modeling, bypassing, or reverse engineering of the application.

## Observation

The output should contain the reviewed security-relevant Java or Kotlin code locations and any indicators showing whether those locations are obfuscated or remain easy to understand and correlate.

## Evaluation

The test case fails if security-relevant Java or Kotlin logic remains sufficiently readable that an attacker can directly understand the logic, correlate it with sensitive functionality, or use it to model attacks.

The test case also fails if security-relevant logic is present but the observed protections are too weak to prevent straightforward recovery of identifiers, strings, control flow, or other implementation details relevant to reverse engineering. Findings should be interpreted in the context of the app's threat model and the sensitivity of the reviewed code.
