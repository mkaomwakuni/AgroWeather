package iz.est.mkao.agroweather.presentation.suggestion.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.presentation.common.LoadingState
import iz.est.mkao.agroweather.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun WeatherOvercastSection(
    weatherData: iz.est.mkao.farmweather.data.model.WeatherResponse?,
    loadingState: LoadingState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Today's Overcast",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        when (loadingState) {
            is LoadingState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is LoadingState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Failed to load weather data",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onRetry) {
                            Text("Retry", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            else -> {
                weatherData?.let { weather ->
                    WeatherDataGrid(weather = weather)
                } ?: run {
                    EmptyWeatherState(onRetry = onRetry)
                }
            }
        }
    }
}

@Composable
private fun WeatherDataGrid(weather: iz.est.mkao.farmweather.data.model.WeatherResponse) {
    val currentConditions = weather.currentConditions
    val today = weather.days.firstOrNull()
    
    // Get agricultural data from today's hourly data
    val todayHourlyData = today?.hours ?: emptyList()
    
    // Calculate agricultural averages/totals from hourly data
    val avgSoilMoisture = todayHourlyData.mapNotNull { it.soilMoisture }.takeIf { it.isNotEmpty() }?.average()
    val avgSoilTemperature = todayHourlyData.mapNotNull { it.soilTemperature }.takeIf { it.isNotEmpty() }?.average()
    val totalEvapotranspiration = todayHourlyData.mapNotNull { it.evapotranspiration }.takeIf { it.isNotEmpty() }?.sum()
    val avgUVIndex = todayHourlyData.mapNotNull { it.uvindex.takeIf { uv -> uv > 0 } }.takeIf { it.isNotEmpty() }?.average()
    val avgDirectRadiation = todayHourlyData.mapNotNull { it.solarradiation.takeIf { solar -> solar > 0 } }.takeIf { it.isNotEmpty() }?.average()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // First row: Temperature and Rainfall
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WeatherCard(
                title = "Temperature",
                value = "${currentConditions.temp.roundToInt()}°C",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Thermostat,
                iconColor = SunnyYellow,
            )
            WeatherCard(
                title = "Rainfall",
                value = if (currentConditions.precip > 0) "${currentConditions.precip}mm" else "0mm",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Water,
                iconColor = IrrigationBlue,
            )
        }

        // Second row: UV Index and Solar Radiation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WeatherCard(
                title = "UV Index",
                value = when {
                    today?.uvindex != null && today.uvindex > 0 -> "${today.uvindex.roundToInt()}"
                    avgUVIndex != null -> "${avgUVIndex.roundToInt()}"
                    else -> "N/A"
                },
                modifier = Modifier.weight(1f),
                icon = Icons.Default.WbSunny,
                iconColor = SunnyYellow,
            )
            WeatherCard(
                title = "Solar Radiation",
                value = when {
                    today?.solarenergy != null && today.solarenergy > 0 -> "${(today.solarenergy / 24).roundToInt()} W/m²"
                    avgDirectRadiation != null -> "${avgDirectRadiation.roundToInt()} W/m²"
                    else -> "N/A"
                },
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Flare,
                iconColor = SunnyYellow,
            )
        }

        // Third row: Soil data
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WeatherCard(
                title = "Soil Moisture",
                value = when {
                    avgSoilMoisture != null -> "${(avgSoilMoisture * 100).roundToInt()}%"
                    today?.soilMoisture != null -> "${(today.soilMoisture * 100).roundToInt()}%"
                    else -> "N/A"
                },
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Opacity,
                iconColor = GrassGreen,
            )
            WeatherCard(
                title = "Soil Temperature",
                value = when {
                    avgSoilTemperature != null -> "${avgSoilTemperature.roundToInt()}°C"
                    today?.soilTemperature != null -> "${today.soilTemperature.roundToInt()}°C"
                    else -> "N/A"
                },
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Thermostat,
                iconColor = SoilBrown,
            )
        }

        // Fourth row: Evapotranspiration and Sunlight
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EvapotranspirationCard(
                currentET = avgDirectRadiation?.let { it / 1000 },
                dailyET = totalEvapotranspiration ?: today?.evapotranspiration,
                modifier = Modifier.weight(1f),
            )
            today?.let { dayWeather ->
                val sunriseTime = if (dayWeather.sunrise.isNotEmpty()) {
                    try {
                        dayWeather.sunrise.split("T").lastOrNull()?.split(":")?.take(2)?.joinToString(":") ?: "N/A"
                    } catch (e: Exception) {
                        "N/A"
                    }
                } else "N/A"
                
                val sunsetTime = if (dayWeather.sunset.isNotEmpty()) {
                    try {
                        dayWeather.sunset.split("T").lastOrNull()?.split(":")?.take(2)?.joinToString(":") ?: "N/A"
                    } catch (e: Exception) {
                        "N/A"
                    }
                } else "N/A"
                
                WeatherCard(
                    title = "Sunlight",
                    value = if (sunriseTime != "N/A" && sunsetTime != "N/A") "$sunriseTime - $sunsetTime" else "N/A",
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.WbSunny,
                    iconColor = SunnyYellow,
                )
            } ?: WeatherCard(
                title = "Sunlight",
                value = "N/A",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.WbSunny,
                iconColor = SunnyYellow,
            )
        }

        // Fifth row: Additional atmospheric data
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WeatherCard(
                title = "Cloud Cover",
                value = "${currentConditions.cloudcover.roundToInt()}%",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Cloud,
                iconColor = CloudyGray,
            )
            WeatherCard(
                title = "Pressure",
                value = "${currentConditions.pressure.roundToInt()} hPa",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Speed,
                iconColor = MaterialTheme.colorScheme.primary,
            )
        }

        // Sixth row: Wind and Visibility
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WeatherCard(
                title = "Wind Speed",
                value = "${currentConditions.windspeed.roundToInt()} km/h",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Air,
                iconColor = IrrigationBlue,
            )
            WeatherCard(
                title = "Visibility",
                value = "${(currentConditions.visibility / 1000).roundToInt()} km",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Visibility,
                iconColor = CloudyGray,
            )
        }
    }
}

@Composable
private fun EmptyWeatherState(onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Weather data unavailable",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Please check your internet connection and try again",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Retry")
            }
        }
    }
}
