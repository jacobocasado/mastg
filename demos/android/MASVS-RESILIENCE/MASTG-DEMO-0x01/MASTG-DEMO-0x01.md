---
platform: android
title: Testing for Obfuscation in the Java layer
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
tools: []
kind: fail
---

## Sample

The sample code demonstrates an Android app that performs a root detection routine implemented in the Java/Kotlin layer.

The app checks whether well-known root manager packages such as `com.topjohnwu.magisk`, `eu.chainfire.supersu`, or `me.weishu.kernelsu` are installed on the device by calling `PackageManager.getPackageInfo()`. If one of those packages is found, the app reports the detection result and closes itself by calling `finishAffinity()`.

The app is not obfuscated, which is a flaw, as it is possible to recover the root detection logic when reverse engineering and analyzing the app logic without a high level of effort. As a result, an attacker could hook the related functions or spoof the queried values to bypass the detection logic.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

1. Build and install the release APK.
2. Perform @MASTG-TECH-0017 to decompile the installed release APK and extract `MastgTest_reversed.java` and `AndroidManifest_reversed.xml`.
3. Perform @MASTG-TECH-0023 to review the decompiled Java/Kotlin code.
4. Determine whether the obfuscation applied to the Java or Kotlin layer still allows security-relevant logic to be identified and reverse engineered with reasonable effort (Use @MASTG-KNOW-0033 as reference of common obfuscation patterns).

{{ run.sh }}

## Observation

The output contains the relevant locations in the reverse-engineered files that show the monitored root manager packages, the `PackageManager` lookups, and the `finishAffinity()` call.

{{ output.txt }}

## Evaluation

The test case fails because when performing reverse engineering on the minified Java/Kotlin layer it is possible to identify and understand the security-relevant root detection logic with little effort.

In `AndroidManifest_reversed.xml`, lines 14 to 16 show the package visibility queries for the monitored root manager packages.

In `MastgTest_reversed.java`, lines 21, 24, and 32 show that R8 shortened member names (`f7310a`, `f7311b`, and `a()`), so some minification is clearly present. However, lines 29, 47, 50, 52, 59, and 64 still reveal the monitored package names, the `PackageManager` lookups, the root detection message, and the `finishAffinity()` call, as the strings are not encrypted.

```java
public final class MastgTest {
    public final Context f7310a;
    public final List f7311b;

    public MastgTest(Context context) {
        this.f7310a = context;
        this.f7311b = AbstractC0489n.I0("com.topjohnwu.magisk", "eu.chainfire.supersu", "me.weishu.kernelsu");
    }

    public final String a() {
        ...
        PackageManager packageManager = context.getPackageManager();
        packageManager.getPackageInfo(str, 0);
        ...
        String str3 = "Detected root manager package '" + str2 + "'. The app closes when root-related packages are found.";
        ...
        activity.finishAffinity();
    }
}
```

This means that the reverse-engineered code still makes it straightforward to identify that the app is checking for root manager packages and closing the app when one is found, despites having some obfuscation applied in the form of variable renaming.
