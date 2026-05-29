// SOURCE: Based on the original MASTG-TEST-0069 dynamic analysis approach

// Hook authorization status methods for various iOS frameworks
// This script traces permission-related API calls at runtime

console.log("\n[*] Starting Permission Authorization Status Tracing...\n");

// Hook CLLocationManager authorization status
if (ObjC.available) {
    try {
        Interceptor.attach(ObjC.classes.CLLocationManager["+ authorizationStatus"].implementation, {
            onEnter: function(args) {
                console.log("\n[+] CLLocationManager.authorizationStatus called");
            },
            onLeave: function(retval) {
                const status = retval.toInt32();
                const statusMap = {
                    0: "notDetermined",
                    1: "restricted",
                    2: "denied",
                    3: "authorizedAlways",
                    4: "authorizedWhenInUse"
                };
                console.log("    Return value: " + status + " (" + (statusMap[status] || "unknown") + ")");
                console.log("\nBacktrace:");
                console.log(Thread.backtrace(this.context, Backtracer.ACCURATE)
                    .map(DebugSymbol.fromAddress).slice(0, 5).join("\n"));
            }
        });
        console.log("[*] Hooked CLLocationManager.authorizationStatus");
    } catch (e) {
        console.log("[-] Failed to hook CLLocationManager: " + e);
    }

    // Hook PHPhotoLibrary authorization status
    try {
        Interceptor.attach(ObjC.classes.PHPhotoLibrary["+ authorizationStatus"].implementation, {
            onEnter: function(args) {
                console.log("\n[+] PHPhotoLibrary.authorizationStatus called");
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
        console.log("[*] Hooked PHPhotoLibrary.authorizationStatus");
    } catch (e) {
        console.log("[-] Failed to hook PHPhotoLibrary: " + e);
    }

    // Hook CNContactStore authorization status
    try {
        Interceptor.attach(ObjC.classes.CNContactStore["+ authorizationStatusForEntityType:"].implementation, {
            onEnter: function(args) {
                console.log("\n[+] CNContactStore.authorizationStatusForEntityType called");
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
