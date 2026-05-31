---
title: Enumerating Services
platform: android
---

You can enumerate the services an app declares, and determine which of them are exported, by inspecting the app's `AndroidManifest.xml` or by querying the running system. See @MASTG-KNOW-0x02 for background on services and the `android:exported` attribute.

Prefer static analysis of the manifest first, as it doesn't require a device and reflects exactly what the app declares. Use the device- and tool-based options when you also need to confirm runtime behavior.

## Using the AndroidManifest

Extract and decode the `AndroidManifest.xml` as described in @MASTG-TECH-0117, then analyze it as described in @MASTG-TECH-0150. Look for [`<service>`](https://developer.android.com/guide/topics/manifest/service-element) elements.

A service is exported, and therefore reachable by other apps, when either of the following is true:

- It sets [`android:exported="true"`](https://developer.android.com/guide/topics/manifest/service-element#exported).
- It declares an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) and does not set `android:exported="false"`.

For example, with the manifest extracted to standard XML, you can list service declarations with:

```bash
xmlstarlet sel -t -m "//service" -v "@android:name" -o " exported=" -v "@android:exported" -o " permission=" -v "@android:permission" -n AndroidManifest.xml
```

Note any [`android:permission`](https://developer.android.com/guide/topics/manifest/service-element#prmsn) attribute, which restricts which callers can start or bind to the service.

## Using @MASTG-TOOL-0124

`aapt2` prints the components declared in the manifest, including services, without decoding the full XML:

```bash
aapt2 d xmltree app.apk --file AndroidManifest.xml | grep -A3 "E: service"
```

In the raw output, an exported service has `android:exported` set to `0xffffffff` (true) or declares a nested `intent-filter`.

## Using @MASTG-TOOL-0004

On a device or emulator with the app installed, you can list services with the package manager and start one with the activity manager:

```bash
# List the services declared by a package
adb shell dumpsys package <package_name> | grep -i service

# Start a service by component name
adb shell am startservice -n <package_name>/<service_name>
```

To interact with a bound service, you first need to identify the inputs it expects (for example, the `Message` codes of a `Messenger` interface or the methods of an AIDL interface) through static analysis, as described in @MASTG-TECH-0023.

## Using @MASTG-TOOL-0015

As a last resort, when manifest and `adb` inspection aren't sufficient, drozer can enumerate exported services and send messages to them. Use it when you need to craft and deliver structured `Message` objects to a bound service:

```bash
run app.service.info -a <package_name>
run app.service.send <package_name> <service_name> --msg <what> <arg1> <arg2> --extra <type> <key> <value> --bundle-as-obj
```
