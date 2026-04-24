---
platform: ios
title: Password Field Not Using Secure Text Entry
id: MASTG-DEMO-0x57
code: [swift]
test: MASTG-TEST-0x57
kind: fail
status: draft
note: The MASTestApp binary and accurate output need to be added once the app is compiled from the MastgTest.swift in this demo.
---

## Sample

The following sample demonstrates a login form where the password field does not use secure text entry. The password field has [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) explicitly set to `false`, causing the entered password to be displayed in plain text.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the script.

{{ textfield_masking.r2 # run.sh }}

## Observation

The output reveals:

- 1 reference to `UITextField` at address `0x100010158`.
- 1 call to `setSecureTextEntry:` at address `0x1000045f0`, discovered via `f~setSecureTextEntry` and then `axt`.

{{ output.txt }}

## Evaluation

The test case fails because the `setSecureTextEntry:` selector is called with argument `0` (`false`) for the password field.

**Interpreting the Disassembly:**

Although `MastgTest.swift` is written in Swift, it interacts with UIKit (an Objective-C framework). The compiler translates these interactions into calls to the `objc_msgSend` function. We analyze the arguments passed to this function using the ARM64 calling convention:

- `x0` register: holds `self` (the instance of the `UITextField`).
- `x1` register: holds the selector (the method name).
- `x2` register: holds the argument passed to that method.

**Password Field (FAIL):**

At address `0x1000045f0`, the binary loads the selector `setSecureTextEntry:`.

At address `0x1000045f8`, the instruction `mov w2, 0` sets the argument to `0`.

This means [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) is set to `false` for the password field, causing the password to be displayed in plain text instead of being masked with bullet characters.
