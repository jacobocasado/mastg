#!/bin/bash
python3 - <<'PY' > output.txt
import plistlib

with open("entitlements.plist", "rb") as f:
    data = plistlib.load(f)

for key in [
    "com.apple.developer.associated-domains",
    "com.apple.developer.healthkit",
    "com.apple.security.application-groups",
]:
    print(f"{key}: {data.get(key)!r}")
PY
