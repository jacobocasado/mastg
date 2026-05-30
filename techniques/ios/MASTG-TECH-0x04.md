---
title: Bypassing Reverse Engineering Tools Detection
platform: ios
---

Bypassing reverse engineering tools detection involves identifying the specific checks an app performs at runtime and nullifying them, either through static patching of the binary or by applying dynamic hooks.

## Static Patching

If the detection logic can be located in the binary (for example, by searching for strings such as "frida", "CydiaSubstrate", or known detection function names), you can disable the unwanted behavior by patching the binary.

Load the binary into @MASTG-TOOL-0073 via its graphical interface @MASTG-TOOL-0098:

```sh
r2 -Aw /path/to/binary
```

Alternatively, use @MASTG-TOOL-0033 to open the binary and navigate to the detection function. Once located, replace the detection logic with a return instruction (e.g., `RET` on ARM64) or a branch that skips the termination path.

After patching, re-sign the binary and repackage the IPA before installing it on the device. See @MASTG-TECH-0152 for an example of locating and patching runtime detection functions.

## Dynamic Hooks

Dynamic hooks offer a non-destructive alternative to static patching. Use @MASTG-TOOL-0039 to intercept the lower-level APIs that the detection logic depends on and modify their behavior at runtime.

For example, if the detection logic enumerates loaded dynamic libraries via `_dyld_get_image_name`, you can hook that function with @MASTG-TOOL-0039 and filter out suspicious entries before the detection routine sees them. Similarly, if the detection logic probes TCP ports (such as @MASTG-KNOW-0087's D-Bus AUTH check against port `27042`), you can hook `connect` to block connections to those ports.

You can also use @MASTG-TOOL-0139 to hook Objective-C or Swift methods directly. The general approach is to target the specific API that the detection logic relies on for its information source.

## See Also

- @MASTG-KNOW-0087 — Detection methods that this technique aims to bypass.
- @MASTG-TECH-0152 — Bypassing Jailbreak Detection, which follows the same static patching and dynamic hooks approach.
- @MASTG-TECH-0095 — Method Hooking with Frida on iOS.
