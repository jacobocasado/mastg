// SUMMARY: This sample shows an exported broadcast receiver that reads a phone number
// and a new password from the received intent and discloses the stored password by SMS.
// Any app can trigger it. Inspired by the "Android Insecure Bank" receiver.

package org.owasp.mastestapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class PasswordResetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // FAIL: [MASTG-TEST-0x03] The receiver is exported and requires no permission.
        // It acts on unvalidated extras from the intent and discloses the stored
        // password by SMS, so any app can trigger this by sending the broadcast.
        val phoneNumber = intent.getStringExtra("phonenumber") ?: return
        val newPassword = intent.getStringExtra("newpass")

        val currentPassword = CredentialStore(context).getPassword()
        val message = "Password changed from $currentPassword to $newPassword"

        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null)
    }
}
