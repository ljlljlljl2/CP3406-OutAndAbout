package au.edu.jcu.assessment.utilityapp

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

enum class City(val label: String, val latitude: Double, val longitude: Double) {
    SINGAPORE("Singapore", 1.3521, 103.8198),
    TOWNSVILLE("Townsville", -19.259, 146.8169),
    CAIRNS("Cairns", -16.9186, 145.7781)
}

data class WeatherResponse(val current: CurrentWeather?)
data class CurrentWeather(
    val time: String,
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("apparent_temperature") val feelsLike: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("weather_code") val code: Int,
    @SerializedName("wind_speed_10m") val wind: Double
)

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

interface WeatherRepository {
    suspend fun fetch(city: City): CurrentWeather
}

class NetworkWeatherRepository(private val api: WeatherApi) : WeatherRepository {
    override suspend fun fetch(city: City): CurrentWeather =
        requireNotNull(api.forecast(city.latitude, city.longitude).current) {
            "Weather service returned no current data"
        }
}

// A small manual DI container keeps networking out of the UI and permits fake repositories.
object AppContainer {
    val repository: WeatherRepository by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(25, TimeUnit.SECONDS)
            .build()
        val api = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(WeatherApi::class.java)
        NetworkWeatherRepository(api)
    }
}

fun temperatureText(celsius: Double, fahrenheit: Boolean): String {
    val value = if (fahrenheit) celsius * 9 / 5 + 32 else celsius
    return java.lang.String.format(java.util.Locale.US, "%.0f°%s", value, if (fahrenheit) "F" else "C")
}

fun conditionText(code: Int): String = when (code) {
    0 -> "Clear sky"
    1, 2 -> "Partly cloudy"
    3 -> "Overcast"
    45, 48 -> "Fog"
    51, 53, 55, 56, 57 -> "Drizzle"
    61, 63, 65, 66, 67 -> "Rain"
    71, 73, 75, 77, 85, 86 -> "Snow"
    80, 81, 82 -> "Rain showers"
    95, 96, 99 -> "Thunderstorms"
    else -> "Conditions unavailable"
}

fun outingTip(weather: CurrentWeather): String = when (weather.code) {
    95, 96, 99 -> "Thunderstorms: postpone outdoor plans and check local weather alerts."
    51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> "Take an umbrella and allow extra travel time."
    71, 73, 75, 77, 85, 86 -> "Dress warmly and check local travel conditions."
    45, 48 -> "Visibility may be low. Take extra care when travelling."
    else -> when {
        weather.wind >= 35 -> "Windy conditions: take care on exposed routes."
        weather.feelsLike >= 32 -> "Feels hot: carry water and plan shaded breaks."
        weather.feelsLike <= 10 -> "Take a warm layer for your trip."
        else -> "Check the conditions before heading out."
    }
}
