---
title: Hardening Against Virtual Devices
alias: hardening-against-virtual-devices
id: MASTG-BEST-00eb
platform: ios
knowledge: [MASTG-KNOW-009x, MASTG-KNOW-0088, MASTG-KNOW-0087, MASTG-KNOW-0089]
---

Virtual devices, like @MASTG-TOOL-0108, allow target applications to be executed in controlled environments that may use custom system images, modified platform components, or instrumentation that is difficult for the app to detect. This enables advanced reverse-engineering techniques.

Defending against virtual devices involves a layered approach that commonly consists of applying several types of security controls:

- **Detective controls**: Scan for common virtual device indicators and properties (@MASTG-KNOW-009x) to help identify risky devices and virtualized environments.
- **Preventive controls**: Implement detection of reverse engineering tools (@MASTG-KNOW-0087), as virtual devices are often combined with the usage of such tools.
- **Deterrent controls**: Obfuscate this detection logic (@MASTG-KNOW-0089), scatter checks throughout the app, and vary their timing to increase the cost and effort required to bypass these checks.
