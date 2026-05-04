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

The following sample demonstrates a login form with three text input fields and one SwiftUI `SecureField`:

- **Username** (`UITextField`, no explicit `isSecureTextEntry` — defaults to `false`, not sensitive).
- **Password** (`UITextField`, `isSecureTextEntry = false` explicitly — **FAIL**).
- **OTP 1** (`UITextField`, `isSecureTextEntry = true` — PASS).
- **OTP 2** (`SecureField` in SwiftUI — PASS).

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the script.

{{ textfield_masking.r2 # run.sh }}

## Observation

The output reveals:

- Multiple symbols referencing `UITextField` (closures, thunks, metadata) and `SecureField` (SwiftUI initializers, relocs, and metadata).
- The `setSecureTextEntry:` selector string at `0x000177a5` and its fixup reloc at `0x000201c0`.
- Two cross-references to `reloc.fixup.setSecureTextEntry:` (`0x000201c0`):
    - `sym.MASTestApp.MastgTest.mastg.completion.UITextField...U0_` at `0x1a24` — the **password field** closure (FAIL).
    - `sym.MASTestApp.MastgTest.mastg.completion.UITextField._` at `0x1b38` — the **OTP 1 field** closure (PASS).
- One cross-reference to the `SecureField` initializer at `0x3c60` — the **OTP 2** SwiftUI view (PASS).
- The disassembly confirms the argument value (`w2`) passed to `objc_msgSend` for each `setSecureTextEntry:` call.

{{ output.txt }}

## Evaluation

The test case fails because the password field calls `setSecureTextEntry:` with argument `0` (`false`).

**Interpreting the Disassembly:**

Although `MastgTest.swift` is written in Swift, it interacts with UIKit (an Objective-C framework). The compiler translates these interactions into calls to the `objc_msgSend` function. We analyze the arguments passed to this function using the ARM64 calling convention:

- `x0` register: holds `self` (the instance of the `UITextField`).
- `x1` register: holds the selector (the method name).
- `x2` register: holds the argument passed to that method.

**Password Field (FAIL):**

At address `0x00001a24`, the binary loads the selector `setSecureTextEntry:` into `x1` (pointing to `reloc.fixup.setSecureTextEntry:` at `0x201c0`).

At address `0x00001a28`, `mov w8, 0` sets the value to `0`.

At address `0x00001a2c`, `and w2, w8, 1` prepares the boolean argument (masking to 1 bit), so `w2 = 0`.

This means [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) is set to `false` for the password field, causing the password to be displayed in plain text instead of being masked with bullet characters.

**OTP 1 Field (PASS):**

At address `0x00001ae0`, `mov w8, 1` stores the value `1` (true) into `var_1ch` at `0x00001ae4`.

At address `0x00001b38`, the binary loads the selector `setSecureTextEntry:` into `x1`.

At address `0x00001b2c`, `ldr w8, [var_1ch]` reloads that stored value (`1`).

At address `0x00001b3c`, `and w2, w8, 1` results in `w2 = 1`, so `isSecureTextEntry` is `true`. The OTP 1 field is correctly masked.

**OTP 2 Field (PASS — SwiftUI `SecureField`):**

At address `0x00003c60`, the binary calls the `SecureField` initializer directly (`bl sym.SwiftUI.SecureField...`). SwiftUI's `SecureField` always masks input by design — there is no `isSecureTextEntry` setter involved.
