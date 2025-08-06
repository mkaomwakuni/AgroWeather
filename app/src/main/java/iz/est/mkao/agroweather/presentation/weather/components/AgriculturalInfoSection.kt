package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.data.model.DayWeather
import kotlin.math.roundToInt

@Composable
fun AgriculturalInfoSection(
    dayWeather: DayWeather,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.agricultural_information),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                val soilMoisture = dayWeather.soilMoisture ?: 0.45
                val subtitle = if (dayWeather.soilMoisture != null) {
                    getSoilMoistureAdvice(soilMoisture)
                } else {
                    stringResource(R.string.estimated_value)
                }
                AgriculturalCard(
                    title = stringResource(R.string.soil_moisture),
                    value = "${(soilMoisture * 100).roundToInt()}%",
                    icon = Icons.Default.WaterDrop,
                    subtitle = subtitle,
                )
            }
            
            item {
                val soilTemp = dayWeather.soilTemperature ?: (dayWeather.temp - 2)
                val subtitle = if (dayWeather.soilTemperature != null) {
                    getSoilTempAdvice(soilTemp)
                } else {
                    stringResource(R.string.estimated_soil_temp)
                }
                AgriculturalCard(
                    title = stringResource(R.string.soil_temperature),
                    value = "${soilTemp.roundToInt()}°C",
                    icon = Icons.Default.Thermostat,
                    subtitle = subtitle,
                )
            }
            
            item {
                val sprayConditions = calculateSprayConditions(dayWeather)
                AgriculturalCard(
                    title = stringResource(R.string.spray_conditions),
                    value = sprayConditions.status,
                    icon = Icons.Default.Shower,
                    subtitle = sprayConditions.reason,
                )
            }
            
            item {
                val bestTime = getBestSprayTime(dayWeather)
                AgriculturalCard(
                    title = stringResource(R.string.best_spray_time),
                    value = bestTime.time,
                    icon = Icons.Default.Schedule,
                    subtitle = bestTime.reason,
                )
            }
        }
    }
}

@Composable
private fun AgriculturalCard(
    title: String,
    value: String,
    icon: ImageVector,
    subtitle: String? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (subtitle != null) 120.dp else 100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = getAgriculturalIconColor(icon),
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun getAgriculturalIconColor(icon: ImageVector): Color {
    return when (icon) {
        Icons.Default.WaterDrop -> colorResource(R.color.detail_icon_water_drop)
        Icons.Default.Thermostat -> colorResource(R.color.detail_icon_thermostat)
        Icons.Default.Shower -> colorResource(R.color.spray_excellent)
        Icons.Default.Schedule -> colorResource(R.color.detail_icon_default)
        else -> colorResource(R.color.detail_icon_default)
    }
}

data class SprayConditions(
    val status: String,
    val reason: String
)

data class BestSprayTime(
    val time: String,
    val reason: String
)

private fun calculateSprayConditions(dayWeather: DayWeather): SprayConditions {
    val windSpeed = dayWeather.windspeed
    val rainChance = dayWeather.precipprob
    val temperature = dayWeather.temp
    val humidity = dayWeather.humidity
    
    return when {
        windSpeed > 25 -> SprayConditions("Poor", "High winds (${windSpeed.roundToInt()} km/h)")
        rainChance > 70 -> SprayConditions("Poor", "${rainChance.roundToInt()}% rain chance")
        temperature > 35 -> SprayConditions("Poor", "Too hot (${temperature.roundToInt()}°C)")
        temperature < 5 -> SprayConditions("Poor", "Too cold (${temperature.roundToInt()}°C)")
        windSpeed > 15 -> SprayConditions("Fair", "Moderate winds (${windSpeed.roundToInt()} km/h)")
        rainChance > 40 -> SprayConditions("Fair", "${rainChance.roundToInt()}% rain chance")
        temperature > 30 -> SprayConditions("Fair", "Hot weather (${temperature.roundToInt()}°C)")
        temperature < 10 -> SprayConditions("Fair", "Cool weather (${temperature.roundToInt()}°C)")
        humidity < 30 -> SprayConditions("Fair", "Low humidity (${humidity.roundToInt()}%)")
        windSpeed <= 10 && rainChance <= 20 && temperature in 15.0..28.0 -> 
            SprayConditions("Excellent", "Ideal conditions")
        windSpeed <= 15 && rainChance <= 30 -> 
            SprayConditions("Good", "Favorable conditions")
        else -> SprayConditions("Good", "Suitable for spraying")
    }
}

private fun getBestSprayTime(dayWeather: DayWeather): BestSprayTime {
    val windSpeed = dayWeather.windspeed
    val temperature = dayWeather.temp
    val rainChance = dayWeather.precipprob
    
    return when {
        rainChance > 50 -> BestSprayTime("Avoid Today", "Rain expected")
        windSpeed > 20 -> BestSprayTime("Avoid Today", "Too windy")
        temperature > 32 -> BestSprayTime("Early Morning", "Too hot during day")
        temperature < 8 -> BestSprayTime("Midday", "Too cold morning/evening")
        windSpeed <= 8 -> BestSprayTime("Anytime", "Calm conditions")
        else -> BestSprayTime("Morning/Evening", "Avoid midday heat")
    }
}

private fun getSoilMoistureAdvice(soilMoisture: Double): String {
    return when {
        soilMoisture < 0.2 -> "Very dry - irrigation needed"
        soilMoisture < 0.4 -> "Dry - consider watering"
        soilMoisture < 0.7 -> "Optimal for most crops"
        soilMoisture < 0.9 -> "Wet - monitor drainage"
        else -> "Saturated - risk of waterlogging"
    }
}

private fun getSoilTempAdvice(soilTemp: Double): String {
    return when {
        soilTemp < 5 -> "Too cold for most crops"
        soilTemp < 10 -> "Cool - limited growth"
        soilTemp < 20 -> "Good for cool season crops"
        soilTemp < 30 -> "Optimal for warm season crops"
        else -> "Hot - may stress plants"
    }
}
