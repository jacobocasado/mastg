---
title: Implicit Intents Leak Sensitive Arguments
platform: android
id: MASTG-TEST-XXXC
type: [dynamic]
weakness: MASWE-0066
best-practices: [MASTG-BEST-XXXA]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

The transmission of sensitive data (such as authentication tokens, passwords, or personally identifiable information) via extras in an implicit intent is insecure. Implicit intents are resolved by the system and can be intercepted by any app that registers a matching intent filter, exposing the sensitive data to unauthorized parties. Attackers can create malicious apps that "listen" for specific actions and capture the attached sensitive data. Sensitive data should only be transmitted via explicit intents or other secure IPC mechanisms.

## Steps

1. Identify actions in the app that involve transmitting sensitive data between components.
2. Monitor the intents sent by the app using @MASTG-TECH-0012 or a custom @MASTG-TOOL-0001 script.
3. Check if the captured intents are implicit and if they contain sensitive information in their extras.

## Observation

The output should contain the details of any captured implicit intents that carry sensitive data in their extras.

## Evaluation

The test case fails if any implicit intent is found to carry sensitive information. The exposure of sensitive data via implicit intents is a direct violation of secure IPC best practices.
