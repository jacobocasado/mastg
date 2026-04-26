---
platform: android
title: Emulator Detection Checks
id: MASTG-TEST-0x49
type: [dynamic]
weakness: MASWE-0099
best-practices: []
profiles: [R]
knowledge: [MASTG-KNOW-0031]
---

## Overview

This test verifies whether the app actively executes emulator detection checks at runtime. An attacker who runs the app in an emulator can more easily leverage dynamic analysis tools and techniques that aren't practical on physical devices, making it important that effective emulator detection is actually triggered during execution.

See @MASTG-KNOW-0031 for more information on emulator detection techniques and specific APIs and artifacts to look for.

## Steps

1. Start the device.
2. Use runtime method hooking (see @MASTG-TECH-0043) and look for usages of emulator detection APIs (see @MASTG-KNOW-0031 to obtain a detailed list of emulation methods) while running the app on a device.

## Observation

The output should contain evidence of emulator detection checks.

## Evaluation

The test case fails if the hooking output does not show any evidence of emulator detection checks.
