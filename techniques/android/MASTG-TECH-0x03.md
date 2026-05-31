---
title: Enumerating Broadcast Receivers
platform: android
---

You can enumerate the broadcast receivers an app declares, and determine which of them are exported, by inspecting the app's `AndroidManifest.xml` or by querying the running system. See @MASTG-KNOW-0x03 for background on broadcast receivers and the `android:exported` attribute.

Prefer static analysis of the manifest first, as it doesn't require a device and reflects exactly what the app declares. Use the device- and tool-based options when you also need to confirm runtime behavior. Note that context-registered receivers (registered at runtime with `Context.registerReceiver`) don't appear in the manifest and require code or runtime analysis.

## Using the AndroidManifest

Extract and decode the `AndroidManifest.xml` as described in @MASTG-TECH-0117, then analyze it as described in @MASTG-TECH-0150. Look for [`<receiver>`](https://developer.android.com/guide/topics/manifest/receiver-element) elements.

A manifest-declared receiver is exported, and therefore reachable by other apps, when either of the following is true:

- It sets [`android:exported="true"`](https://developer.android.com/guide/topics/manifest/receiver-element#exported).
- It declares an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) and does not set `android:exported="false"`.

For example, with the manifest extracted to standard XML, you can list receiver declarations with:

```bash
xmlstarlet sel -t -m "//receiver" -v "@android:name" -o " exported=" -v "@android:exported" -o " permission=" -v "@android:permission" -n AndroidManifest.xml
```

Note any [`android:permission`](https://developer.android.com/guide/topics/manifest/receiver-element#prmsn) attribute, which restricts which senders can deliver broadcasts to the receiver.

## Identifying Context-Registered Receivers

Receivers created at runtime are registered by calling [`Context.registerReceiver`](https://developer.android.com/reference/android/content/Context#registerReceiver(android.content.BroadcastReceiver,%20android.content.IntentFilter)). To find them, search the reverse-engineered code for calls to `registerReceiver` and for subclasses of [`BroadcastReceiver`](https://developer.android.com/reference/android/content/BroadcastReceiver), as described in @MASTG-TECH-0014. Check whether the call specifies `RECEIVER_NOT_EXPORTED`, which since Android 13 (API level 33) prevents other apps from delivering broadcasts to the receiver.

## Using @MASTG-TOOL-0124

`aapt2` prints the components declared in the manifest, including receivers, without decoding the full XML:

```bash
aapt2 d xmltree app.apk --file AndroidManifest.xml | grep -A3 "E: receiver"
```

In the raw output, an exported receiver has `android:exported` set to `0xffffffff` (true) or declares a nested `intent-filter`.

## Using @MASTG-TOOL-0004

On a device or emulator with the app installed, you can send a broadcast to a receiver and inspect pending broadcasts with `adb`:

```bash
# Send a broadcast with a specific action and string extras
adb shell am broadcast -a <action> --es <key> <value>

# Inspect recent broadcasts (extras are not shown)
adb shell dumpsys activity broadcasts | grep <action>
```

## Using @MASTG-TOOL-0015

As a last resort, when manifest and `adb` inspection aren't sufficient, drozer can enumerate exported receivers and send broadcasts to them:

```bash
run app.broadcast.info -a <package_name>
run app.broadcast.send --action <action> --extra string <key> <value>
```
