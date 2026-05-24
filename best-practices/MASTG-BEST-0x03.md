---
title: Control Component Export and Access
alias: control-component-export-and-access
id: MASTG-BEST-0x03
platform: android
knowledge: [MASTG-KNOW-0025]
---

Avoid declaring internal components with `android:exported="true"` unless absolutely necessary. By default, any component with an `<intent-filter>` is exported, making it reachable by any other application installed on the device.

## Manifest Configuration

If a component is only needed internally, either:

- Set `android:exported="false"` (the default when no `<intent-filter>` is present), or
- Remove the `<intent-filter>` entirely and use an explicit intent to reach the component.

```xml
<!-- Avoid: exported with custom action — any app can send this intent -->
<activity android:name=".InternalActivity" android:exported="true">
    <intent-filter>
        <action android:name="com.example.app.INTERNAL_ACTION" />
    </intent-filter>
</activity>

<!-- Prefer: not exported, reachable only by explicit intent -->
<activity android:name=".InternalActivity" android:exported="false" />
```

## Limiting Access on Exported Components

Some components must remain exported, for example to respond to system actions or to allow cross-app communication within a suite of companion apps. In those cases, use `android:protectionLevel` and `android:permission` to require the caller to hold a specific prerequisites before the system delivers the intent.

### Restricting with a Signature Protection

A plain custom permission can still be obtained by any app that requests it. To restrict access to apps from the same developer, set `android:protectionLevel="signature"`:

```xml
<permission
    android:name="com.example.app.INTERNAL_ACCESS"
    android:protectionLevel="signature" />
```

With this protection level, the system only grants the permission to apps signed with the same certificate. This is different from `android:exported="false"`, which limits access to the same APK. Signature protection allows communication across separate APKs you own (such as a main app and a companion app or widget) while still blocking third-party apps.

### Restricting with a Custom Permission

Declare a custom permission and reference it on the component:

```xml
<permission android:name="com.example.app.INTERNAL_ACCESS" />

<activity
    android:name=".InternalActivity"
    android:exported="true"
    android:permission="com.example.app.INTERNAL_ACCESS" />
```

Any app that wants to start this activity must declare `<uses-permission android:name="com.example.app.INTERNAL_ACCESS" />`. Without it, the system throws a `SecurityException`.
