import SwiftUI
import UIKit

// SUMMARY: This sample demonstrates a login form where the password field is not masked
// because isSecureTextEntry is explicitly set to false, exposing the password in plain text.

struct MastgTest {

    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            let alert = UIAlertController(
                title: "Login",
                message: "Please enter your credentials.",
                preferredStyle: .alert
            )

            alert.addTextField { tf in
                tf.placeholder = "Username"
                tf.autocorrectionType = .no
                tf.accessibilityIdentifier = "username_field"
            }

            // FAIL: [MASTG-TEST-0x57] The password field has isSecureTextEntry set to false,
            // so the password is displayed in plain text instead of being masked.
            alert.addTextField { tf in
                tf.placeholder = "Password"
                tf.isSecureTextEntry = false
                tf.accessibilityIdentifier = "password_field"
            }

            alert.addAction(UIAlertAction(title: "Login", style: .default, handler: { _ in
                let username = alert.textFields?[0].text ?? ""
                completion("Logged in as: \(username)")
            }))

            if let presenter = topViewController() {
                presenter.present(alert, animated: true, completion: nil)
            } else {
                completion("Failed to present alert (no active view controller).")
            }
        }
    }

    private static func topViewController(
        base: UIViewController? = {
            let scenes = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
            let keyWindow = scenes
                .flatMap { $0.windows }
                .first { $0.isKeyWindow }
            return keyWindow?.rootViewController
        }()
    ) -> UIViewController? {
        if let nav = base as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController {
            return topViewController(base: tab.selectedViewController)
        }
        if let presented = base?.presentedViewController {
            return topViewController(base: presented)
        }
        return base
    }
}
