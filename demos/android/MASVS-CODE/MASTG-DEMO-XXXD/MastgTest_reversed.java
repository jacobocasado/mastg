package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MastgTest {
    private final Context context;

    public MastgTest(Context context) {
        this.context = context;
    }

    public String mastgTest() {
        DemoResults r = new DemoResults("XXXD");
        try {
            Intent intent = new Intent(this.context, VulnerableActivity.class);
            intent.addFlags(268435456);
            this.context.startActivity(intent);
            r.add(Status.FAIL, "Launched VulnerableActivity to demonstrate intent response handling");
        } catch (Exception e) {
            r.add(Status.ERROR, e.toString());
        }
        return r.toJson();
    }
}

class VulnerableActivity extends Activity {
    
    private static final int REQUEST_CODE_GET_CONTENT = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        File publicDir = new File(getFilesDir(), "public");
        publicDir.mkdirs();
        File privateDir = new File(getFilesDir(), "private");
        privateDir.mkdirs();
        
        try {
            FileOutputStream fos = new FileOutputStream(new File(privateDir, "secret.txt"));
            fos.write("Original Secret Content".getBytes());
            fos.close();
        } catch (Exception e) {}

        Intent intent = new Intent("org.owasp.mastestapp.REQUEST_FILE");
        startActivityForResult(intent, REQUEST_CODE_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_CONTENT && resultCode == -1 && data != null && (uri = data.getData()) != null) {
            String fileName = "temp_file";
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex("_display_name");
                        if (nameIndex >= 0) {
                            fileName = cursor.getString(nameIndex);
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
            try {
                InputStream input = getContentResolver().openInputStream(uri);
                if (input != null) {
                    try {
                        File publicDir = new File(getFilesDir(), "public");
                        FileOutputStream output = new FileOutputStream(new File(publicDir, fileName));
                        try {
                            byte[] buffer = new byte[8192];
                            while (true) {
                                int read = input.read(buffer);
                                if (read <= 0) {
                                    break;
                                }
                                output.write(buffer, 0, read);
                            }
                        } finally {
                            output.close();
                        }
                    } finally {
                        input.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            showExploitationProof(fileName, publicDir, privateDir);
        }
    }

    private void showExploitationProof(String fileName, File publicDir, File privateDir) {
        android.widget.TextView tv = new android.widget.TextView(this);
        tv.setTextSize(18);
        tv.setPadding(32, 32, 32, 32);
        
        StringBuilder proof = new StringBuilder();
        proof.append("VULNERABILITY DEMONSTRATION\n");
        proof.append("---------------------------\n");
        proof.append("Intent Result Filename: ").append(fileName).append("\n\n");
        
        proof.append("[ PUBLIC DIRECTORY ]\n");
        proof.append("Target Path: public/\n");
        proof.append("Files Found:\n");
        File[] publicFiles = publicDir.listFiles();
        if (publicFiles != null && publicFiles.length > 0) {
            for (File f : publicFiles) {
                proof.append("- ").append(f.getName()).append(": ");
                try {
                    java.util.Scanner s = new java.util.Scanner(f).useDelimiter("\\A");
                    proof.append(s.hasNext() ? s.next() : "Empty").append("\n");
                } catch (Exception e) { proof.append("Error reading!\n"); }
            }
        } else {
            proof.append("Empty\n");
        }
        
        proof.append("\n[ PRIVATE DIRECTORY ]\n");
        proof.append("Target Path: private/\n");
        proof.append("Content of secret.txt:\n");
        File privateFile = new File(privateDir, "secret.txt");
        try {
            java.util.Scanner s = new java.util.Scanner(privateFile).useDelimiter("\\A");
            String content = s.hasNext() ? s.next() : "Empty";
            proof.append(content).append("\n");
            
            proof.append("\n---------------------------\n");
            proof.append("RESULT: ").append(content.equals("Original Secret Content") ? "SAFE" : "EXPLOITED!");
        } catch (Exception e) {
            proof.append("File Deleted or Error!\n");
            proof.append("\n---------------------------\n");
            proof.append("RESULT: EXPLOITED!");
        }
        
        tv.setText(proof.toString());
        setContentView(tv);
    }
}
