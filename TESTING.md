# Testing record

Date: 29 October 2026
Device: Medium Phone emulator, Android 16 / API 36

## Completed checks

| Check | Actual result |
| --- | --- |
| Build and installation | App built, installed and launched successfully. |
| First launch online | Singapore weather loaded with temperature, condition, wind and humidity. |
| City selection | Selecting Cairns updated the main screen to Cairns weather. |
| Temperature units | Switching to Fahrenheit displayed 78°F and feels-like 83°F. Wind remained in km/h. |
| Unit tests | testDebugUnitTest completed successfully. |
| Android Lint | lintDebug completed successfully and generated an HTML report. Detailed findings have not yet been reviewed. |

## Command used

JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" bash gradlew :app:testDebugUnitTest :app:lintDebug

Result: BUILD SUCCESSFUL in 46s.

## Environment issue resolved

The first terminal attempt could not locate Java. Setting JAVA_HOME to Android Studio's bundled Java runtime allowed the command to run successfully.

## Checks still pending

- Rapid city switching.
- Rotation and preference retention.
- Offline refresh, error display and retry.
- City switching while offline.
- Recovery after restoring the connection.
- Large font, small screen and dark mode.
- Open-Meteo attribution link.
- Detailed review of the generated Lint report.

Successful checks above do not establish that all app behaviour has been tested.
