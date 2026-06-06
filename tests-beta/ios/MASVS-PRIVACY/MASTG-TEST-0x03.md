---
title: Privacy-Relevant Entitlements in the App Code Signature
platform: ios
id: MASTG-TEST-0x03
type: [static, package, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0077]
---

## Overview

If an iOS app enables entitlements or capabilities that it does not need, it may gain unnecessary access to protected services or additional channels for sharing personal data outside the default sandbox model. This test checks whether the entitlements signed into the app only enable privacy-relevant capabilities that are required for the app's actual features.

Capabilities configured in Xcode are signed into the app as entitlements, but the entitlement itself is not a runtime API call. The associated runtime surface depends on the service: `HealthKit` uses `HKHealthStore` and `HealthKit` data types, App Groups use shared containers or suite defaults, and Associated Domains reach the app through system-delivered activities.

Privacy-relevant examples include:

- `com.apple.security.application-groups`
- `com.apple.developer.icloud-container-identifiers`
- `com.apple.developer.healthkit`
- `com.apple.developer.homekit`
- `com.apple.developer.associated-domains`
- `com.apple.developer.networking.multicast`
- `com.apple.developer.siri`

See @MASTG-KNOW-0077 for the relationship between Xcode capabilities, signed entitlements, and the framework APIs or entry points that use the corresponding service.

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0111 to extract the entitlements from the main binary.

> The entitlements signed into the app binary are the reliable source, because they are present regardless of how the app was built or signed. The `embedded.mobileprovision` file can carry the same entitlements, but it only exists when the packaged app includes a provisioning profile, such as development, ad-hoc, enterprise, or App Store submission builds before App Store processing. It is absent in simulator builds, App Store-distributed apps, and pseudo-signed builds (for example, when the binary is signed with `ldid`).

## Observation

The output should contain the entitlements embedded in the app's code signature.

## Evaluation

The test case fails if the app enables entitlements or capabilities that are not required for its real features or if the enabled capabilities create a broader data-sharing surface than the app's privacy model justifies.

Pay special attention to entitlements that:

- allow data sharing across apps or extensions, for example App Groups or iCloud containers,
- unlock access to sensitive user data or system services, for example HealthKit, HomeKit, or Siri-related capabilities, or
- expose additional external interfaces, for example Associated Domains or multicast networking.
