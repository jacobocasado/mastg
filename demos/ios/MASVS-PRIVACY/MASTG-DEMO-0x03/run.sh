#!/bin/bash
set -euo pipefail

# Extract the entitlements from the app's main binary (see @MASTG-TECH-0111).
# A pipeline/simulator build is pseudo-signed and has no embedded.mobileprovision,
# so the binary's code signature is the reliable source of entitlements.
rabin2 -OC MASTestApp > entitlements_reversed.plist

INPUT="${1:-entitlements_reversed.plist}"
OUTPUT="${2:-output.txt}"

# Apple entitlement catalog:
# https://developer.apple.com/documentation/bundleresources/entitlements
#
# Apple Xcode capabilities docs:
# https://developer.apple.com/documentation/xcode/capabilities
#
# Local discovery on a Mac with Xcode installed:
# find "$(xcode-select -p)/.." -type f \( -name "*.plist" -o -name "*.strings" -o -name "*.xcspec" \) -print0 \
#   | xargs -0 grep -hoE '([A-Za-z0-9_.-]+\.)?com\.apple\.[A-Za-z0-9_.-]+' \
#   | sort -u

if [[ ! -f "$INPUT" ]]; then
  echo "Missing file: $INPUT" >&2
  exit 1
fi

tmp_plist="$(mktemp)"
tmp_keys="$(mktemp)"
trap 'rm -f "$tmp_plist" "$tmp_keys"' EXIT

# rabin2 -OC may append a DER dump after the </plist> tag. Keep only the embedded
# plist so the grep below reports the real <key> entries and their file line numbers.
sed -n '1,/<\/plist>/p' "$INPUT" > "$tmp_plist"

cat > "$tmp_keys" <<'EOF'
aps-environment
com.apple.developer.applesignin
com.apple.developer.associated-domains
com.apple.developer.authentication-services.autofill-credential-provider
com.apple.developer.contacts.notes
com.apple.developer.default-data-protection
com.apple.developer.device-information.user-assigned-device-name
com.apple.developer.devicecheck.appattest-environment
com.apple.developer.family-controls
com.apple.developer.family-controls.app-and-website-usage
com.apple.developer.healthkit
com.apple.developer.healthkit.access
com.apple.developer.healthkit.background-delivery
com.apple.developer.homekit
com.apple.developer.icloud-container-environment
com.apple.developer.icloud-container-identifiers
com.apple.developer.icloud-services
com.apple.developer.in-app-payments
com.apple.developer.maps
com.apple.developer.networking.HotspotConfiguration
com.apple.developer.networking.HotspotHelper
com.apple.developer.networking.multicast
com.apple.developer.networking.networkextension
com.apple.developer.networking.vpn.api
com.apple.developer.networking.wifi-info
com.apple.developer.nfc.hce
com.apple.developer.nfc.readersession.formats
com.apple.developer.pass-type-identifiers
com.apple.developer.passkit.pass-presentation-suppression
com.apple.developer.payment-pass-provisioning
com.apple.developer.push-to-talk
com.apple.developer.sensitivecontentanalysis.client
com.apple.developer.shared-with-you
com.apple.developer.siri
com.apple.developer.usernotifications.communication
com.apple.developer.usernotifications.critical-alerts
com.apple.developer.usernotifications.time-sensitive
com.apple.developer.voip-push-notification
EOF

{
  echo "iOS privacy-relevant entitlement matches in: $INPUT"
  echo
  grep -nF -f "$tmp_keys" "$tmp_plist" || true
} > "$OUTPUT"

echo "Wrote $OUTPUT"
