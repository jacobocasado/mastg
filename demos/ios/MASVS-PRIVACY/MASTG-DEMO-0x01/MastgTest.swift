// SUMMARY: This sample demonstrates an iOS app that requests multiple permissions via purpose strings in Info.plist.

import SwiftUI
import CoreLocation
import Contacts
import Photos

struct MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0313] The app declares NSLocationWhenInUseUsageDescription but the permission may be excessive for its functionality.
        let locationManager = CLLocationManager()
        let locationStatus = locationManager.authorizationStatus
        
        // FAIL: [MASTG-TEST-0313] The app declares NSContactsUsageDescription but the permission may be excessive for its functionality.
        let contactsStatus = CNContactStore.authorizationStatus(for: .contacts)
        
        // FAIL: [MASTG-TEST-0313] The app declares NSPhotoLibraryUsageDescription but the permission may be excessive for its functionality.
        let photosStatus = PHPhotoLibrary.authorizationStatus()
        
        let result = """
        Permission Status Check:
        - Location: \(locationStatus.rawValue)
        - Contacts: \(contactsStatus.rawValue)
        - Photos: \(photosStatus.rawValue)
        """
        
        completion(result)
    }
}
