package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTech_UtilityAppTheme {
                val model: WeatherViewModel = viewModel(factory = WeatherViewModel.factory(AppContainer.repository))
                val state by model.state.collectAsStateWithLifecycle()
                UtilityApp(state, model::refresh, model::selectCity, model::setFahrenheit)
            }
        }
    }
}

@Composable
fun UtilityApp(
    state: WeatherUiState,
    onRefresh: () -> Unit,
    onCity: (City) -> Unit,
    onFahrenheit: (Boolean) -> Unit
) {
    var settings by rememberSaveable { mutableStateOf(false) }
    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(selected = !settings, onClick = { settings = false },
                icon = { Icon(Icons.Default.Home, contentDescription = null) }, label = { Text("Weather") })
            NavigationBarItem(selected = settings, onClick = { settings = true },
                icon = { Icon(Icons.Default.Settings, contentDescription = null) }, label = { Text("Settings") })
        }
    }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (settings) SettingsScreen(state, onCity, onFahrenheit)
            else UtilityScreen(state, onRefresh)
        }
    }
}

@Composable
fun UtilityScreen(state: WeatherUiState, onRefresh: () -> Unit) {
    val uri = LocalUriHandler.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("OUT & ABOUT", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("Your weather,\nat a glance.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(state.city.label, style = MaterialTheme.typography.titleLarge)
        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text("Updating weather…", style = MaterialTheme.typography.bodyMedium)
        }
        state.error?.let { message ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(message)
                    if (state.weather != null) Text("Previous data below may be out of date.")
                    Button(onClick = onRefresh, enabled = !state.loading) { Text("Retry") }
                }
            }
        }
        state.weather?.let { weather ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(temperatureText(weather.temperature, state.fahrenheit),
                        style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                    Text(conditionText(weather.code), style = MaterialTheme.typography.headlineSmall)
                    Text("Feels like " + temperatureText(weather.feelsLike, state.fahrenheit))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Wind", "${weather.wind} km/h", Modifier.weight(1f))
                MetricCard("Humidity", "${weather.humidity}%", Modifier.weight(1f))
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Before you go", style = MaterialTheme.typography.titleMedium)
                    Text(outingTip(weather))
                }
            }
            Text("Weather time: ${weather.time.replace('T', ' ')}\nLocal time in ${state.city.label}",
                style = MaterialTheme.typography.bodySmall)
        }
        OutlinedButton(onClick = onRefresh, enabled = !state.loading) { Text("Refresh weather") }
        Text("Model-based current conditions, not a full-day forecast. Tips are general guidance.",
            style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = { uri.openUri("https://open-meteo.com/") }) { Text("Weather data by Open-Meteo") }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier) {
    OutlinedCard(modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun SettingsScreen(state: WeatherUiState, onCity: (City) -> Unit, onFahrenheit: (Boolean) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Make it local", style = MaterialTheme.typography.titleLarge)
        Text("Select a city. Weather updates automatically.")
        City.entries.forEach { city ->
            OutlinedButton(onClick = { onCity(city) }, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.city == city) "✓ ${city.label} (selected)" else city.label)
            }
        }
        HorizontalDivider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Use Fahrenheit (°F)", modifier = Modifier.weight(1f))
            Switch(checked = state.fahrenheit, onCheckedChange = onFahrenheit,
                modifier = Modifier.semantics { contentDescription = "Use Fahrenheit" })
        }
        Text("Off: Celsius (°C). Wind speed stays in km/h.")
        Text("Preferences survive screen rotation and tab changes. They reset when the app process restarts.",
            style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    MobileTech_UtilityAppTheme {
        UtilityApp(WeatherUiState(loading = false, weather = CurrentWeather(
            "2026-10-29T20:00", 29.0, 34.0, 78, 2, 10.5)), {}, {}, {})
    }
}
