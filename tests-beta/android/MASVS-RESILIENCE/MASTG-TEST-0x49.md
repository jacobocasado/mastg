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

This test verifies that the app implements emulator detection checks and that it executes them at runtime.

This test verifies if the application performs emulation detection by gathering dynamic evidence via hooking. See @MASTG-KNOW-0031 for a detailed overview about emulation detection indicators and patterns performed by applications.

Threat model: an attacker can instrument or hook the process.

## Steps

1. Start the device.
2. Use runtime method hooking (see @MASTG-TECH-0043) and look for usages of emulator detection APIs (see @MASTG-KNOW-0031 to obtain a detailed list of emulation methods) while running the app on a device.

## Observation

The output should contain evidence of emulator detection checks.

## Evaluation

The test case fails if the hooking output does not show any evidence of emulator detection checks.
