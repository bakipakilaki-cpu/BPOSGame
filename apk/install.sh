#!/usr/bin/env bash
#
# Installs the newest debug APK from this folder onto a USB-connected phone.
#   ./apk/install.sh                       -> installs BPOSGame-debug-latest.apk
#   ./apk/install.sh path/to/other.apk     -> installs that file instead
#
set -euo pipefail

OUT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APK="${1:-$OUT_DIR/BPOSGame-debug-latest.apk}"

ADB="$(command -v adb || echo "${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools/adb")"
[ -x "$ADB" ] || { echo "❌ adb not found. Install Android platform-tools."; exit 1; }
[ -f "$APK" ] || { echo "❌ $APK not found. Run ./apk/build-apk.sh first."; exit 1; }

echo "🔌 Waiting for a device (enable USB debugging and accept the prompt on the phone)..."
"$ADB" wait-for-device
"$ADB" devices

echo "📲 Installing $(basename "$APK")..."
"$ADB" install -r -g "$APK"

echo "🚀 Launching BraiPay..."
"$ADB" shell monkey -p com.aistudio.braipay.qxzrtv -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1 || true
echo "✅ Done."
