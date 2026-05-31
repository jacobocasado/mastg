---
masvs_category: MASVS-PLATFORM
platform: android
title: Android Activities
---

An [activity](https://developer.android.com/guide/components/activities/intro-activities) is an app component that provides a single screen with a user interface. An app typically implements one activity per screen, so an app with three screens implements three activities. Each activity extends the [`Activity`](https://developer.android.com/reference/android/app/Activity) class (or a subclass such as [`AppCompatActivity`](https://developer.android.com/reference/androidx/appcompat/app/AppCompatActivity)) and hosts the user interface elements of that screen, including fragments, views, and layouts.

Activities are a fundamental inter-process communication (IPC) entry point. Other apps and the system can start an activity by sending it an [`Intent`](https://developer.android.com/reference/android/content/Intent), which makes the activity's visibility and access control directly relevant to the app's attack surface. See @MASTG-KNOW-0020 for the IPC model and @MASTG-KNOW-0025 for how implicit intents reach activities.

## Declaration

Each activity must be declared in the `AndroidManifest.xml` file with an [`<activity>`](https://developer.android.com/guide/topics/manifest/activity-element) element nested inside `<application>`:

```xml
<activity android:name=".MainActivity" />
```

Activities that aren't declared in the manifest can't be displayed, and attempting to launch them raises an exception. The system instantiates each activity when it's started and routes user interaction and lifecycle events to it.

## Lifecycle

Activities have their own lifecycle managed by the Android system. An activity can be in one of several states (for example, created, started, resumed, paused, stopped, and destroyed) and receives callbacks as it transitions between them. The most common callbacks are:

- [`onCreate`](https://developer.android.com/reference/android/app/Activity#onCreate(android.os.Bundle)): initializes the activity and is where the user interface is usually built.
- `onStart`, `onResume`: the activity becomes visible and then interactive.
- `onPause`, `onStop`: the activity loses focus and then visibility.
- `onDestroy`: the activity is being removed; release resources here.
- `onSaveInstanceState` and `onRestoreInstanceState`: persist and restore transient UI state.

For the full description, see [The activity lifecycle](https://developer.android.com/guide/components/activities/activity-lifecycle).

## Starting an Activity

An activity is started by passing an `Intent` to [`startActivity`](https://developer.android.com/reference/android/content/Context#startActivity(android.content.Intent)) or, when a result is expected, through the [Activity Result APIs](https://developer.android.com/training/basics/intents/result). Intents can be:

- **Explicit:** they name the target component by class, typically to start an activity within the same app.
- **Implicit:** they describe an action and let the system resolve which component handles it, based on the [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) declarations of installed apps.

See @MASTG-KNOW-0025 for the distinction between explicit and implicit intents.

## Intent Filters

An [`<intent-filter>`](https://developer.android.com/guide/components/intents-filters) declares the actions, categories, and data an activity can respond to. The launcher activity, for example, declares the `MAIN` action and `LAUNCHER` category:

```xml
<activity android:name=".MainActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

An activity can only be started by an implicit intent if it declares a matching intent filter. Deep links are a special case of intent filter that map web or custom URIs to activities; see @MASTG-KNOW-0019.

## Access Control

Whether other apps can start an activity is governed by manifest attributes:

- [`android:exported`](https://developer.android.com/guide/topics/manifest/activity-element#exported): when `true`, components of other apps can start the activity. When `false`, only the same app, apps with the same user ID, or privileged system components can start it. Since Android 12 (API level 31), any component that declares an `<intent-filter>` must set `android:exported` explicitly, or the app fails to install.
- Declaring an `<intent-filter>` historically caused `android:exported` to default to `true`. Relying on the default value is discouraged because the default has changed across Android versions and component types. See the [`android:exported` documentation](https://developer.android.com/privacy-and-security/risks/android-exported) for details.
- [`android:permission`](https://developer.android.com/guide/topics/manifest/activity-element#prmsn): requires the caller to hold a specific permission to start the activity. Combined with a custom permission and an appropriate [`android:protectionLevel`](https://developer.android.com/guide/topics/manifest/permission-element#plevel) (for example, `signature`), this restricts which apps can interact with the component. See [Permission-based access control to exported components](https://developer.android.com/privacy-and-security/risks/access-control-to-exported-components).

For an overview of how to restrict access to exported components, see @MASTG-BEST-0x01.
