---
title: Enumerating Activities
platform: android
---

You can enumerate the activities an app declares, and determine which of them are exported, by inspecting the app's `AndroidManifest.xml` or by querying the running system. See @MASTG-KNOW-0x01 for background on activities and the `android:exported` attribute.

Prefer static analysis of the manifest first, as it doesn't require a device and reflects exactly what the app declares. Use the device- and tool-based options when you also need to confirm runtime behavior.

## Using the AndroidManifest

Extract and decode the `AndroidManifest.xml` as described in @MASTG-TECH-0117, then analyze it as described in @MASTG-TECH-0150. Look for [`<activity>`](https://developer.android.com/guide/topics/manifest/activity-element) and [`<activity-alias>`](https://developer.android.com/guide/topics/manifest/activity-alias-element) elements.

An activity is exported, and therefore reachable by other apps, when either of the following is true:

- It sets [`android:exported="true"`](https://developer.android.com/guide/topics/manifest/activity-element#exported).
- It declares an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) and does not set `android:exported="false"`.

For example, with the manifest extracted to standard XML, you can list activity declarations with:

```bash
xmlstarlet sel -t -m "//activity" -v "@android:name" -o " exported=" -v "@android:exported" -n AndroidManifest.xml
```

Note any [`android:permission`](https://developer.android.com/guide/topics/manifest/activity-element#prmsn) attribute, which restricts access to callers holding that permission.

## Using @MASTG-TOOL-0124

`aapt2` prints the components declared in the manifest, including activities, without decoding the full XML:

```bash
aapt2 d xmltree app.apk --file AndroidManifest.xml | grep -A3 "E: activity"
```

In the raw output, an exported activity has `android:exported` set to `0xffffffff` (true) or declares a nested `intent-filter`.

## Using @MASTG-TOOL-0004

On a device or emulator with the app installed, you can list activities with the package manager:

```bash
adb shell dumpsys package <package_name> | grep -A1 "Activity Resolver Table" 
adb shell cmd package query-activities --components -a android.intent.action.MAIN
```

To launch an exported activity and observe its behavior, use the activity manager:

```bash
# Start an activity by component name
adb shell am start -n <package_name>/<activity_name>

# Start an activity specifying an action and a category
adb shell am start -n <package_name>/<activity_name> -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
```

## Using @MASTG-TOOL-0015

As a last resort, when manifest and `adb` inspection aren't sufficient, drozer can enumerate exported activities and start them. Use it when you need its intent-crafting helpers:

```bash
run app.activity.info -a <package_name>
run app.activity.start --component <package_name> <activity_name>
```
