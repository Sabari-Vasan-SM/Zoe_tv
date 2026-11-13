# Zoe TV Launcher 📺

A modern, minimal, premium Android TV & Google TV Home Launcher engineered with **Kotlin** and **Jetpack Compose**. Designed specifically for 10-foot television experiences with clean typography, fluid D-pad navigation, and zero visual clutter.

---

## 🌟 Key Features

### 1. Minimalist System-UI Aesthetic
- **Atmospheric Theme Engine**: Live switchable presets including *Deep Obsidian*, *Midnight Nebula*, *Aurora Borealis*, *Sunset Ember*, *Emerald Forest*, and *Studio Light*.
- **Subtle Glow & Focus Transitions**: Fluid 1.1x scale animations and glowing accent borders designed specifically for 1080p, 4K, and 8K displays.
- **Zero Distraction**: No intrusive ads, promotional carousels, or unneeded widgets.

### 2. Main Home Screen Architecture
- **Top Status Section**:
  - Live digital clock with crisp tabular figures (supports both 12-hour AM/PM and 24-hour formats).
  - Day and date subtitle.
  - Real-time Wi-Fi status with signal indicator & connected SSID.
  - Ethernet connectivity detection.
  - Battery percentage indicator (only displayed on battery-equipped devices).
  - Quick App Refresh and Launcher Settings buttons.
- **Quick Access System Bar**:
  - Fast shortcuts directly opening Android system settings:
    - System Settings
    - Network / Wi-Fi Settings
    - Bluetooth Devices
    - Display & Sound
    - File Manager
    - Launcher Appearance & Hidden Apps
- **Favorites / Pinned Applications Row**:
  - Prominent horizontal row for your most-used TV apps.
  - Pinned star indicator badge.
  - Quick empty-state guidance for first-time setup.
- **All Applications Section**:
  - Automatically queries all installed apps (`CATEGORY_LEANBACK_LAUNCHER` & `CATEGORY_LAUNCHER`).
  - High-performance asynchronous icon caching for 60/120 fps fluid scrolling.
  - Real-time package monitoring (`ACTION_PACKAGE_ADDED`, `ACTION_PACKAGE_REMOVED`, `ACTION_PACKAGE_CHANGED`) to auto-refresh without restarting the launcher.

### 3. Remote Control Navigation (10-Foot UI Experience)
- **100% Remote-Driven**: Completely navigatable without a touch screen or mouse pointer.
- **D-Pad Directional Controls**: Up, Down, Left, Right navigate smoothly between pills, cards, and sections.
- **OK / Center Click**: Instantly launches the selected application.
- **Menu Key or Long-Press Center**: Opens the contextual App Actions modal:
  - Open application
  - Pin to Favorites / Remove from Favorites
  - Hide application from drawer
  - Open App Info & permissions
  - Uninstall application
- **Back Button Management**: Smart back button interceptor that dismisses active modals or safely maintains the launcher home screen without accidentally quitting.

---

## 📦 Installable APK Deliverables

The installable APK has been built and verified:
- **Location**: `release/ZoeTVLauncher-v1.0.0.apk`
- **Debug Package**: `app/build/outputs/apk/debug/app-debug.apk`

### Installation via ADB
Connect to your Android TV / Google TV over USB or Wi-Fi:

```bash
# 1. Connect to your TV (if using wireless ADB)
adb connect <YOUR_TV_IP_ADDRESS>:5555

# 2. Install the launcher APK
adb install -r release/ZoeTVLauncher-v1.0.0.apk

# 3. Press the Home button on your TV remote and choose 'Zoe TV Launcher' as 'Always'
```

### Installation via USB Flash Drive
1. Copy `ZoeTVLauncher-v1.0.0.apk` to a USB drive.
2. Insert the USB drive into your Android TV box or smart TV.
3. Open any file manager on your TV (e.g. FX, AnExplorer, or X-plore).
4. Select `ZoeTVLauncher-v1.0.0.apk` and click **Install**.

---

## 🛠️ Setting as Default Launcher

1. Press the **HOME** button on your TV remote.
2. When prompted *"Select a Home app"*, choose **Zoe TV Launcher** and select **Always**.
3. If your Android TV device requires manual launcher selection:
   - Navigate to **TV Settings** > **Apps** > **Special App Access** > **Default Home App**.
   - Select **Zoe TV Launcher**.

---

## 🏗️ Project Architecture

```
Zoe_tv/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/zoe/tvlauncher/
│       │   ├── ZoeTvApplication.kt
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   ├── AppInfo.kt
│       │   │   │   └── WallpaperTheme.kt
│       │   │   ├── preferences/
│       │   │   │   └── LauncherPreferences.kt
│       │   │   └── manager/
│       │   │       ├── AppsManager.kt
│       │   │       └── SystemStatusMonitor.kt
│       │   ├── ui/
│       │   │   ├── components/
│       │   │   │   ├── AllAppsSection.kt
│       │   │   │   ├── AppCard.kt
│       │   │   │   ├── FavoritesRow.kt
│       │   │   │   ├── QuickAccessBar.kt
│       │   │   │   ├── TopStatusBar.kt
│       │   │   │   └── TvFocusModifier.kt
│       │   │   ├── dialogs/
│       │   │   │   ├── AppActionsDialog.kt
│       │   │   │   └── SettingsDialog.kt
│       │   │   ├── screens/
│       │   │   │   └── HomeScreen.kt
│       │   │   └── theme/
│       │   │       ├── Color.kt
│       │   │       ├── Theme.kt
│       │   │       └── Type.kt
│       │   └── viewmodel/
│       │       └── LauncherViewModel.kt
│       └── res/
│           ├── drawable/
│           │   ├── tv_banner.xml
│           │   ├── ic_launcher_background.xml
│           │   └── ic_launcher_foreground.xml
│           ├── mipmap-anydpi-v26/
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               └── styles.xml
├── release/
│   └── ZoeTVLauncher-v1.0.0.apk
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradlew
```

---

## 🔧 Building from Source

To compile the project yourself:

```bash
./gradlew assembleDebug
# or for release:
./gradlew assembleRelease
```
