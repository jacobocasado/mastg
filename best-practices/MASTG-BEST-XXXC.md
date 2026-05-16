---
title: Avoid Exporting Internal Components
alias: avoid-exporting-internal-components
id: MASTG-BEST-XXXC
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

> [!NOTE]
> Setting `android:exported="false"` doesn't prevent other components within the same app from starting the activity via an explicit intent. It only prevents external apps from doing so. If `android:exported="true"` is required (for example, to handle system actions), restrict access using [permissions](https://developer.android.com/guide/topics/permissions/overview) with `android:permission`.
