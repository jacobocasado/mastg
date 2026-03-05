---
title: Hardening Against Simulators (iOS)
alias: hardening-against-simulators-ios
id: MASTG-BEST-00eb
platform: ios
knowledge: [MASTG-KNOW-0088, MASTG-KNOW-0087, MASTG-KNOW-0089]
---

iOS simulators can be detected in the app by performing in-app checks (@MASTG-KNOW-0088).

However, it is recommended to also implement other types of security controls to avoid reverse engineering and runtime introspection, as some of these simulator detection checks in the app could be detected by reverse engineering and could be afterwards altered by an attacker (e.g, by patching the code). The following security controls are recommended:

- **Preventive controls**: Implement detection of reverse engineering tools (@MASTG-KNOW-0087), as custom builds are often combined with the usage of such tools.
- **Deterrent controls**: Obfuscate this detection logic (@MASTG-KNOW-0089), scatter checks throughout the app, and vary their timing to increase the cost and effort required to bypass these checks.
