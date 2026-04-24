---
platform: ios
title: App Exposing User Authentication Data in Text Input Fields
id: MASTG-TEST-0x57
type: [static, manual]
weakness: MASWE-0053
profiles: [L2]
best-practices: [MASTG-BEST-0x41]
knowledge: [MASTG-KNOW-0098]
---

## Overview

This test verifies that the app handles user input correctly, ensuring that access codes (passwords or PINs) and verification codes (OTPs) are not exposed in plain text within text input fields.

Proper masking (e.g., dots instead of input characters) of these codes is essential to protect user privacy. This can be achieved using appropriate input configurations that obscure the characters entered by the user.

UIKit:

```swift
let passwordField = UITextField()
passwordField.isSecureTextEntry = true
```

SwiftUI:

```swift
SecureField("Password", text: $password)
```

If the app does not configure these settings, sensitive data such as passwords or OTPs may be visible to bystanders or captured in screenshots and screen recordings.

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0070 to look for references to text field classes and text obfuscation APIs.
3. Manually evaluate and shortlist the fields for access or verification codes usage.

## Observation

The output should contain a list of locations where text input fields for access or verification codes are used.

## Evaluation

The test case fails if any text input field used for access or verification codes is found to be unmasked. For example, due to the following:

- A `UITextField` used for a password, PIN, or OTP does not have [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) set to `true`.
- A SwiftUI `TextField` is used instead of [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) for a password, PIN, or OTP field.

!!! note
    This test may produce false negatives if the app uses custom text input controls that do not rely on standard classes such as `UITextField` or `SecureField` (for example in custom UI frameworks or game engines).
