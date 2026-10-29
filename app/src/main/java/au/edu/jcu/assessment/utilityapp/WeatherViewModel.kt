package au.edu.jcu.assessment.utilityapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val city: City = City.SINGAPORE,
    val fahrenheit: Boolean = false,
    val loading: Boolean = true,
    val weather: CurrentWeather? = null,
    val error: String? = null
)

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(WeatherUiState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null

    init { refresh() }

    fun selectCity(city: City) {
        if (city == mutableState.value.city) return
        mutableState.update { it.copy(city = city, weather = null) }
        refresh()
    }

    fun setFahrenheit(enabled: Boolean) {
        mutableState.update { it.copy(fahrenheit = enabled) }
    }

    fun refresh() {
        request?.cancel()
        val city = mutableState.value.city
        mutableState.update { it.copy(loading = true, error = null) }
        request = viewModelScope.launch {
            try {
                val weather = repository.fetch(city)
                mutableState.update { it.copy(loading = false, weather = weather) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                mutableState.update {
                    it.copy(loading = false, error = "Could not update weather. Check your connection and try again.")
                }
            }
        }
    }

    companion object {
        fun factory(repository: WeatherRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(WeatherViewModel::class.java))
                    return WeatherViewModel(repository) as T
                }
            }
    }
}
