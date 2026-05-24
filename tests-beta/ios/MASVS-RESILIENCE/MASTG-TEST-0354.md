---
platform: ios
title: References to Reverse Engineering Tools Detection in Code
id: MASTG-TEST-0354
type: [static, code]
weakness: MASWE-0107
false_negative_prone: true
profiles: [R]
knowledge: [MASTG-KNOW-0087]
best-practices: [MASTG-BEST-0048]
---

## Overview

This test checks whether the app includes code that detects the presence of reverse engineering tools such as Frida. If the app does not implement these checks, an attacker can attach a hooking framework to intercept security-sensitive operations — for example, extracting cryptographic keys or bypassing authentication — without triggering any defensive response.

On iOS, detecting reverse engineering tools typically involves inspecting loaded dynamic libraries via [`_dyld_image_count`](https://developer.apple.com/documentation/kernel/1582893-_dyld_image_count) and [`_dyld_get_image_name`](https://developer.apple.com/documentation/kernel/1582851-_dyld_get_image_name) to look for known tool artifacts such as `FridaGadget.dylib`, `frida-agent.dylib`, or libraries containing the string "frida". Other common checks include probing TCP port 27042 for a frida-server response or scanning for named pipes. See @MASTG-KNOW-0087 for details on these techniques.

This test is best combined with a dynamic analysis test that verifies whether the detection checks are actually triggered at runtime.

!!! note "Out of Scope"
    This test does not assess the robustness or bypass-resistance of the detection mechanisms. Even a well-implemented detection routine can be bypassed by hooking the file-system or dyld APIs used to perform the checks. See @MASTG-BEST-0048 for best practices on implementing effective reverse engineering tools detection.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should include any instances of reverse engineering tools detection checks in the app binary, such as references to `_dyld_image_count`, `_dyld_get_image_name`, or Frida-related artifact strings (e.g., "frida", "FridaGadget", "cynject").

## Evaluation

The test case fails if no reverse engineering tools detection checks are found in the app binary.

**Expected False Negatives:**

This test may produce false negatives if the app uses detection mechanisms not covered by the search patterns, or if detection logic is implemented in a way that is not identifiable through static string search alone (for example, through obfuscation, dynamic string construction, or native code that does not use well-known APIs). In such cases, the absence of findings does not guarantee the absence of detection mechanisms, and additional manual reverse engineering may be required.
