package org.owasp.mastestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream

// SUMMARY: This sample demonstrates the risk of processing unsanitized data from implicit intent results.
class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("XXXD")

        try {
            val intent = Intent(context, VulnerableActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            r.add(Status.FAIL, "Launched VulnerableActivity to demonstrate intent response handling")
        } catch (e: Exception) {
            r.add(Status.ERROR, e.toString())
        }

        return r.toJson()
    }
}

class VulnerableActivity : Activity() {

    companion object {
        private const val REQUEST_CODE_GET_CONTENT = 1337
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup directories
        val publicDir = File(filesDir, "public").apply { mkdirs() }
        val privateDir = File(filesDir, "private").apply { mkdirs() }
        File(privateDir, "secret.txt").writeText("Original Secret Content")

        // App requests a file using a custom implicit intent
        val intent = Intent("org.owasp.mastestapp.REQUEST_FILE")
        startActivityForResult(intent, REQUEST_CODE_GET_CONTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GET_CONTENT && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                var fileName = "temp_file"
                
                // Querying the ContentProvider for the file's metadata
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) {
                            // FAIL: [MASTG-TEST-XXXD] Blindly trusting the filename provided by an external app
                            fileName = it.getString(nameIndex)
                        }
                    }
                }

                // FAIL: [MASTG-TEST-XXXD] Writing to internal storage using the unsanitized filename
                val publicDir = File(filesDir, "public")
                val privateDir = File(filesDir, "private")
                val outFile = File(publicDir, fileName)
                
                try {
                    contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(outFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                showExploitationProof(fileName, publicDir, privateDir)
            }
        }
    }

    private fun showExploitationProof(fileName: String, publicDir: File, privateDir: File) {
        val privateFile = File(privateDir, "secret.txt")
        val privateContent = if (privateFile.exists()) privateFile.readText() else "File Not Found!"
        
        val publicFileList = publicDir.listFiles()
        val publicFilesInfo = if (publicFileList.isNullOrEmpty()) {
            "Empty"
        } else {
            publicFileList.joinToString("\n") { file ->
                "- ${file.name}: ${file.readText()}"
            }
        }

        val tv = TextView(this)
        tv.textSize = 18f
        tv.setPadding(32, 32, 32, 32)
        tv.text = """
            VULNERABILITY DEMONSTRATION
            ---------------------------
            Intent Result Filename: $fileName
            
            [ PUBLIC DIRECTORY ]
            Target Path: ${publicDir.name}/
            Files Found:
            $publicFilesInfo
            
            [ PRIVATE DIRECTORY ]
            Target Path: ${privateDir.name}/
            Content of secret.txt:
            $privateContent
            
            ---------------------------
            RESULT: ${if (privateContent != "Original Secret Content") "EXPLOITED!" else "SAFE"}
        """.trimIndent()
        setContentView(tv)
    }
}
