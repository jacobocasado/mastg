---
platform: ios
title: Reviewing Privacy-Relevant Entitlements
id: MASTG-DEMO-0x03
code: [xml]
test: MASTG-TEST-0x03
---

## Sample

The sample below shows a signed app entitlements file with privacy-relevant capabilities enabled.

{{ entitlements.plist }}

## Steps

1. Use @MASTG-TECH-0111 to extract the entitlements from the signed app bundle. In this demo, the extracted file is `entitlements.plist`.
2. Run `run.sh` to print the relevant entitlements in a readable format.

{{ run.sh }}

## Observation

The output shows that the app enables several privacy-relevant entitlements:

{{ output.txt }}

## Evaluation

The test case fails because the app enables privacy-relevant entitlements that expand the app's data-sharing surface and access to sensitive platform services.

Review each enabled entitlement and confirm that:

- the corresponding feature is genuinely present in the app,
- the data flow enabled by that entitlement is necessary, and
- a narrower capability or data-sharing design would not be sufficient.
