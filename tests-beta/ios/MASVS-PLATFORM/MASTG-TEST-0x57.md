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

If the app does not mask text input fields for access codes (passwords or PINs) and verification codes (OTPs), sensitive data may be visible to bystanders (shoulder surfing) or captured in screenshots and screen recordings.

iOS provides dedicated APIs to prevent this. In UIKit, setting [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) to `true` on a `UITextField` replaces typed characters with bullet characters. In SwiftUI, [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) should be used instead of `TextField` for sensitive inputs. If the app uses a `UITextField` without `isSecureTextEntry = true`, or uses `TextField` in SwiftUI instead of `SecureField`, the entered value is displayed in plain text.

UIKit:

```swift
let passwordField = UITextField()
passwordField.isSecureTextEntry = true
```

SwiftUI:

```swift
SecureField("Password", text: $password)
```

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0070 to look for references to text field classes and text obfuscation APIs.

## Observation

The output should contain a list of locations where text input fields are used, along with whether they are configured to mask input.

## Evaluation

The test case fails if any text input field used for access or verification codes is found to be unmasked. For example, due to the following:

- A `UITextField` used for a password, PIN, or OTP does not have [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) set to `true`.
- A SwiftUI `TextField` is used instead of [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) for a password, PIN, or OTP field.

!!! note
    This test may produce false negatives if the app uses custom text input controls that do not rely on standard classes such as `UITextField` or `SecureField` (for example in custom UI frameworks or game engines).

Note that it is not a failure if non-sensitive text input fields (for example, for a username or email address) are unmasked. Validating whether a text input field is used for sensitive data may require manual review of the app's UI and code to determine the context in which the field is used. 
