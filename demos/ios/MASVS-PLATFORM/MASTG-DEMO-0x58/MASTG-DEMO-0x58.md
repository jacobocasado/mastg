---
platform: ios
title: Text Input Fields Not Hiding Sensitive Data
id: MASTG-DEMO-0x58
code: [swift]
test: MASTG-TEST-0x58
kind: fail
---

## Sample

The application exposes a login form with the following text input fields:

- **Username** (Not considered sensitive data. Uses `UITextField` text field, no explicit `isSecureTextEntry` — defaults to `false`).
- **Password** (Considered sensitive data. Uses `UITextField` text field, `isSecureTextEntry = false` explicitly).
- **OTP 1** (Considered sensitive data. Uses `UITextField` text field, `isSecureTextEntry = true` explicitly).
- **OTP 2** (Considered sensitive data. Uses `SecureField` text field).

{{ MastgTest.swift }}

The "Password" field is not masked, and is therefore visible to bystanders (shoulder surfing) or potentially captured in screenshots and screen recordings.

The "OTP 1" and "OTP 2" fields use text input fields that are configured to mask the inputted data.

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Execute `run.sh` to open the app with @MASTG-TOOL-0073.

{{ textfield_masking.r2 # run.sh }}

## Observation

The output reveals:

- 3 `UITextField` closures at addresses `0x1828`, `0x19a8`, and `0x1ab8`.
- 2 calls to `setSecureTextEntry:` at `0x1a24` and `0x1b38`.
- 1 call to the `SecureField` initializer at `0x3c60`.

{{ output.txt }}

## Evaluation

The test case fails because the password field calls `setSecureTextEntry:` with argument `false`, leaving the sensitive input unmasked.

**Interpreting the Disassembly:**

Although `MastgTest.swift` is written in Swift, it interacts with UIKit (an Objective-C framework). The compiler translates these interactions into calls to the `objc_msgSend` function. We analyze the arguments passed to this function using the ARM64 calling convention:

- `x0` register: holds `self` (the instance of the `UITextField`).
- `x1` register: holds the selector (the method name).
- `x2` register: holds the argument passed to that method.

**Password Field (FAIL):**

The `setSecureTextEntry:` call at `0x1a24` corresponds to the password field. The disassembly shows:

At `0x1a28`, `mov w8, 0` sets the value to `0`. At `0x1a2c`, `and w2, w8, 1` prepares the boolean argument, resulting in `w2 = 0` (`false`).

This means [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) is set to `false` for the password field, causing the password to be displayed in plain text instead of being masked with bullet characters.

**OTP 1 Field (PASS):**

The `setSecureTextEntry:` call at `0x1b38` corresponds to the OTP 1 field. The disassembly shows:

At `0x1ae0`, `mov w8, 1` stores the value `1` (`true`) into a local variable. At `0x1b2c`, the stored value is reloaded. At `0x1b3c`, `and w2, w8, 1` results in `w2 = 1` (`true`).

This means `isSecureTextEntry` is `true` for the OTP 1 field, so the input is correctly masked.

**OTP 2 Field (PASS):**

The call at `0x3c60` invokes the `SecureField` initializer directly (`bl sym.SwiftUI.SecureField...`). SwiftUI's `SecureField` always masks input by design — there is no `isSecureTextEntry` setter involved.
