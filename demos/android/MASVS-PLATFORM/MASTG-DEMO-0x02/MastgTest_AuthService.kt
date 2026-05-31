// SUMMARY: This sample shows an exported bound service that exposes a Messenger
// interface to change the app's password without verifying the caller's permission.
// Any app can bind to it and reset the password. Inspired by the "Sieve" AuthService.

package org.owasp.mastestapp

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger

class AuthService : Service() {

    companion object {
        const val MSG_SET_PASSWORD = 6345
        const val KEY_PASSWORD = "org.owasp.mastestapp.PASSWORD"
    }

    private val messenger = Messenger(IncomingHandler(this))

    override fun onBind(intent: Intent): IBinder = messenger.binder

    private class IncomingHandler(val service: AuthService) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // FAIL: [MASTG-TEST-0x02] The service changes the stored password based on a
            // Messenger request without calling checkCallingPermission() or otherwise
            // verifying the caller, so any app bound to this exported service can reset it.
            when (msg.what) {
                MSG_SET_PASSWORD -> {
                    val newPassword = msg.data.getString(KEY_PASSWORD)
                    if (newPassword != null) {
                        CredentialStore(service).setPassword(newPassword)
                    }
                }
            }
        }
    }
}
