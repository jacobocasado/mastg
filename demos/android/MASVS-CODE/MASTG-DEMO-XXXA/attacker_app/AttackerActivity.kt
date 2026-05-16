package org.owasp.mastestapp.attacker

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.view.Gravity

class AttackerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val action = intent.action
        val textToShow = if (action == android.content.Intent.ACTION_MAIN) {
            "Hello in the attacker Demo app"
        } else {
            val extras = intent.extras
            val args = extras?.keySet()?.joinToString { key -> "$key=${extras.get(key)}" } ?: "None"
            "Intercepted intent! \nArguments: $args"
        }

        val textView = TextView(this).apply {
            text = textToShow
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(android.graphics.Color.BLACK)
        }
        setContentView(textView)
    }
}
