// SUMMARY: This sample shows an exported activity that displays stored credentials
// without re-checking the authentication state, allowing other apps to bypass the
// login screen by starting it directly. Inspired by the "Sieve" password manager.

package org.owasp.mastestapp

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class PasswordListActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FAIL: [MASTG-TEST-0x01] The activity is exported (via its intent filter in
        // the manifest) and does not verify that the user has authenticated. Any app
        // can start it with `am start -a org.owasp.mastestapp.SHOW_PASSWORDS` and read
        // the stored credentials, bypassing MainLoginActivity.
        val credentials = CredentialStore(this).loadAll()

        val view = TextView(this)
        view.text = credentials.joinToString("\n") { "${it.service}: ${it.username} / ${it.password}" }
        setContentView(view)
    }
}
