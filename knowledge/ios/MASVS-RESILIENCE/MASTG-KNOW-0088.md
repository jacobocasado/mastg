---
masvs_category: MASVS-RESILIENCE
platform: ios
title: Simulator Detection
---

## Overview

In the context of anti-reversing, the goal of emulator detection is to increase the difficulty of running the app on a non-physical device, which impedes some tools and techniques reverse engineers like to use. This increased difficulty forces the reverse engineer to defeat these or utilize the physical device, thereby barring the access required for large-scale device analysis.

Apple's operating system provides a software called "Simulator", shipped with XCode. This simulator mimics an iOS device, but does not try to emulate its architecture, and is not an iOS virtual machine.

At this moment, there is not an iOS "emulator" available.

As discussed in the section [Testing on the iOS Simulator](../../../Document/0x06b-iOS-Security-Testing.md#testing-on-the-ios-simulator "Testing on the iOS Simulator") in the basic security testing chapter, simulator binaries are compiled to macOS operating system code instead of iOS operating system code (independently of the architecture).

Therefore, apps from app store (or compiled for physical devices) do not need to detect the presence of an iOS Simulator as they can't be installed or executed in such platform.

## Techniques

Although apps from App Store cannot be executed in an iOS Simulator, there are several indicators that can be used by the app to know if the device in question is an iOS Simulator.

### Runtime environment check

The following indicator uses the [runtime environment](/https://developer.apple.com/documentation/foundation/processinfo/environment) process information to check if the environment variable "SIMULATOR_DEVICE_NAME" has been set (this variable is set by the iOS Simulator when spawning the app process):

```swift
private static func isSimulatorEnv() -> Bool {
    return ProcessInfo.processInfo.environment["SIMULATOR_DEVICE_NAME"] != nil // If this is true, app could be in a Simulator as the variable has been set.
  }
```

### Usage of compiler directives

Swift compiler directives can be used so that the compiler adds specific code to the binary when the destination target is an iOS simulator (remember that the resulting binary for the application differs from a physical device binary). **These compiler directives can be used in code so that the app detects in runtime if it is a compiled binary for the iOS emulator:**

```swift
#if targetEnvironment(simulator)
    // This code will only get compiled for iOS Simulator binaries
#endif 
```
