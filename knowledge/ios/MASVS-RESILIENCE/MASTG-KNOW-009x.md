---
masvs_category: MASVS-RESILIENCE
platform: ios
title: Virtual Devices Detection
---

## Overview

In the context of anti-reversing, the goal of emulator and virtual device detection is to increase the difficulty of running the app on an emulated or virtualized device. This increased difficulty forces the reverse engineer to defeat the checks or use a physical device, thereby limiting the access required for large-scale device analysis.

Virtual devices are environments that virtualize the hardware and operating system expected by iOS apps compiled for physical devices. Unlike the iOS Simulator, they can run iOS device binaries because they preserve the relevant iOS architecture and runtime environment.

Since its release, [Corellium](https://www.corellium.com/) (commercial tool) has made iOS virtualization available, [setting itself apart from the iOS simulator](https://www.corellium.com/compare/ios-simulator "Corellium vs Apple\'s iOS Simulator").

Corellium claims that their virtual devices run on a proprietary hypervisor **and are not emulators**, which means that the iOS architecture is maintained and iOS apps can run in Corellium virtual devices, enabling reverse engineering of iOS apps using these virtual devices.

!!! note
    Do not confuse virtual devices with the iOS Simulator. App Store apps can't run in the iOS Simulator, but they can run in virtual devices because virtual devices emulate the complete architecture of physical devices. For more information on the iOS Simulator, check @MASTG-KNOW-0088.

!!! note
    Virtual device detection is inherently a cat-and-mouse game. Detection methods and bypass techniques evolve continuously, and determined attackers with sufficient time and resources can circumvent these protections, for example, by hooking or patching the detection logic. These techniques should be part of a defense-in-depth strategy, not a standalone solution.

There are several indicators that the device in question is being emulated or virtualized. The main detection strategy is to identify features and limitations of commonly used emulation or virtual device solutions.

### App Attest checks

Apple's [App Attest service](https://developer.apple.com/documentation/devicecheck/dcappattestservice) works by creating a hardware-based cryptographic key in the Secure Enclave of the device, then using Apple's service so **your server can validate that requests come from a legitimate instance of your app** (meaning that this check is server-based).

Corellium states that [users cannot directly download an App Store app onto a Corellium device](https://support.corellium.com/features/apps/testing-ios-apps) and cannot load an encrypted App Store app copied from another device. Instead, they can only test a signed, unencrypted version of the app.

Therefore, when an App Store app is extracted from a physical device, decrypted, and then sideloaded or re-signed for execution on Corellium or another virtual device, it should no longer be treated as the same production app instance that the backend expects to validate through App Attest. This modification flow alters the app's provisioning signature, which doesn't match the app identity expected by the server's App Attest validation logic.

!!! note
    This check is not limited to detecting virtual devices such as Corellium. The same App Attest validation failure can also occur when an App Store app is extracted, decrypted, and re-signed for execution on a physical device, because the relevant security property is not whether the device is virtualized, but whether the app instance still matches the genuine production identity expected by the backend. As a result, App Attest can help cover a broader class of tampering and repackaging scenarios beyond virtual-device execution alone.

### Hardware elements presence

Virtual devices can be detected by trying to interact with hardware components using operating system APIs, as these devices can't interact with these hardware components in the same way as physical devices.

The [official Corellium iOS documentation](https://support.corellium.com/devices/ios) claims that their virtual devices do not support GPU hardware emulation ([Metal](https://developer.apple.com/documentation/metal/gpu_devices_and_work_submission/getting_the_default_gpu "Apple Metal Framework")), NFC or Bluetooth.

Apple exposes methods to query the presence of these hardware elements:

#### Metal GPU

```swift
import Metal

let hasGPU = MTLCreateSystemDefaultDevice() != nil // Check if app can use the GPU hardware
```

#### NFC

```swift
import CoreNFC

let hasNFC = NFCNDEFReaderSession.readingAvailable // Check if the app can use the NFC hardware
```

#### Bluetooth

```swift
import CoreBluetooth

final class BTProbe: NSObject, CBCentralManagerDelegate {
    private var manager: CBCentralManager!
    var state: CBManagerState = .unknown

    override init() {
        super.init()
        manager = CBCentralManager(delegate: self, queue: nil)
    }

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        state = central.state // Check if app can use Bluetooth hardware
    }
}
```

If `state` is `.unsupported`, Bluetooth is not supported by the device. Note that `.poweredOff` state means Bluetooth is off, but available.

### Device properties

The `sysctlbyname` [call](https://developer.apple.com/documentation/kernel/1387446-sysctlbyname) can be used to check information about the device properties.

For example, `sysctlbyname("hw.machine")` reports the platform identifier (the device model), like "iPhone14".

These properties can be used to cross-check if the device should support the previous hardware capabilities. A claimed device model whose capabilities do not match what that model should expose is another indicator of a virtual device.

### Presence of specific virtualization engine files

It is also possible to check for specific files that the virtualization engine might create in the virtual device and that aren't part of a standard iOS device.

Corellium virtual devices, for example, leave a system process binary in `/usr/libexec/corelliumd`, which can be checked for its presence.

The following example checks for the presence of such a file using the `stat` call:

```c
#include <sys/stat.h>

struct stat st;
return stat("/usr/libexec/corelliumd", &st) == 0; // If true, stat was successful and Corellium system process exists.
```
