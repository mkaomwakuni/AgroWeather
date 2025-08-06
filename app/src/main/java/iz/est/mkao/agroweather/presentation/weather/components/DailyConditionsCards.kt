package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.data.model.CurrentConditions
import iz.est.mkao.agroweather.data.model.DayWeather
import kotlin.math.roundToInt

@Composable
fun EnhancedDailyConditionsSection(
    dailyWeather: List<DayWeather>,
    currentConditions: CurrentConditions,
    modifier: Modifier = Modifier,
    onInsightClick: (String) -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(
            text = "Current Conditions & Daily Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Current conditions highlight cards
        CurrentConditionsHighlight(
            currentConditions = currentConditions,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Daily weather cards
        DailyWeatherCardsGrid(
            dailyWeather = dailyWeather.take(14),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Agricultural insights
        AgriculturalInsightsSection(
            dailyWeather = dailyWeather.take(7),
            currentConditions = currentConditions,
            onInsightClick = onInsightClick,
        )
    }
}

@Composable
fun CurrentConditionsHighlight(
    currentConditions: CurrentConditions,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        item {
            CurrentConditionCard(
                title = "Temperature",
                value = "${currentConditions.temp.roundToInt()}°C",
                subtitle = "Feels ${currentConditions.feelslike.roundToInt()}°C",
                icon = Icons.Default.Thermostat,
                iconColor = Color(0xFFFF6B35),
            )
        }

        item {
            CurrentConditionCard(
                title = "Humidity",
                value = "${currentConditions.humidity.roundToInt()}%",
                subtitle = getHumidityStatus(currentConditions.humidity),
                icon = Icons.Default.WaterDrop,
                iconColor = Color(0xFF1E88E5),
            )
        }

        item {
            CurrentConditionCard(
                title = "Wind",
                value = "${currentConditions.windspeed.roundToInt()} km/h",
                subtitle = getWindDirection(currentConditions.winddir),
                icon = Icons.Default.Air,
                iconColor = Color(0xFF4682B4),
            )
        }

        item {
            CurrentConditionCard(
                title = "UV Index",
                value = "${currentConditions.uvindex.roundToInt()}",
                subtitle = getUVStatus(currentConditions.uvindex),
                icon = Icons.Default.WbSunny,
                iconColor = Color(0xFFFFD700),
            )
        }

        if (currentConditions.precip > 0) {
            item {
                CurrentConditionCard(
                    title = "Rainfall",
                    value = "${String.format("%.1f", currentConditions.precip)}mm",
                    subtitle = "${currentConditions.precipprob.roundToInt()}% chance",
                    icon = Icons.Default.Grain,
                    iconColor = Color(0xFF4169E1),
                )
            }
        }

        // Agricultural specific cards
        currentConditions.evapotranspiration?.let { et ->
            item {
                CurrentConditionCard(
                    title = "Evapotranspiration",
                    value = "${String.format("%.1f", et)}mm",
                    subtitle = "Water loss rate",
                    icon = Icons.Default.CompareArrows,
                    iconColor = Color(0xFF4CAF50),
                )
            }
        }
    }
}

@Composable
fun CurrentConditionCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = value,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp),
                )
            }

            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun DailyWeatherCardsGrid(
    dailyWeather: List<DayWeather>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "14-Day Detailed Forecast",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(600.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(dailyWeather) { day ->
                EnhancedDayWeatherCard(day = day)
            }
        }
    }
}

@Composable
fun EnhancedDayWeatherCard(
    day: DayWeather,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.border(
            width = 1.dp,
            color = Color(0xFFE9ECEF),
            shape = RoundedCornerShape(16.dp),
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Date and weather icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatDate(day.datetime),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB),
                )
                getWeatherIcon(day.icon)
            }

            // Temperature range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${day.temp.roundToInt()}°C",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (day.precipprob > 0) {
                    Text(
                        text = "${day.precipprob.roundToInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1E88E5),
                    )
                }
            }

            // Conditions
            Text(
                text = day.conditions,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )

            // Agricultural data if available
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                day.soilMoisture?.let { moisture ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Soil Moisture",
                            tint = Color(0xFF1E88E5),
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = "Soil: ${(moisture * 100).roundToInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                        )
                    }
                }

                if (day.uvindex > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "UV Index",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = "UV: ${day.uvindex.roundToInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                        )
                    }
                }

                if (day.windspeed > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = "Wind",
                            tint = Color(0xFF87CEEB),
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = "Wind: ${day.windspeed.roundToInt()} km/h",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AgriculturalInsightsSection(
    dailyWeather: List<DayWeather>,
    currentConditions: CurrentConditions,
    modifier: Modifier = Modifier,
    onInsightClick: (String) -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(
            text = "Agricultural Insights",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(400.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Irrigation recommendation
            item {
                val irrigationInsight = getAdvancedIrrigationRecommendation(dailyWeather, currentConditions)
                ClickableInsightCard(
                    title = "Irrigation",
                    value = irrigationInsight.recommendation,
                    icon = Icons.Default.WaterDrop,
                    color = Color(0xFF1E88E5),
                    onClick = { onInsightClick("Provide detailed irrigation advice for today's conditions: ${irrigationInsight.context}") },
                )
            }

            // Planting/Harvesting conditions
            item {
                val plantingInsight = getPlantingHarvestingConditions(dailyWeather, currentConditions)
                ClickableInsightCard(
                    title = "Planting/Harvest",
                    value = plantingInsight.recommendation,
                    icon = Icons.Default.Eco,
                    color = Color(0xFF4CAF50),
                    onClick = { onInsightClick("What farming activities are best suited for today's weather: ${plantingInsight.context}") },
                )
            }

            // Pest and disease risk
            item {
                val pestRisk = getPestDiseaseRisk(dailyWeather, currentConditions)
                ClickableInsightCard(
                    title = "Pest/Disease Risk",
                    value = pestRisk.recommendation,
                    icon = Icons.Default.Warning,
                    color = if (pestRisk.recommendation.contains("High")) Color(0xFFE53E3E) else Color(0xFFFF8C00),
                    onClick = { onInsightClick("Analyze pest and disease risks for current weather conditions: ${pestRisk.context}") },
                )
            }

            // Fertilizer application
            item {
                val fertilizerTiming = getFertilizerApplicationTiming(dailyWeather, currentConditions)
                ClickableInsightCard(
                    title = "Fertilizer Timing",
                    value = fertilizerTiming.recommendation,
                    icon = Icons.Default.Grass,
                    color = Color(0xFF38A169),
                    onClick = { onInsightClick("When is the best time to apply fertilizer given these conditions: ${fertilizerTiming.context}") },
                )
            }

            // Soil conditions
            item {
                val soilConditions = getSoilWorkingConditions(dailyWeather, currentConditions)
                ClickableInsightCard(
                    title = "Soil Working",
                    value = soilConditions.recommendation,
                    icon = Icons.Default.Terrain,
                    color = Color(0xFF8B4513),
                    onClick = { onInsightClick("Analyze soil working conditions and field operations suitability: ${soilConditions.context}") },
                )
            }

            // Weather stress analysis
            item {
                val stressAnalysis = getCropStressAnalysis(dailyWeather, currentConditions)
                ClickableInsightCard(
                    title = "Crop Stress",
                    value = stressAnalysis.recommendation,
                    icon = Icons.Default.Warning,
                    color = if (stressAnalysis.recommendation.contains("High")) Color(0xFFE53E3E) else Color(0xFF9C27B0),
                    onClick = { onInsightClick("Explain potential crop stress factors based on current weather: ${stressAnalysis.context}") },
                )
            }
        }
    }
}

@Composable
fun InsightCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
fun ClickableInsightCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

// Data classes for insights
data class AgriculturalInsight(
    val recommendation: String,
    val context: String,
)

// Helper functions
private fun getHumidityStatus(humidity: Double): String {
    return when {
        humidity < 30 -> "Low - Dry"
        humidity < 60 -> "Comfortable"
        humidity < 80 -> "High - Humid"
        else -> "Very High"
    }
}

private fun getWindDirection(degrees: Double): String {
    val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val index = ((degrees + 22.5) / 45.0).toInt() % 8
    return directions[index]
}

private fun getUVStatus(uvIndex: Double): String {
    return when {
        uvIndex <= 2 -> "Low"
        uvIndex <= 5 -> "Moderate"
        uvIndex <= 7 -> "High"
        uvIndex <= 10 -> "Very High"
        else -> "Extreme"
    }
}

@Composable
private fun getWeatherIcon(iconCode: String) {
    val (icon, color) = when (iconCode) {
        "clear-day" -> Icons.Default.WbSunny to Color(0xFFFFD700)
        "clear-night" -> Icons.Default.WbTwilight to Color(0xFF483D8B)
        "rain", "showers-day", "showers-night" -> Icons.Default.Grain to Color(0xFF4169E1)
        "snow", "snow-showers-day", "snow-showers-night" -> Icons.Default.AcUnit to Color(0xFFE6E6FA)
        "wind" -> Icons.Default.Air to Color(0xFF87CEEB)
        "fog" -> Icons.Default.Visibility to Color(0xFF708090)
        "cloudy" -> Icons.Default.Cloud to Color(0xFF696969)
        "partly-cloudy-day" -> Icons.Default.Cloud to Color(0xFF87CEFA)
        "partly-cloudy-night" -> Icons.Default.Cloud to Color(0xFF2F4F4F)
        else -> Icons.Default.WbSunny to Color(0xFFFFD700)
    }

    Icon(
        imageVector = icon,
        contentDescription = iconCode,
        tint = color,
        modifier = Modifier.size(20.dp),
    )
}

private fun formatDate(dateString: String): String {
    return try {
        val parts = dateString.split("-")
        if (parts.size >= 3) {
            "${parts[2]}/${parts[1]}"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

// Advanced agricultural insight functions with realistic conditions
private fun getAdvancedIrrigationRecommendation(
    dailyWeather: List<DayWeather>,
    currentConditions: CurrentConditions,
): AgriculturalInsight {
    val upcomingRain = dailyWeather.take(3).sumOf { it.precip }
    val currentMoisture = currentConditions.soilmoisture ?: 0.35
    val evapotranspiration = currentConditions.evapotranspiration ?: 3.0
    val temp = currentConditions.temp
    val windSpeed = currentConditions.windspeed
    val humidity = currentConditions.humidity

    val waterStress = evapotranspiration - (currentConditions.precip + upcomingRain / 3)

    val recommendation = when {
        upcomingRain > 15 -> "Postpone - Heavy Rain Expected"
        currentMoisture < 0.15 -> "Critical - Irrigate Immediately"
        currentMoisture < 0.25 && waterStress > 4 -> "High Priority - Irrigate Today"
        currentMoisture < 0.4 && temp > 30 && humidity < 40 -> "Recommended - Hot & Dry"
        windSpeed > 25 -> "Delay - Too Windy"
        currentMoisture > 0.7 -> "Avoid - Risk of Waterlogging"
        else -> "Monitor - Optional Today"
    }

    val context = "Soil moisture: ${(currentMoisture * 100).roundToInt()}%, ET rate: ${String.format("%.1f", evapotranspiration)}mm, upcoming rain: ${String.format("%.1f", upcomingRain)}mm"

    return AgriculturalInsight(recommendation, context)
}

private fun getPlantingHarvestingConditions(
    dailyWeather: List<DayWeather>,
    currentConditions: CurrentConditions,
): AgriculturalInsight {
    val temp = currentConditions.temp
    val soilTemp = currentConditions.soiltemp ?: temp - 2
    val soilMoisture = currentConditions.soilmoisture ?: 0.4
    val windSpeed = currentConditions.windspeed
    val upcomingRain = dailyWeather.take(2).sumOf { it.precip }

    val recommendation = when {
        soilTemp < 8 -> "Too Cold for Planting"
        soilTemp > 35 -> "Too Hot - Delay Planting"
        soilMoisture < 0.2 -> "Too Dry - Pre-irrigate"
        soilMoisture > 0.8 -> "Too Wet - Wait for Drainage"
        windSpeed > 20 -> "Too Windy for Operations"
        upcomingRain > 20 -> "Postpone - Heavy Rain Coming"
        temp in 15.0..28.0 && soilMoisture in 0.3..0.6 -> "Excellent for Planting"
        temp in 20.0..35.0 && soilMoisture < 0.7 -> "Good for Harvesting"
        else -> "Fair - Monitor Conditions"
    }

    val context = "Soil temp: ${String.format("%.1f", soilTemp)}°C, soil moisture: ${(soilMoisture * 100).roundToInt()}%, wind: ${windSpeed.roundToInt()}km/h"

    return AgriculturalInsight(recommendation, context)
}

private fun getPestDiseaseRisk(
    dailyWeather: List<DayWeather>,
    currentConditions: CurrentConditions,
): AgriculturalInsight {
    val temp = currentConditions.temp
    val humidity = currentConditions.humidity
    val recentRain = dailyWeather.take(3).sumOf { it.precip }
    val leafWetness = if (humidity > 85 || recentRain > 5) "High" else "Low"

    val recommendation = when {
        temp in 20.0..30.0 && humidity > 80 && recentRain > 10 -> "High Risk - Apply Preventive Treatment"
        temp in 15.0..25.0 && humidity > 70 -> "Moderate Risk - Monitor Closely"
        temp > 35 || temp < 10 -> "Low Risk - Unfavorable for Pests"
        humidity < 50 && recentRain < 2 -> "Low Risk - Dry Conditions"
        else -> "Monitor - Normal Conditions"
    }

    val context = "Temperature: ${temp.roundToInt()}°C, humidity: ${humidity.roundToInt()}%, recent rain: ${String.format("%.1f", recentRain)}mm, leaf wetness: $leafWetness"

    return AgriculturalInsight(recommendation, context)
}

private fun getFertilizerApplicationTiming(
    dailyWeather: List<DayWeather>,
    currentConditions: CurrentConditions,
): AgriculturalInsight {
    val upcomingRain = dailyWeather.take(2).sumOf { it.precip }
    val windSpeed = currentConditions.windspeed
    val temp = currentConditions.temp
    val soilMoisture = currentConditions.soilmoisture ?: 0.4

    val recommendation = when {
        upcomingRain > 15 -> "Postpone - Heavy Rain Risk"
        upcomingRain < 2 && soilMoisture < 0.3 -> "Poor - Too Dry to Dissolve"
        windSpeed > 15 -> "Avoid - Risk of Drift"
        temp > 30 -> "Early Morning Only - Too Hot"
        upcomingRain in 5.0..10.0 && windSpeed < 10 -> "Excellent - Ideal Conditions"
        soilMoisture > 0.4 && windSpeed < 15 -> "Good - Apply Today"
        else -> "Fair - Suboptimal Timing"
    }

    val context = "Expected rain: ${String.format("%.1f", upcomingRain)}mm, wind: ${windSpeed.roundToInt()}km/h, soil moisture: ${(soilMoisture * 100).roundToInt()}%"

    return AgriculturalInsight(recommendation, context)
}

private fun getSoilWorkingConditions(
    dailyWeather: List<DayWeather>,
    currentConditions: CurrentConditions,
): AgriculturalInsight {
    val soilMoisture = currentConditions.soilmoisture ?: 0.4
    val recentRain = dailyWeather.take(1).sumOf { it.precip }
    val upcomingRain = dailyWeather.take(2).sumOf { it.precip }
    val temp = currentConditions.temp

    val recommendation = when {
        soilMoisture > 0.7 -> "Too Wet - Risk of Compaction"
        soilMoisture < 0.15 -> "Too Dry - Dusty Conditions"
        recentRain > 10 -> "Wait 24-48h - Recently Wet"
        upcomingRain > 10 -> "Complete Soon - Rain Expected"
        temp < 5 -> "Frozen - Avoid Field Work"
        soilMoisture in 0.25..0.45 -> "Excellent - Perfect Conditions"
        soilMoisture in 0.2..0.6 -> "Good - Suitable for Work"
        else -> "Fair - Acceptable Conditions"
    }

    val context = "Soil moisture: ${(soilMoisture * 100).roundToInt()}%, recent rain: ${String.format("%.1f", recentRain)}mm, upcoming rain: ${String.format("%.1f", upcomingRain)}mm"

    return AgriculturalInsight(recommendation, context)
}

private fun getCropStressAnalysis(
    dailyWeather: List<DayWeather>,
    currentConditions: CurrentConditions,
): AgriculturalInsight {
    val temp = currentConditions.temp
    val humidity = currentConditions.humidity
    val windSpeed = currentConditions.windspeed
    val uvIndex = currentConditions.uvindex
    val soilMoisture = currentConditions.soilmoisture ?: 0.4
    val consecutiveHotDays = dailyWeather.take(5).count { it.temp > 32 }

    val stressFactors = mutableListOf<String>()

    if (temp > 35) stressFactors.add("Heat stress")
    if (temp < 5) stressFactors.add("Cold stress")
    if (soilMoisture < 0.2) stressFactors.add("Drought stress")
    if (soilMoisture > 0.8) stressFactors.add("Waterlogging")
    if (windSpeed > 30) stressFactors.add("Wind damage risk")
    if (uvIndex > 8) stressFactors.add("UV damage")
    if (consecutiveHotDays >= 3) stressFactors.add("Prolonged heat")

    val recommendation = when {
        stressFactors.size >= 3 -> "High Stress - Multiple Factors"
        stressFactors.size == 2 -> "Moderate Stress - Monitor"
        stressFactors.size == 1 -> "Low Stress - ${stressFactors[0]}"
        else -> "Minimal Stress - Good Conditions"
    }

    val context = "Temperature: ${temp.roundToInt()}°C, soil moisture: ${(soilMoisture * 100).roundToInt()}%, wind: ${windSpeed.roundToInt()}km/h, UV: ${uvIndex.roundToInt()}"

    return AgriculturalInsight(recommendation, context)
}
