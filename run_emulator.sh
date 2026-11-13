#!/bin/bash
set -e

export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

echo "=========================================="
echo "  Starting Android TV Emulator & Zoe TV   "
echo "=========================================="

# Check if emulator is already running
if adb devices | grep -q "emulator"; then
    echo "✔ Found running emulator."
else
    echo "Starting Television_1080p emulator..."
    nohup "$ANDROID_HOME/emulator/emulator" -avd Television_1080p >/dev/null 2>&1 &
    
    echo "Waiting for emulator to boot up (this may take 15-30s)..."
    adb wait-for-device
    while [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
        sleep 2
    done
    echo "✔ Emulator booted successfully!"
fi

echo "Installing Zoe TV Launcher..."
adb install -r release/ZoeTVLauncher-v1.0.0.apk

echo "Launching Zoe TV Launcher..."
adb shell am start -n com.zoe.tvlauncher.debug/com.zoe.tvlauncher.MainActivity

echo ""
echo "✔ Done! Use your Arrow Keys (D-pad) and Enter (OK) to navigate."
