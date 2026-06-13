---
masvs_category: MASVS-RESILIENCE
platform: ios
title: Simulator Detection
---

## Overview

In the context of anti-reversing, the goal of emulator and virtual device detection is to increase the difficulty of running the app on an emulated device. This increased difficulty forces the reverse engineer to defeat the checks or use a physical device, thereby limiting the access required for large-scale device analysis.

Apple's operating system provides a software called "Simulator", shipped with Xcode. The Simulator does not try to emulate the `arm64` architecture of iOS devices.

As discussed in the section [Testing on the iOS Simulator](../../../Document/0x06b-iOS-Security-Testing.md#testing-on-the-ios-simulator), simulator binaries are compiled to macOS operating system code instead of the iOS operating system code.

Therefore, apps from App Store (and overall, apps compiled for iOS physical devices) do not need to detect the presence of an iOS Simulator as they can't be installed or executed in such platform.

!!! note
  Do not misunderstand the iOS Simulator and virtual devices. Apps from App Store can run in virtual devices as they are devices that emulate the complete architecture of the physical devices. For more information on virtual devices, check @MASTG-KNOW-009x.

Although apps from App Store cannot be executed in an iOS Simulator, there are several indicators that can be used by the app to know if the device in question is an iOS Simulator.

### Runtime environment check

The following indicator uses the [runtime environment](https://developer.apple.com/documentation/foundation/processinfo/environment) process information to check if the environment variable `SIMULATOR_DEVICE_NAME` has been set (this variable is set by the iOS Simulator when spawning the app process):

```swift
private static func isSimulatorEnv() -> Bool {
    return ProcessInfo.processInfo.environment["SIMULATOR_DEVICE_NAME"] != nil // If this is true, app could be in a Simulator as the variable has been set.
  }
```

### Usage of compiler directives

Swift compiler directives can be used so that the compiler adds specific code to the binary when the destination target is an iOS Simulator (remember that the resulting binary for the application differs from a physical device binary). **These compiler directives can be used in code so that the app detects in runtime if it is a compiled binary for the iOS Simulator:**

```swift
#if targetEnvironment(simulator)
    // This code will only get compiled for iOS Simulator binaries
#endif 
```
