---
masvs_category: MASVS-RESILIENCE
platform: android
title: Obfuscation
---

@MASTG-KNOW-0111 introduces common obfuscation techniques that apply across platforms. On Android, those techniques can affect both the Java or Kotlin layer and the native layer.

This page describes common Android obfuscation techniques across both layers.

## Java Layer

Java and Kotlin source code in Android apps is transformed into DEX bytecode for execution on Android.

When reverse engineering the application, the DEX bytecode is decompiled into human-readable pseudocode.

Obfuscation at this layer changes names, literals, control flow, or code loading behavior to reduce how much information the decompiled output reveals and increase the effort required to recover program logic.

Common open-source obfuscators that apply some of the techniques below are [R8](https://developer.android.com/topic/performance/app-optimization/enable-app-optimization), [ProGuard](https://github.com/Guardsquare/proguard), and [dProtect](https://obfuscator.re/dprotect/).

### Identifier Renaming

Android classes, methods, fields, packages, and local variables can be renamed to make it harder to correlate an identifier with its purpose when analyzing decompiled code. Instead of descriptive names, the output may contain short or meaningless identifiers that reveal little about the original identifier purpose in the app.

This is a type of layout obfuscation, which doesn't impact the program's performance.

R8 and ProGuard can rename classes, methods, and fields during the release build process. The behavior is controlled through the release build configuration and keep rules that preserve selected identifiers. See the [Android app optimization documentation](https://developer.android.com/topic/performance/app-optimization/enable-app-optimization), the [Android keep rules guide](https://developer.android.com/topic/performance/app-optimization/add-keep-rules), and the [ProGuard manual](https://www.guardsquare.com/manual/configuration/usage).

```groovy
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

```pro
-keep class com.example.api.PublicApi { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
```

### String Encryption

String literals can reveal implementation details that are relevant for reverse engineering and understanding of the business logic of the app, such as endpoint URLs, file paths, feature names, API keys, device attestation detection artifacts (such as root or jailbreak paths), and error messages.

String encryption replaces plaintext literals with encoded or encrypted representations and adds runtime logic that reconstructs the original value before use.

When this technique is applied, the original string values may no longer appear directly in the decompiled code or extracted DEX data; the clear strings are only present at runtime.

`dProtect` extends the ProGuard rule format with string obfuscation passes. See the [dProtect strings encryption documentation](https://obfuscator.re/dprotect/passes/strings/) for more information about how to encrypt strings.

```pro
-obfuscate-strings class com.example.sensitive.ApiClient {
    private static java.lang.String API_KEY;
    public java.lang.String buildHeader();
}
```

### Dynamic Code Loading and Packing

Android apps can keep part of their logic outside the main static DEX view and make it available only at runtime. This can be done by loading additional code with class loaders such as `DexClassLoader`, `PathClassLoader`, or `InMemoryDexClassLoader` (see the [Android documentation](https://developer.android.com/reference/dalvik/system/package-summary)), or by storing code in a transformed representation and restoring it before execution.

This changes where executable logic is stored and when it becomes available to the runtime. As a result, part of the application logic may be separated from the main static codebase and may only become visible after the corresponding loading or unpacking logic executes.

### Reflection and Indirect Invocation

Reflection is a property of some programming languages, including Java, that allows an app or process to inspect types and invoke constructors, methods, and fields dynamically at runtime.

This indirect style can reduce the number of explicit call sites visible in decompiled code. Instead of a direct method call, the code may obtain a class with `Class.forName`, resolve a method with `getDeclaredMethod`, and invoke it through the reflection APIs.

Reflection is separate from dynamic code loading. It operates on code that is already present in the app or otherwise available to the runtime, but it changes how that code is referenced and executed.

### Control Flow Obfuscation

The control-flow graph of a function is a representation of the elementary computational blocks and the conditions required to reach them. This representation is usually an early step in the decompilation process.

Control flow obfuscation modifies the bytecode to produce a more complex control-flow graph in the decompiled output. Common examples include inserted conditions, opaque predicates, or transformations around branch instructions such as `GOTO #offset`.

`dProtect` provides a dedicated pass for this transformation. See the [dProtect control-flow obfuscation documentation](https://obfuscator.re/dprotect/passes/control-flow/).

```pro
-obfuscate-control-flow class com.example.sensitive.** { *; }
```

### Dead Code Injection

Dead code injection adds code paths, methods, branches, or classes that do not contribute to the program's final behavior. This increases the amount of code that appears relevant during reverse engineering and makes static analysis outputs noisier.

This technique is different from control flow obfuscation. Control flow obfuscation mainly restructures real logic, while dead code injection adds extra logic that is not required for the program's intended result.

### Resource and Asset Encryption

Obfuscation in Android apps is not limited to executable code. Apps can also encode or encrypt files stored in `assets/`, `res/raw/`, or other packaged resources so that their content is not directly visible after extracting the APK.

These resources may contain configuration data, scripts, model files, web assets, or auxiliary data consumed by the Java or Kotlin layer at runtime. The app then includes logic to decode or decrypt the resource before using it.
