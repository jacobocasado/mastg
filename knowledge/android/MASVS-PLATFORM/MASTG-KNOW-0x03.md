---
masvs_category: MASVS-PLATFORM
platform: android
title: Android Broadcast Receivers
---

A [broadcast receiver](https://developer.android.com/guide/components/broadcasts) is an app component that responds to broadcast messages from other apps or from the system. Apps use broadcasts as a publish-subscribe messaging mechanism: the system delivers broadcasts for events such as boot completion or connectivity changes, and apps can send their own broadcasts to communicate between components or with other apps. A broadcast receiver extends the [`BroadcastReceiver`](https://developer.android.com/reference/android/content/BroadcastReceiver) class and implements [`onReceive`](https://developer.android.com/reference/android/content/BroadcastReceiver#onReceive(android.content.Context,%20android.content.Intent)).

Broadcasts are built on top of the [`Intent`](https://developer.android.com/reference/android/content/Intent) system and are an inter-process communication (IPC) entry point, subject to manifest access controls such as `android:exported` and `android:permission`. This makes receiver visibility directly relevant to the app's attack surface. See @MASTG-KNOW-0020 for the IPC model and @MASTG-KNOW-0025 for implicit intents.

## Registration

A broadcast receiver can be made known to the system in two ways.

**Manifest-declared receivers** are declared in the `AndroidManifest.xml` file with a [`<receiver>`](https://developer.android.com/guide/topics/manifest/receiver-element) element and usually an associated intent filter:

```xml
<receiver android:name=".MyReceiver" android:exported="false">
    <intent-filter>
        <action android:name="com.example.app.MY_ACTION" />
    </intent-filter>
</receiver>
```

**Context-registered receivers** are registered at runtime by calling [`Context.registerReceiver`](https://developer.android.com/reference/android/content/Context#registerReceiver(android.content.BroadcastReceiver,%20android.content.IntentFilter)) with an [`IntentFilter`](https://developer.android.com/reference/android/content/IntentFilter), and unregistered with [`unregisterReceiver`](https://developer.android.com/reference/android/content/Context#unregisterReceiver(android.content.BroadcastReceiver)). They receive broadcasts only while their registering context is valid.

Since Android 13 (API level 33), apps that register context-registered receivers for broadcasts from other apps must specify [`RECEIVER_EXPORTED` or `RECEIVER_NOT_EXPORTED`](https://developer.android.com/guide/components/broadcasts#context-registered-receivers) when calling `registerReceiver`.

## Sending Broadcasts

Apps send broadcasts with methods on [`Context`](https://developer.android.com/reference/android/content/Context):

- [`sendBroadcast`](https://developer.android.com/reference/android/content/Context#sendBroadcast(android.content.Intent)): delivers to all interested receivers in an undefined order.
- [`sendOrderedBroadcast`](https://developer.android.com/reference/android/content/Context#sendOrderedBroadcast(android.content.Intent,%20java.lang.String)): delivers to one receiver at a time according to priority, allowing a receiver to abort the broadcast or pass data to the next.
- [`LocalBroadcastManager`](https://developer.android.com/reference/androidx/localbroadcastmanager/content/LocalBroadcastManager) historically kept broadcasts within a single app. It is now [deprecated](https://developer.android.com/reference/androidx/localbroadcastmanager/content/LocalBroadcastManager); Google recommends alternatives such as [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData) or observable data holders for in-app communication.

## Implicit vs Explicit Broadcasts

A broadcast is **implicit** when it doesn't target a specific app. After receiving an implicit broadcast, the system lists all receivers registered for the action and delivers it to each. A broadcast is **explicit** when it names a target package or component.

Background execution limits constrain implicit broadcasts:

- Apps targeting Android 8.0 (API level 26) or higher can't register most implicit broadcasts in the manifest, with limited [exceptions](https://developer.android.com/guide/components/broadcast-exceptions). Context-registered receivers are not affected.
- [Sticky broadcasts](https://developer.android.com/privacy-and-security/risks/sticky-broadcast), sent with the deprecated `sendStickyBroadcast` family of methods, persist after delivery and offer no access control.

## Priority

A receiver's priority for ordered broadcasts can be set with the [`android:priority`](https://developer.android.com/guide/topics/manifest/intent-filter-element#priority) attribute in an intent filter or programmatically with [`IntentFilter.setPriority`](https://developer.android.com/reference/android/content/IntentFilter#setPriority(int)). Receivers with the same priority run in an arbitrary order.

## Access Control

Whether other apps can deliver broadcasts to a receiver, or receive broadcasts the app sends, is governed by:

- [`android:exported`](https://developer.android.com/guide/topics/manifest/receiver-element#exported): when `true`, components of other apps can send broadcasts to the receiver unless another control, such as `android:permission`, blocks the sender. When `false`, access is limited to the same app, apps with the same user ID, or privileged system components. When a manifest receiver declares an intent filter, the default historically became `true`; in its absence the default is `false`. Since Android 12 (API level 31) the attribute must be set explicitly when an intent filter is present.
- [`android:permission`](https://developer.android.com/guide/topics/manifest/receiver-element#prmsn): requires the sender to hold a specific permission to deliver broadcasts to the receiver. If the sender is not granted the permission, the intent is not delivered to the receiver. Combined with a custom permission and an appropriate [`android:protectionLevel`](https://developer.android.com/guide/topics/manifest/permission-element#plevel) (for example, `signature`), this restricts which apps can interact with the component. See @MASTG-KNOW-0017 for permission protection levels, component permission enforcement, and custom permissions, and see [Permission-based access control to exported components](https://developer.android.com/privacy-and-security/risks/access-control-to-exported-components).
- Setting an explicit target package or component on the `Intent` limits delivery to that app. See [Security considerations and best practices](https://developer.android.com/guide/components/broadcasts#security-and-best-practices) and [Insecure broadcast receivers](https://developer.android.com/privacy-and-security/risks/insecure-broadcast-receiver).

For an overview of how to restrict access to exported components, see @MASTG-BEST-0x03.
