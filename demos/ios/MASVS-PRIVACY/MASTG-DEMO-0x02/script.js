// Hook authorization status methods for various iOS frameworks
// This script traces permission-related API calls at runtime

console.log("\n[*] Starting Permission Authorization Status Tracing...\n");

const printBacktrace = (context, maxLines = 8) => {
    console.log("\nBacktrace:");
    let backtrace = Thread.backtrace(context, Backtracer.ACCURATE)
        .map(DebugSymbol.fromAddress);

    for (let i = 0; i < Math.min(maxLines, backtrace.length); i++) {
        console.log(backtrace[i]);
    }
}

// Hook CLLocationManager.requestWhenInUseAuthorization (instance method, void return).
// Note: CLLocationManager.authorizationStatus is NOT used because the Swift compiler
// dispatches it directly into the dyld shared cache, bypassing ObjC objc_msgSend.
if (ObjC.available) {
    try {
        Interceptor.attach(ObjC.classes.CLLocationManager["- requestWhenInUseAuthorization"].implementation, {
            onEnter: function(args) {
                console.log("\n[+] CLLocationManager.requestWhenInUseAuthorization called");
                printBacktrace(this.context);
            }
        });
        console.log("[*] Hooked CLLocationManager.requestWhenInUseAuthorization");
    } catch (e) {
        console.log("[-] Failed to hook CLLocationManager: " + e);
    }

    // Hook PHPhotoLibrary.authorizationStatusForAccessLevel: (iOS 14+).
    // authorizationStatus(for:) in Swift maps to this selector.
    try {
        Interceptor.attach(ObjC.classes.PHPhotoLibrary["+ authorizationStatusForAccessLevel:"].implementation, {
            onEnter: function(args) {
                console.log("\n[+] PHPhotoLibrary.authorizationStatusForAccessLevel called");
                printBacktrace(this.context);
            },
            onLeave: function(retval) {
                const status = retval.toInt32();
                const statusMap = {
                    0: "notDetermined",
                    1: "restricted",
                    2: "denied",
                    3: "authorized",
                    4: "limited"
                };
                console.log("    Return value: " + status + " (" + (statusMap[status] || "unknown") + ")");
            }
        });
        console.log("[*] Hooked PHPhotoLibrary.authorizationStatusForAccessLevel");
    } catch (e) {
        console.log("[-] Failed to hook PHPhotoLibrary: " + e);
    }

    // Hook CNContactStore authorization status
    try {
        Interceptor.attach(ObjC.classes.CNContactStore["+ authorizationStatusForEntityType:"].implementation, {
            onEnter: function(args) {
                console.log("\n[+] CNContactStore.authorizationStatusForEntityType called");
                printBacktrace(this.context);
            },
            onLeave: function(retval) {
                const status = retval.toInt32();
                const statusMap = {
                    0: "notDetermined",
                    1: "restricted",
                    2: "denied",
                    3: "authorized"
                };
                console.log("    Return value: " + status + " (" + (statusMap[status] || "unknown") + ")");
            }
        });
        console.log("[*] Hooked CNContactStore.authorizationStatusForEntityType");
    } catch (e) {
        console.log("[-] Failed to hook CNContactStore: " + e);
    }
}

console.log("\n[*] Hooks installed. Interact with the app to trigger permission checks.\n");
