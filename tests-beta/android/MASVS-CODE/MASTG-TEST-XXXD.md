---
title: Unsanitized Data from Implicit Intents
platform: android
id: MASTG-TEST-XXXD
type: [static, dynamic]
weakness: MASWE-0083
best-practices: [MASTG-BEST-XXXA, MASTG-BEST-XXXB]
knowledge: [MASTG-KNOW-0025, MASTG-KNOW-XXXB, MASTG-KNOW-XXXC]
profiles: [L1, L2]
---

## Overview

Applications that use implicit intents to request data (such as files) from other applications must properly validate and sanitize the data received in the `onActivityResult` callback. When an implicit intent is used (whether it is a standard action like `GET_CONTENT` or a custom action), any app on the device can potentially respond. A malicious responder can return unexpected URIs (like `file://` instead of `content://`) or malicious metadata (like filenames containing path-traversal strings `../`). 

If the receiving app trusts this data without validation, it can lead to severe vulnerabilities such as Arbitrary File Read (@MASTG-KNOW-XXXB) or Arbitrary Code Execution (@MASTG-KNOW-XXXC).

This test focuses on the broader issue of improper verification of data returned by third-party components.

## Steps

### Static Analysis

1. Use @MASTG-TOOL-0110 to scan the application's source code or decompiled codebase for instances where data is received via an intent response (e.g., in `onActivityResult`).
2. Analyze the flow of the returned data (URI or metadata) to identify where it is used in sensitive operations, such as file I/O or dynamic code loading.
3. Verify if the application implements robust sanitization and validation on the data. For example:
    * Checking that a URI uses the expected `content://` scheme and not a local `file://` scheme.
    * Validating that a filename does not contain path-traversal sequences like `../`.
    * Ensuring the final resolved file path is within the intended directory.

### Dynamic Analysis

1. Use an instrumentation tool like @MASTG-TOOL-0145 to hook file system constructors (e.g., `java.io.File`, `java.io.FileOutputStream`).
2. Trigger the implicit intent and provide a malicious response from a controlled attacker app (see @MASTG-KNOW-XXXC for an example).
3. Observe the parameters passed to the file system hooks.
4. Verify if the instrumentation detects path traversal or attempts to write to sensitive internal directories.

## Observation

The output (static or dynamic) should show whether the app handles data returned from an implicit intent with proper validation or sanitization routines.

## Evaluation

The test case **fails** if:
- Static analysis reveals that data from an intent response is used in sensitive operations without proper sanitization.
- Dynamic analysis confirms that providing a malicious intent response results in unauthorized file access or path-traversal behavior.
