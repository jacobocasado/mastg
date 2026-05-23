---
platform: android
title: Emulator Detection Checks
id: MASTG-TEST-0x49
type: [dynamic, hooks]
weakness: MASWE-0099
best-practices: [MASTG-BEST-00ea]
profiles: [R]
knowledge: [MASTG-KNOW-0031]
---

## Overview

This test verifies whether the app actively executes emulator detection checks at runtime. An attacker who runs the app in an emulator can more easily leverage dynamic analysis tools and techniques that aren't practical on physical devices, making it important that effective emulator detection is actually triggered during execution.

See @MASTG-KNOW-0031 for more information on emulator detection techniques and specific APIs and artifacts to look for.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to trace emulator detection API calls.
3. Exercise the app extensively to trigger emulator detection checks.

## Observation

The output should contain any instances of emulator detection checks, along with the methods or APIs that were hooked.

## Evaluation

The test case fails if no instances of emulator detection checks are observed.
