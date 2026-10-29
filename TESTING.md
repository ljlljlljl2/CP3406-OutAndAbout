# Manual verification - record actual results

These checks have not been executed in the assistant environment.

| Check | Expected result | Actual result |
| --- | --- | --- |
| Gradle sync and assembleDebug | No build errors | To record |
| testDebugUnitTest | All unit tests pass | To record |
| lintDebug | Review and resolve material findings | To record |
| First launch online | Singapore weather replaces loading state | To record |
| Select Cairns, return to Weather | Cairns data and local weather time shown | To record |
| Switch Celsius/Fahrenheit | Temperature and feels-like convert; wind stays km/h | To record |
| Switch cities rapidly | Only final city's data displayed | To record |
| Rotate while viewing settings/weather | Selection and unit retained; layout usable | To record |
| Enable airplane mode and refresh | Error and Retry; existing data marked potentially outdated | To record |
| Change city while offline | Old city's data not shown under new city | To record |
| Restore internet, Retry | New data shown, error removed | To record |
| Small screen / large font / dark mode | Scrollable, legible, controls accessible | To record |
| Attribution link | Opens Open-Meteo | To record |

Capture screenshots of Weather, Settings and the offline error state. Compare a displayed weather reading with an API response if possible. Record device/API version, date, result and any fixes. Do not mark a check passed without running it.
