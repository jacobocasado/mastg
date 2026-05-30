---
title: References to Privacy-Relevant Entitlements
platform: ios
id: MASTG-TEST-0x03
type: [static, package, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0077]
---

## Overview

If an iOS app enables entitlements or capabilities that it does not need, it may gain unnecessary access to protected services or additional channels for sharing personal data outside the default sandbox model. This test checks whether the app's signed entitlements and embedded provisioning profile only enable the privacy-relevant capabilities that are required for the app's actual features.

Privacy-relevant examples include:

- `com.apple.security.application-groups`
- `com.apple.developer.icloud-container-identifiers`
- `com.apple.developer.healthkit`
- `com.apple.developer.homekit`
- `com.apple.developer.associated-domains`
- `com.apple.developer.networking.multicast`
- `com.apple.developer.siri`

Refer to @MASTG-KNOW-0077 for a broader discussion of iOS permissions, entitlements, and privacy-preserving alternatives.

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0111 to extract entitlements from the main binary.
3. Review the extracted entitlements and provisioning-profile entitlements against the app's documented features, declared purpose strings, and data-sharing model.

## Observation

The output should contain the entitlements embedded in the app.

## Evaluation

The test case fails if the app enables entitlements or capabilities that are not required for its real features or if the enabled capabilities create a broader data-sharing surface than the app's privacy model justifies.

Pay special attention to entitlements that:

- allow data sharing across apps or extensions, for example App Groups or iCloud containers,
- unlock access to sensitive user data or system services, for example HealthKit, HomeKit, or Siri-related capabilities, or
- expose additional external interfaces, for example Associated Domains or multicast networking.

**Further Validation Required:**

Inspect the code paths and target configuration associated with each flagged entitlement to confirm whether the entitlement is actively used and whether the resulting data flow is necessary and appropriately constrained.
