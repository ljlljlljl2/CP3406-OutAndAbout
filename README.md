# Out & About - Weather Utility

An extension of the CP3406/CP5307 MobileTech Utility App starter. Helps users check current conditions before leaving home, with minimal interaction and two tabs.

## Repository
https://github.com/ljlljlljl2/CP3406-OutAndAbout

## Features
- Weather tab: temperature, condition, feels-like temperature, wind, humidity, weather timestamp and a condition-based outing tip.
- Settings tab: Singapore, Townsville or Cairns; Celsius/Fahrenheit switch.
- City changes automatically fetch new data; temperature unit changes convert locally.
- Loading indicator, retry on failure, manual refresh and stale-data notice.
- Scrollable Compose layouts and Material 3 theme, including system dark mode.
- No API key, location permission or personal data required.

## Run
1. Extract the ZIP into a new folder (keep your starter separately).
2. Open the folder containing settings.gradle.kts in Android Studio.
3. Use JetBrains JDK 21 for Gradle, matching gradle/gradle-daemon-jvm.properties (toolchainVersion=21). Android Studio's bundled JetBrains Runtime can be used if its Java version is 21. Install Android SDK 36 and let Gradle sync finish. Internet access is needed for dependencies and any required JDK download.
4. Select an API 26+ emulator or Android device with internet and click Run.

From the project root: `./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` (Windows: `gradlew.bat`).

## Implementation
- MainActivity.kt: starter Scaffold and bottom navigation, modular UtilityScreen, SettingsScreen and MetricCard composables. Tab saved with rememberSaveable.
- WeatherViewModel.kt: immutable StateFlow UI state; viewModelScope networking; cancellation of obsolete requests; preferences survive activity recreation via ViewModel.
- WeatherData.kt: Retrofit API, Gson response mapping, WeatherRepository abstraction, NetworkWeatherRepository, and lazy AppContainer. Constructor injection and a ViewModel factory provide manual dependency injection; no Hilt is required.
- collectAsStateWithLifecycle collects only while lifecycle is active.
- Network calls are suspend functions with timeouts and error handling. Cancellation is rethrown.
- Preferences are intentionally not persisted across process restarts, as allowed by the brief.

## API and attribution
Weather data by [Open-Meteo](https://open-meteo.com/), [API documentation](https://open-meteo.com/en/docs), under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/). Attribution is also linked in the UI. Data is weather-model output, not a device observation.

GET https://api.open-meteo.com/v1/forecast with city coordinates, timezone=auto and current=temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m. Defaults provide Celsius and km/h. Fahrenheit is a local conversion. WMO codes are grouped into readable conditions. Tips are deterministic rules, not a forecast or official alert service.

## Verification status
The app was built, installed and run on a Medium Phone emulator running Android 16 / API 36 on 29 October 2026.

Verified behaviour:
- Singapore weather loaded successfully.
- Selecting Cairns updated the weather screen.
- Switching to Fahrenheit updated both temperature and feels-like readings; wind remained in km/h.
- The testDebugUnitTest and lintDebug tasks completed with BUILD SUCCESSFUL.

The detailed Lint report has not yet been reviewed. Offline recovery, rotation, rapid city switching, accessibility and the attribution link still require manual verification. See TESTING.md for the testing record.

## Scope and limitations
Three predefined cities; no search or GPS. No background refresh, persistence or offline disk cache. A refresh failure can retain previous same-city data with a warning. Switching cities clears old data. Current conditions cannot predict rain later in the day. Check local official alerts for travel decisions.

## Academic acknowledgement
This project extends the lecturer-provided MobileTech Utility App starter. OpenAI ChatGPT assisted with troubleshooting and documentation. I configured and ran the project, checked the documented functionality, executed the test and Lint tasks, and uploaded the project.
