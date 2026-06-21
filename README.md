# TimerWebView

Android WebView application with auto-refresh functionality and persistent configuration.

## 📥 Download APK

| Version | Release Date | Download | Size |
|---------|--------------|----------|------|
| **v1.2** | 2026-06-21 | [📱 TimerWebView-v1.2-release.apk](https://github.com/DarrenHost/TimerWebView/releases/download/v1.2/TimerWebView-v1.2-release.apk) | 9.2 MB |

**Direct Download Links:**
- **GitHub Release**: https://github.com/DarrenHost/TimerWebView/releases/download/v1.2/TimerWebView-v1.2-release.apk
- **GitHub Assets**: https://github.com/DarrenHost/TimerWebView/blob/master/Assets/TimerWebView-v1.1-release.apk
- **NAS (Local)**: `/data/gswy/00_临时文件/TimerWebView-v1.0-release.apk`

> **Note**: APK is signed and ready for installation. Minimum Android 10 (API 29) required.

## Features

- **WebView Loading**: Load HTTP/HTTPS URLs with JavaScript support
- **Auto Refresh**: Configurable auto-refresh interval (0-7200 seconds)
- **Fullscreen Mode**: Hide status bar, navigation bar, and action bar
- **Tap to Exit**: Tap WebView 5 times to exit fullscreen
- **Persistent Settings**: All configurations saved locally using SharedPreferences
- **Network Aware**: Pause refresh when offline, auto-resume when network restored
- **Background Aware**: Pause timer when app goes to background
- **Version Display**: Show app version in settings page

## Technical Specifications

| Item | Details |
|------|---------|
| **Package** | com.github.darrenhost.timerwebview |
| **Min SDK** | Android 10 (API 29) |
| **Target SDK** | Android 34 |
| **Core Tech** | Native WebView + SharedPreferences + Handler |
| **Signature** | Signed with timerwebview.keystore |

## Project Structure

```
TimerWebView/
├── Assets/                              # APK output directory
│   └── TimerWebView-v1.1-release.apk
├── app/
│   ├── src/main/
│   │   ├── java/com/github/darrenhost/timerwebview/
│   │   │   ├── MainActivity.java         # Main WebView page + Fullscreen
│   │   │   ├── SettingsActivity.java     # Settings page + Version display
│   │   │   └── ConfigManager.java        # Configuration management
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   └── activity_settings.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   ├── menu/
│   │   │   │   └── main_menu.xml
│   │   │   └── drawable/
│   │   │       └── ic_launcher_foreground.xml  # TW logo
│   │   ├── AndroidManifest.xml
│   │   └── build.gradle
│   └── proguard-rules.pro
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Build

```bash
cd TimerWebView

# Build Debug
gradle assembleDebug

# Build Release (signed)
gradle assembleRelease

# Build and copy to NAS
gradle assembleRelease copyToGswy
```

APK output:
- **Debug**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release**: `app/build/outputs/apk/release/app-release.apk`
- **Assets**: `Assets/TimerWebView-v{version}-release.apk`
- **NAS**: `/data/gswy/00_临时文件/TimerWebView-v{version}-release.apk`

## Configuration

All settings are saved locally and persist across app restarts:

| Setting | Default | Range |
|---------|---------|-------|
| URL | (empty) | http/https only |
| Refresh Interval | 30 seconds | 0-7200 seconds (0 = disabled) |
| JavaScript | Enabled | On/Off |
| Cache | Enabled | On/Off |

## Core Logic

1. **Startup**: Read local configuration
2. **URL Check**: If empty, prompt to enter settings
3. **Auto Refresh**: Polling in foreground (seconds), pause in background
4. **Network Handle**: Stop refresh on error, auto-resume on network restore
5. **Config Update**: Reload URL and reset timer when settings change
6. **Fullscreen**: Hide ActionBar + Status Bar + Navigation Bar
7. **Exit Fullscreen**: Tap WebView 5 times or use menu button

## Change Log

### v1.2 (2026-06-21)
- ✅ New logo design (TW letters with blue circle)
- ✅ White menu button icons
- ✅ Fullscreen hides ActionBar/TitleBar completely

### v1.1 (2026-06-21)
- ✅ Fullscreen mode with menu button
- ✅ Tap 5 times to exit fullscreen
- ✅ Display app version in settings
- ✅ Refresh interval changed to seconds

### v1.0 (2026-06-21)
- ✅ Initial release
- ✅ Auto-refresh WebView
- ✅ Persistent settings

## License

MIT License
