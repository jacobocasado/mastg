package org.owasp.mastestapp.attacker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class AttackerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val action = intent.action
        if (action == Intent.ACTION_MAIN) {
            val textView = TextView(this).apply {
                text = "Hello in the attacker Demo app"
                textSize = 20f
                gravity = Gravity.CENTER
                setTextColor(android.graphics.Color.BLACK)
            }
            setContentView(textView)
        } else {
            // Return a URI pointing to the attacker's own ContentProvider
            // Note: Authority must match what is defined in AndroidManifest.xml
            val resultIntent = Intent()
            resultIntent.data = Uri.parse("content://org.owasp.mastestapp.attacker/fakeFile")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
