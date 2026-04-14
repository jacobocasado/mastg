---
title: Hardening Against Emulation (Android)
alias: hardening-against-emulation-android
id: MASTG-BEST-00ea
platform: android
knowledge: [MASTG-KNOW-0031, MASTG-KNOW-0035, MASTG-KNOW-0033, MASTG-KNOW-0030]
---

Emulated devices are a way to execute target applications with custom platform-layer instrumentation that is often hard to detect, and allows for advanced reverse engineering techniques.

Defending against emulated devices involves a layered approach that commonly consist in applying several types of security controls:

- **Detective controls**: Scan for common device emulator indicators and properties (@MASTG-KNOW-0031) and perform Google Play API integrity checks (@MASTG-KNOW-0035) to detect the presence of custom device builds.
- **Deterrent controls**: Obfuscate this detection logic (@MASTG-KNOW-0033), scatter checks throughout the app, and vary their timing to increase the cost and effort require to bypass these checks.
- **Hardening against reverse engineering tools**: Implement detection of reverse engineering tools (@MASTG-KNOW-0030), as custom builds are often combined with the usage of such tools.
