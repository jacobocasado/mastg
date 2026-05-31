---
title: Restrict Access to Android Activities
alias: restrict-access-to-activities
id: MASTG-BEST-0x01
platform: android
knowledge: [MASTG-KNOW-0x01]
---

Expose an [activity](https://developer.android.com/guide/components/activities/intro-activities) to other apps only when another app genuinely needs to start it. Every exported activity is an entry point that any app on the device can invoke, so keeping activities private by default reduces the app's attack surface.

- **Set `android:exported` explicitly.** Declare [`android:exported="false"`](https://developer.android.com/guide/topics/manifest/activity-element#exported) on every activity that doesn't need to be started by other apps. Don't rely on the default value: it has changed across Android versions and component types, and an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) historically makes an activity exported unless you set the attribute. Since Android 12 (API level 31), any component with an intent filter must set `android:exported` explicitly. See [`android:exported`](https://developer.android.com/privacy-and-security/risks/android-exported).
- **Don't put sensitive functionality behind an exported activity without authentication.** If an activity must be exported, make sure it doesn't let a caller bypass authentication or reach a screen that should only be available after login. Re-check the app's authentication state in the activity rather than assuming the user reached it through the intended flow.
- **Require a permission when appropriate.** If only specific apps should start the activity, protect it with [`android:permission`](https://developer.android.com/guide/topics/manifest/activity-element#prmsn) and define a custom permission with an appropriate [`android:protectionLevel`](https://developer.android.com/guide/topics/manifest/permission-element#plevel), such as `signature` to limit access to apps signed with the same key. See [Permission-based access control to exported components](https://developer.android.com/privacy-and-security/risks/access-control-to-exported-components).
- **Validate incoming intent data.** Treat all data in an incoming `Intent` as untrusted and validate it before use.

!!! warning

    Setting `android:exported="false"` prevents other apps from starting the activity, but components within the same app and apps sharing the same user ID can still reach it. Don't treat it as a substitute for in-app authentication checks on sensitive screens.

For background on activities and the `android:exported` attribute, see @MASTG-KNOW-0x01.
