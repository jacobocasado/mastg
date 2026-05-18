---
platform: ios
title: References to APIs for Hiding Sensitive Data in Text Fields
id: MASTG-TEST-0x58
type: [static, manual]
weakness: MASWE-0053
profiles: [L2]
best-practices: [MASTG-BEST-0x41]
knowledge: [MASTG-KNOW-0098]
---

## Overview

If the app does not mask text input fields that contain sensitive data, such data may be visible to bystanders (shoulder surfing) or captured in screenshots and screen recordings. Masking by replacing typed characters with bullet characters.

This test monitors text input fields in the app at runtime and their visibility properties to check if the app masks the text entry when the user enters sensitive data. In iOS, the following settings can be applied to input fields to mask the text entries:

- In `UIKit`, this is done by setting [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) to `true` on a `UITextField`.
- In `SwiftUI`, this is done by using [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) instead of `TextField`.

Example for UIKit:

```swift
let passwordField = UITextField()
passwordField.isSecureTextEntry = true
// Alternative way (toggling)
passwordField.isSecureTextEntry = false
textField.isSecureTextEntry.toggle()
```

Example for SwiftUI:

```swift
SecureField("Password", text: $password)
```

!!! note
    This test may produce false negatives if the app uses custom text input controls that do not rely on standard classes such as `UITextField` or `SecureField` (for example in custom UI frameworks or game engines, or if text entry is handled through nonstandard abstractions that prevent reliable observation of input traits at rest).

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0072 to look for references to APIs that create text input fields and modify their visibility attributes.
3. Use @MASTG-TECH-0076 to analyze the relevant code paths and determine whether sensitive data is stored in the input fields.

## Observation

The output should contain a list of locations where the app:

- Creates text input fields, such as `UITextField`, `SecureField` or `TextField`.
- Explicitly configures visibility attributes that mask the inputted text.

## Evaluation

The test case fails if the app uses text input fields to input sensitive data and these input fields are not masked. This occurs when:

- `UIKit` `UITextField` used for a password, PIN, or OTP does not have [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) set to `true`.
- `SwiftUI` `TextField` is used instead of [`SecureField`](https://developer.apple.com/documentation/swiftui/securefield) for a password, PIN, or OTP field.

!!! note
    It is not a failure if non-sensitive text input fields (for example, for a username or email address) are unmasked. Validating whether a text input field is used for sensitive data may require a review of the app's UI and business logic to determine the context in which the field is used.

!!! note
    This test may produce false negatives if the app uses custom text input controls that do not rely on standard classes such as `UITextField` or `SecureField` (for example in custom UI frameworks or game engines, or if text entry is handled through nonstandard abstractions that prevent reliable observation of input traits at rest).
