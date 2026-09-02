#!/usr/bin/env bash
#
# Builds the BPOSGame APK and drops it in this folder.
#
#   ./apk/build-apk.sh           -> debug APK   (signed with debug.keystore, installable right away)
#   ./apk/build-apk.sh release   -> release APK (needs KEYSTORE_PATH, STORE_PASSWORD, KEY_PASSWORD)
#
set -euo pipefail

VARIANT="${1:-debug}"
if [ $# -gt 0 ]; then shift; fi   # any remaining args are passed straight to Gradle
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="$ROOT/apk"
cd "$ROOT"

# --- Pick a JDK the Android Gradle Plugin supports (17-24; it rejects newer ones) ---
if [ -z "${JAVA_HOME:-}" ] || ! "$JAVA_HOME/bin/java" -version >/dev/null 2>&1; then
  for candidate in 21 17 23 22 20 19 18; do
    if home="$(/usr/libexec/java_home -v "$candidate" 2>/dev/null)"; then
      export JAVA_HOME="$home"
      break
    fi
  done
fi
[ -n "${JAVA_HOME:-}" ] || { echo "❌ No JDK found. Install one: brew install --cask temurin@21"; exit 1; }
echo "☕ JAVA_HOME=$JAVA_HOME"

# --- Android SDK location ---
if [ ! -f "$ROOT/local.properties" ]; then
  SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
  [ -d "$SDK" ] || { echo "❌ Android SDK not found. Install it, then set ANDROID_HOME."; exit 1; }
  echo "sdk.dir=$SDK" > "$ROOT/local.properties"
  echo "📝 Wrote local.properties -> $SDK"
fi

# --- Debug signing key (generated once, never committed) ---
if [ "$VARIANT" = "debug" ] && [ ! -f "$ROOT/debug.keystore" ]; then
  echo "🔑 Generating debug.keystore..."
  "$JAVA_HOME/bin/keytool" -genkeypair -v -keystore "$ROOT/debug.keystore" \
    -storepass android -alias androiddebugkey -keypass android \
    -keyalg RSA -keysize 2048 -validity 10950 \
    -dname "CN=Android Debug,O=Android,C=US" >/dev/null
fi

if [ "$VARIANT" = "release" ]; then
  : "${STORE_PASSWORD:?Set STORE_PASSWORD for a release build}"
  : "${KEY_PASSWORD:?Set KEY_PASSWORD for a release build}"
  TASK="assembleRelease"
  BUILT="app/build/outputs/apk/release/app-release.apk"
else
  TASK="assembleDebug"
  BUILT="app/build/outputs/apk/debug/app-debug.apk"
fi

echo "🔨 ./gradlew $TASK"
./gradlew "$TASK" --stacktrace "$@"

[ -f "$BUILT" ] || { echo "❌ Build finished but $BUILT is missing."; exit 1; }

STAMP="$(date +%Y%m%d-%H%M)"
DEST="$OUT_DIR/BPOSGame-$VARIANT-$STAMP.apk"
LATEST="$OUT_DIR/BPOSGame-$VARIANT-latest.apk"
cp "$BUILT" "$DEST"
cp "$BUILT" "$LATEST"

echo
echo "✅ APK ready:"
echo "   $DEST"
echo "   $LATEST  (always points at the newest build)"
echo "   size: $(du -h "$DEST" | cut -f1)"
echo
echo "📲 Install on a phone plugged in over USB:"
echo "   ./apk/install.sh"
echo "   (or copy the .apk to the phone and open it)"
