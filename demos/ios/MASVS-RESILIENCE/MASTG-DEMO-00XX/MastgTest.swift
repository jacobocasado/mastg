import Foundation
import CoreBluetooth
import Metal
import Darwin

struct MastgTest {
    // SUMMARY: This sample demonstrates iOS virtual-device detection by executing the local indicators from MASTG-KNOW-009x and reporting a final verdict in the UI.

    private static var activeDetector: VirtualDeviceDetector?

    static func mastgTest(completion: @escaping (String) -> Void) {
        let detector = VirtualDeviceDetector()
        activeDetector = detector
        detector.run { report in
            activeDetector = nil
            completion(report)
        }
    }
}

private enum IndicatorLevel {
    case expected
    case suspicious
    case confirmed
    case inconclusive

    var label: String {
        switch self {
        case .expected:
            return "expected"
        case .suspicious:
            return "suspicious"
        case .confirmed:
            return "confirmed"
        case .inconclusive:
            return "inconclusive"
        }
    }
}

private struct IndicatorResult {
    let order: Int
    let name: String
    let observed: String
    let level: IndicatorLevel
    let reason: String
}

private final class VirtualDeviceDetector: NSObject, CBCentralManagerDelegate {
    private var completion: ((String) -> Void)?
    private var bluetoothManager: CBCentralManager?
    private var bluetoothResultRecorded = false
    private var results = [IndicatorResult]()
    private var finished = false
    private var machineIdentifier = "unknown"

    func run(completion: @escaping (String) -> Void) {
        self.completion = completion
        machineIdentifier = currentMachineIdentifier()
        runSynchronousChecks()

        // PASS: [MASTG-TEST-0x92] The app queries Bluetooth support through CBCentralManager as a virtual-device indicator.
        bluetoothManager = CBCentralManager(
            delegate: self,
            queue: nil,
            options: [CBCentralManagerOptionShowPowerAlertKey: false]
        )

        if let bluetoothManager {
            let state = bluetoothManager.state
            if state != .unknown {
                recordBluetoothState(state)
            }
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) { [weak self] in
            self?.finishIfNeeded()
        }
    }

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        recordBluetoothState(central.state)
        finishIfNeeded()
    }

    private func runSynchronousChecks() {
        let machineLevel: IndicatorLevel = machineIdentifier == "unknown" ? .inconclusive : .expected
        let machineReason: String

        if machineIdentifier == "unknown" {
            machineReason = "The app could not resolve the device model, so hardware cross-checks are limited."
        } else {
            machineReason = "The reported model can be cross-checked against the hardware capabilities observed at runtime."
        }

        // PASS: [MASTG-TEST-0x92] The app queries hw.machine through sysctlbyname as a virtual-device indicator.
        results.append(
            IndicatorResult(
                order: 1,
                name: "hw.machine",
                observed: machineIdentifier,
                level: machineLevel,
                reason: machineReason
            )
        )

        // PASS: [MASTG-TEST-0x92] The app checks whether a Metal GPU is available.
        let metalDevice = MTLCreateSystemDefaultDevice()
        if metalDevice != nil {
            results.append(
                IndicatorResult(
                    order: 2,
                    name: "Metal GPU",
                    observed: "available",
                    level: .expected,
                    reason: "A Metal device is available, which is consistent with a physical iOS device."
                )
            )
        } else {
            results.append(
                IndicatorResult(
                    order: 2,
                    name: "Metal GPU",
                    observed: "missing",
                    level: .suspicious,
                    reason: "No Metal device is available, which is unusual for a supported physical iOS device."
                )
            )
        }

        // PASS: [MASTG-TEST-0x92] The app checks for the Corellium daemon file.
        let corelliumDaemonPresent = corelliumDaemonExists()
        if corelliumDaemonPresent {
            results.append(
                IndicatorResult(
                    order: 4,
                    name: "Corellium daemon",
                    observed: "present",
                    level: .confirmed,
                    reason: "The file /usr/libexec/corelliumd exists, which is a strong Corellium indicator."
                )
            )
        } else {
            results.append(
                IndicatorResult(
                    order: 4,
                    name: "Corellium daemon",
                    observed: "not found",
                    level: .expected,
                    reason: "The file /usr/libexec/corelliumd was not found."
                )
            )
        }
    }

    private func recordBluetoothState(_ state: CBManagerState) {
        guard !bluetoothResultRecorded else {
            return
        }

        bluetoothResultRecorded = true

        let observed = "\(bluetoothStateDescription(state)) (\(state.rawValue))"
        let level: IndicatorLevel
        let reason: String

        switch state {
        case .poweredOn:
            level = .expected
            reason = "Bluetooth hardware is available and powered on."
        case .poweredOff:
            level = .expected
            reason = "Bluetooth hardware is available but currently powered off."
        case .resetting:
            level = .expected
            reason = "Bluetooth hardware is available, but CoreBluetooth is temporarily resetting."
        case .unsupported:
            level = .suspicious
            reason = "Bluetooth is reported as unsupported, which is unusual for a physical iOS device."
        case .unauthorized:
            level = .inconclusive
            reason = "Bluetooth access is not authorized, so this indicator cannot confirm hardware availability."
        case .unknown:
            level = .inconclusive
            reason = "CoreBluetooth has not finished resolving the hardware state yet."
        @unknown default:
            level = .inconclusive
            reason = "CoreBluetooth returned an unknown state."
        }

        results.append(
                IndicatorResult(
                    order: 3,
                    name: "Bluetooth",
                    observed: observed,
                    level: level,
                reason: reason
            )
        )
    }

    private func finishIfNeeded() {
        guard !finished else {
            return
        }

        finished = true

        if !bluetoothResultRecorded {
            results.append(
                IndicatorResult(
                    order: 3,
                    name: "Bluetooth",
                    observed: "timeout",
                    level: .inconclusive,
                    reason: "No Bluetooth state was received before the timeout expired."
                )
            )
        }

        let report = buildReport()
        DispatchQueue.main.async { [completion] in
            completion?(report)
        }
    }

    private func buildReport() -> String {
        let orderedResults = results.sorted { lhs, rhs in
            lhs.order < rhs.order
        }

        let confirmedResults = orderedResults.filter { $0.level == .confirmed }
        let suspiciousResults = orderedResults.filter { $0.level == .suspicious }

        var lines = [
            "Virtual device detection results (MASTG-KNOW-009x)",
            "",
            "Indicators:"
        ]

        for result in orderedResults {
            lines.append("- \(result.name): \(result.observed) [\(result.level.label)] - \(result.reason)")
        }

        lines.append("")
        lines.append("Verdict:")

        if !confirmedResults.isEmpty || suspiciousResults.count >= 2 {
            lines.append("- Likely virtual device.")

            for result in confirmedResults + suspiciousResults {
                lines.append("  - \(result.name): \(result.reason)")
            }
        } else if suspiciousResults.count == 1 {
            lines.append("- No strong virtual-device verdict yet. Review the suspicious indicator below.")
            lines.append("  - \(suspiciousResults[0].name): \(suspiciousResults[0].reason)")
        } else {
            lines.append("- Likely physical device. No strong virtual-device indicators were observed.")
        }

        return lines.joined(separator: "\n")
    }

    private func currentMachineIdentifier() -> String {
        var size: size_t = 0

        guard sysctlbyname("hw.machine", nil, &size, nil, 0) == 0, size > 1 else {
            return "unknown"
        }

        var buffer = [CChar](repeating: 0, count: Int(size))
        let result = buffer.withUnsafeMutableBufferPointer { pointer in
            sysctlbyname("hw.machine", pointer.baseAddress, &size, nil, 0)
        }

        guard result == 0 else {
            return "unknown"
        }

        return String(cString: buffer)
    }

    private func corelliumDaemonExists() -> Bool {
        var fileInfo = stat()
        return "/usr/libexec/corelliumd".withCString { path in
            stat(path, &fileInfo) == 0
        }
    }
    private func bluetoothStateDescription(_ state: CBManagerState) -> String {
        switch state {
        case .unknown:
            return ".unknown"
        case .resetting:
            return ".resetting"
        case .unsupported:
            return ".unsupported"
        case .unauthorized:
            return ".unauthorized"
        case .poweredOff:
            return ".poweredOff"
        case .poweredOn:
            return ".poweredOn"
        @unknown default:
            return ".unknown"
        }
    }
}
