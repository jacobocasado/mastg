---
platform: ios
title: Runtime Monitoring of Text Fields Not Masking Sensitive Data with Frida
id: MASTG-DEMO-0x57
code: [swift]
test: MASTG-TEST-0x57
kind: fail
---

## Sample

This demo uses the same sample as @MASTG-DEMO-0x58.

{{ ../MASTG-DEMO-0x58/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn your app with Frida.
4. Fill in the login form: enter text in all fields, tap **Next**, enter a value in the OTP 2 field, and tap **Submit**.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

The script hooks into text input controls at runtime and monitors when they lose focus. For each interaction, it captures the entered text, the input field class, accessibility identifier when available, placeholder text when available, and the `isSecureTextEntry` attribute. Based on this value, it reports whether the input is masked or exposed.

## Observation

{{ output.txt }}

The output contains all text that was entered in every text input, along with its masking status.

## Evaluation

The test case fails because the output shows the password field with `isSecureTextEntry` set to `false`, meaning it is exposed — and this field contains sensitive data.

- The password input (`password_field`) has `isSecureTextEntry=false` and contains sensitive data → **FAIL**.
- The username input (`username_field`) has `isSecureTextEntry=false` but is not considered sensitive → not a failure.
- The OTP 1 input (`otp_1_field`) has `isSecureTextEntry=true` → **PASS**.
- The OTP 2 input (the last line in the output) is a SwiftUI `SecureField` which always masks input. Notice that its `aid` is `null` because SwiftUI's `SecureField` does not propagate the `accessibilityIdentifier` to the underlying `UITextField`. However, `placeholder` correctly shows `OTP 2` and `isSecureTextEntry=true` confirms masking → **PASS**.
