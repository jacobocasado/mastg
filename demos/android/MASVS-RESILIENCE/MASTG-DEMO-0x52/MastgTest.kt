package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.util.Log

// SUMMARY: This sample demonstrates a native root detection routine based on common su binary paths.
class MastgTest(private val context: Context) {

    // FAIL: [MASTG-TEST-0x51-2] Due to weak code obfuscation, the root detection logic remains identifiable in the decompiled library.
    val shouldRunInMainThread = true

    fun mastgTest(): String {
        val results = DemoResults("0x52")

        return try {
            val detectedPath = findRootArtifactPath()

            if (detectedPath != null) {
                val message = "Detected root artifact path '$detectedPath'. The app closes when a monitored su path is found."
                Log.w("MASTG-DEMO-0x52", message)
                results.add(Status.FAIL, message)
                closeApp()
            } else {
                // PASS: [MASTG-TEST-0x51-2] None of the monitored su paths were present.
                results.add(Status.PASS, "No monitored su path was found.")
            }

            results.toJson()
        } catch (e: Exception) {
            results.add(Status.ERROR, e.toString())
            results.toJson()
        }
    }

    private fun closeApp() {
        val activity = context as? Activity ?: return
        activity.finishAffinity()
    }

    private external fun findRootArtifactPath(): String?

    companion object {
        init {
            System.loadLibrary("rootcheck")
        }
    }
}
