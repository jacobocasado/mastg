---
platform: iOS
title: Testing Virtual Device Checks 
id: MASTG-TEST-0x92
type: [dynamic]
weakness: MASWE-0099
best-practices: [MASTG-BEST-00eb]
profiles: [R]
knowledge: [MASTG-KNOW-009x]
---

## Overview

This test verifies that the app implements checks to detect the presence of an iOS virtual device (like Corellium) and that it executes such checks at runtime. The verifications are made at runtime, via runtime method hooking.

See @MASTG-KNOW-009x for a detailed overview about virtual device detection indicators and patterns performed by applications.

Note that this test verifies that the app performs the checks, and does not verify the behavior of the app when the indicators are triggered.

## Steps

1. Start the device.
2. Use @MASTG-TECH-0056 to install the app in the device.
3. Use runtime method hooking (see @MASTG-TECH-0039) to trace system and API calls related to virtual device detection (see @MASTG-KNOW-009x for a detailed list of indicators).
4. Capture the output, including any abrupt session termination events or errors.

## Observation

The output should contain evidence of virtual device detection checks being triggered at runtime.

## Evaluation

The test case fails if the output does not show any evidence of virtual device detection checks being triggered at runtime by the application.
