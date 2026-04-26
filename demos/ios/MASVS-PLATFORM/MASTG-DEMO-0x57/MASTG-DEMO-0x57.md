---
platform: ios
title: Password Field Not Using Secure Text Entry
id: MASTG-DEMO-0x57
code: [swift]
test: MASTG-TEST-0x57
kind: fail
status: draft
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

- Multiple symbols referencing `UITextField` (functions, strings, and relocs).
- The `setSecureTextEntry:` selector string at `0x00017474` and a fixup reloc at `0x000241b8`.
- A cross-reference from `0x241b8` (DATA, read-only).
- The disassembly near `0x000048fc` shows `setSecureTextEntry:` being loaded and called via `objc_msgSend`.

{{ output.txt }}

## Evaluation

The test case fails because the `setSecureTextEntry:` selector is called with argument `0` (`false`) for the password field.

**Interpreting the Disassembly:**

Although `MastgTest.swift` is written in Swift, it interacts with UIKit (an Objective-C framework). The compiler translates these interactions into calls to the `objc_msgSend` function. We analyze the arguments passed to this function using the ARM64 calling convention:

- `x0` register: holds `self` (the instance of the `UITextField`).
- `x1` register: holds the selector (the method name).
- `x2` register: holds the argument passed to that method.

**Password Field (FAIL):**

At address `0x000048fc`, the binary loads the selector `setSecureTextEntry:` into `x1` via `ldr x1, [x8, 0x1b8]` (pointing to the reloc fixup at `0x241b8`).

At address `0x00004900`, the instruction `mov w8, 0` sets the value to `0`.

At address `0x00004904`, `and w2, w8, 1` prepares the boolean argument (masking to 1 bit), so `w2 = 0`.

This means [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) is set to `false` for the password field, causing the password to be displayed in plain text instead of being masked with bullet characters.
