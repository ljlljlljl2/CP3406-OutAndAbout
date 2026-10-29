# Out & About - Weather Utility

An extension of the CP3406/CP5307 MobileTech Utility App starter. Helps users check current conditions before leaving home, with minimal interaction and two tabs.

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
3. Use JDK 17 for Gradle, install Android SDK 36, and let Gradle sync finish. Internet access is needed for dependencies.
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
Build and unit test execution was attempted in the assistant environment but stopped before compilation because services.gradle.org was unreachable. No successful build, emulator run, or live API response has been verified here. Run the commands above and follow TESTING.md on your machine before submission. Added tests cover unit conversion, tip priority, loading/error/retry state, city changes and obsolete request cancellation.

## Scope and limitations
Three predefined cities; no search or GPS. No background refresh, persistence or offline disk cache. A refresh failure can retain previous same-city data with a warning. Switching cities clears old data. Current conditions cannot predict rain later in the day. Check local official alerts for travel decisions.

## Academic acknowledgement
Based on the lecturer-provided MobileTech Utility App starter. ChatGPT helped implement the weather feature, architecture, tests and documentation. Read and verify all code, record your own testing and changes, and complete the required AI declaration according to course rules. Do not present the reflection prompts as personal experience or invent past Git commits.
