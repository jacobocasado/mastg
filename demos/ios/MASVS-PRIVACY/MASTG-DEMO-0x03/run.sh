#!/bin/bash
set -euo pipefail

APP_BINARY="${1:-../MASTG-DEMO-0x01/MASTestApp}"
INPUT="${2:-entitlements_reversed.plist}"
OUTPUT="${3:-output.txt}"

# Extract the entitlements from the app's main binary (see @MASTG-TECH-0111).
rabin2 -OC "$APP_BINARY" > "$INPUT"

if [[ ! -f "$INPUT" ]]; then
  echo "Missing file: $INPUT" >&2
  exit 1
fi

sed -n '1,/<\/plist>/p' "$INPUT" > "$OUTPUT"

echo "Wrote $OUTPUT"
