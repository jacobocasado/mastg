---
title: Implicit Intents Used for Internal App Communication
platform: android
id: MASTG-TEST-0x01
type: [static]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0025, MASTG-KNOW-0x01]
profiles: [L1, L2]
---

## Overview

Using implicit intents for communication between components within the same application introduces a risk of intent hijacking. The Android system resolves implicit intents by identifying all activities that can handle the specified action, which may include third-party applications. If multiple apps register for the same action, the system may present a resolver dialog, potentially leading a user to accidentally select a malicious app. This can result in sensitive data leakage or unauthorized actions. For internal communication, explicit intents should be used to target specific components directly, bypassing the system's resolution process.

## Steps

1. Use @MASTG-TECH-0014 to scan the application's source code or decompiled codebase for insecure intent patterns.
2. Review the findings to identify if implicit intents are used to launch internal components.

## Observation

The output should contain instances where an `Intent` is initialized and used without specifying a target component or package.

## Evaluation

The test case fails if the app uses implicit intents for internal communication.
