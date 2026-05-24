// SUMMARY: This sample demonstrates reverse engineering tools detection in Swift by inspecting loaded dynamic libraries using _dyld_image_count and _dyld_get_image_name to check for known Frida artifacts, and by probing TCP port 27042 for a frida-server response.

import SwiftUI
import Foundation

class MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        let result = ReverseEngineeringToolsDetector.detect()
        completion(result)
    }
}

class ReverseEngineeringToolsDetector {
    static func detect() -> String {
        var detections = [String]()

        // FAIL: [MASTG-TEST-0x01] Check for Frida-related dynamic libraries by iterating loaded dylibs.
        let fridaArtifacts = ["FridaGadget", "frida-agent", "cynject", "libcycript"]
        let imageCount = _dyld_image_count()
        for i in 0..<imageCount {
            if let imageName = _dyld_get_image_name(i) {
                let name = String(cString: imageName)
                for artifact in fridaArtifacts {
                    if name.lowercased().contains(artifact.lowercased()) {
                        detections.append("Detected reverse engineering tool library: \(name)")
                    }
                }
            }
        }

        // FAIL: [MASTG-TEST-0x01] Probe TCP port 27042 for frida-server (default port).
        if isFridaServerRunning(host: "127.0.0.1", port: 27042) {
            detections.append("Detected frida-server listening on port 27042")
        }

        if detections.isEmpty {
            return "Reverse Engineering Tools: Not Detected ✅\n\nNo known reverse engineering tool artifacts were found."
        } else {
            return "Reverse Engineering Tools: Detected ⚠️\n\nDetections:\n" + detections.joined(separator: "\n")
        }
    }

    private static func isFridaServerRunning(host: String, port: Int32) -> Bool {
        let sock = socket(AF_INET, SOCK_STREAM, 0)
        guard sock >= 0 else { return false }
        defer { close(sock) }

        var timeout = timeval(tv_sec: 0, tv_usec: 500_000) // 0.5 second timeout
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, socklen_t(MemoryLayout<timeval>.size))
        setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &timeout, socklen_t(MemoryLayout<timeval>.size))

        var addr = sockaddr_in()
        addr.sin_family = sa_family_t(AF_INET)
        addr.sin_port = UInt16(port).bigEndian
        addr.sin_addr.s_addr = inet_addr(host)

        let connected = withUnsafePointer(to: &addr) {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {
                connect(sock, $0, socklen_t(MemoryLayout<sockaddr_in>.size))
            }
        }

        return connected == 0
    }
}
