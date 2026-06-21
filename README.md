# TimerWebView

Android WebView application with auto-refresh functionality and persistent configuration.

## Features

- **WebView Loading**: Load HTTP/HTTPS URLs with JavaScript support
- **Auto Refresh**: Configurable auto-refresh interval (0-120 minutes)
- **Persistent Settings**: All configurations saved locally using SharedPreferences
- **Network Aware**: Pause refresh when offline, auto-resume when network restored
- **Background Aware**: Pause timer when app goes to background

## Technical Specifications

| Item | Details |
|------|---------|
| **Package** | com.github.darrenhost.timerwebview |
| **Min SDK** | Android 10 (API 29) |
| **Target SDK** | Android 34 |
| **Core Tech** | Native WebView + SharedPreferences + Handler |

## Project Structure

```
TimerWebView/
├── app/
│   ├── src/main/
│   │   ├── java/com/github/darrenhost/timerwebview/
│   │   │   ├── MainActivity.java         # Main WebView page
│   │   │   ├── SettingsActivity.java     # Settings page
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
│   │   │       └── ic_launcher_foreground.xml
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
gradle assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Configuration

All settings are saved locally and persist across app restarts:

| Setting | Default | Range |
|---------|---------|-------|
| URL | (empty) | http/https only |
| Refresh Interval | 5 min | 0-120 min (0 = disabled) |
| JavaScript | Enabled | On/Off |
| Cache | Enabled | On/Off |

## Core Logic

1. **Startup**: Read local configuration
2. **URL Check**: If empty, prompt to enter settings
3. **Auto Refresh**: Polling in foreground, pause in background
4. **Network Handle**: Stop refresh on error, auto-resume on network restore
5. **Config Update**: Reload URL and reset timer when settings change

## License

MIT License
