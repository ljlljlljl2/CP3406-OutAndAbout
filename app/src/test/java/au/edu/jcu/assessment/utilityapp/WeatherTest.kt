package au.edu.jcu.assessment.utilityapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherTest {
    private val sample = CurrentWeather("2026-10-29T20:00", 29.0, 34.0, 78, 2, 10.5)

    @Test fun convertsTemperature() {
        assertEquals("32°F", temperatureText(0.0, true))
        assertEquals("100°C", temperatureText(100.0, false))
        assertEquals("212°F", temperatureText(100.0, true))
    }

    @Test fun prioritisesThunderstormsOverHeat() {
        assertTrue(outingTip(sample.copy(code = 95)).startsWith("Thunderstorms"))
        assertTrue(outingTip(sample.copy(code = 61)).contains("umbrella"))
        assertTrue(outingTip(sample).contains("water"))
        assertEquals("Conditions unavailable", conditionText(-1))
    }

    @Test fun settingsAndRetryUpdateState() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var fail = false
            val requested = mutableListOf<City>()
            val repository = object : WeatherRepository {
                override suspend fun fetch(city: City): CurrentWeather {
                    requested.add(city)
                    if (fail) throw java.io.IOException("offline")
                    return sample
                }
            }
            val model = WeatherViewModel(repository)
            advanceUntilIdle()
            assertFalse(model.state.value.loading)
            assertEquals(sample, model.state.value.weather)
            model.setFahrenheit(true)
            assertTrue(model.state.value.fahrenheit)
            assertEquals(1, requested.size) // Unit changes need no network call.
            fail = true
            model.selectCity(City.CAIRNS)
            advanceUntilIdle()
            assertNull(model.state.value.weather) // Never label old-city data as the new city.
            assertNotNull(model.state.value.error)
            fail = false
            model.refresh()
            advanceUntilIdle()
            assertNull(model.state.value.error)
            assertEquals(sample, model.state.value.weather)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test fun switchingCityCancelsOldRequest() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = object : WeatherRepository {
                override suspend fun fetch(city: City): CurrentWeather {
                    if (city == City.SINGAPORE) kotlinx.coroutines.delay(1000)
                    return sample.copy(temperature = if (city == City.CAIRNS) 25.0 else 29.0)
                }
            }
            val model = WeatherViewModel(repository)
            runCurrent()
            model.selectCity(City.CAIRNS)
            advanceUntilIdle()
            assertEquals(City.CAIRNS, model.state.value.city)
            assertEquals(25.0, model.state.value.weather!!.temperature, 0.001)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
