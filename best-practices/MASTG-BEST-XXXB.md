---
title: Sanitize Data Coming from External Components
alias: sanitize-external-data
id: MASTG-BEST-XXXB
platform: android
knowledge: [MASTG-KNOW-0025]
---

All data received from external sources — such as `Intent` extras, `onActivityResult` callbacks, or `ContentProvider` results — must be treated as untrusted and thoroughly sanitized before use. Failure to validate this data can lead to serious vulnerabilities, including arbitrary file access and path traversal. Specifically, applications must always validate URIs and associated metadata before reading from them, copying their content, or passing them to sensitive system APIs.

## Validate the URI scheme

Reject URI schemes that are unsafe in your context. For most result-handling scenarios, only `content://` URIs from known, trusted authorities should be accepted:

```kotlin
fun isSafeUri(uri: Uri): Boolean {
    // Reject file:// — allows reading arbitrary internal storage paths
    if (uri.scheme == "file") return false
    // Reject unknown content authorities
    val trustedAuthorities = setOf("com.example.app.provider")
    if (uri.scheme == "content" && uri.authority !in trustedAuthorities) return false
    return true
}
```

## Validate the filename from a ContentProvider

When querying a `ContentProvider` for a display name or file path, sanitize the result before using it as a file name. Path-traversal sequences (`../`) in the filename can redirect writes outside the intended directory:

```kotlin
fun sanitizeFileName(name: String): String {
    // Remove all path separators and traversal sequences
    return File(name).name  // strips any directory components
}

val rawName = getFileNameFromUri(uri) ?: "default.bin"
val safeName = sanitizeFileName(rawName)
val target = File(context.filesDir, safeName)
```

`File(name).name` returns only the final path component, discarding any `../` prefix an attacker might inject.

## Avoid world-readable output locations

Don't copy URI content to `externalCacheDir`, `getExternalFilesDir`, or any path on shared storage unless the data is intentionally public. Use internal storage (`filesDir`, `cacheDir`) for any content received from an untrusted source:

```kotlin
// Avoid: world-readable on older Android versions
val output = File(activity.externalCacheDir, fileName)

// Prefer: private to the app
val output = File(activity.filesDir, fileName)
```

> [!NOTE]
> Validating the URI and filename reduces the attack surface but doesn't eliminate it entirely if the content itself is attacker-controlled. Never execute or dynamically load files (via `System.load()`, `DexClassLoader`, etc.) whose content originates from an untrusted source, regardless of where they are stored.
