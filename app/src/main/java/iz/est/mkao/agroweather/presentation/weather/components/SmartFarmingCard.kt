package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.data.model.DayWeather
import kotlin.math.roundToInt

@Composable
fun SmartFarmingCard(
    dayWeather: DayWeather,
    onNavigateToChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.smart_farming_insights),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )

                IconButton(
                    onClick = {
                        val weatherSummary = buildWeatherSummary(dayWeather)
                        onNavigateToChat("Based on today's weather conditions: $weatherSummary, what specific farming activities should I prioritize and what precautions should I take?")
                    },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(20.dp),
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = stringResource(R.string.ask_ai_personalized_advice),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            getDynamicFarmingRecommendations(dayWeather).forEach { recommendation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = recommendation.icon,
                            contentDescription = null,
                            tint = recommendation.color,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recommendation.title,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                            Text(
                                text = recommendation.description,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        val irrigationAdvice = "Should I irrigate my crops today? Current conditions: soil moisture ${dayWeather.soilMoisture?.let { "${(it * 100).roundToInt()}%" } ?: "unknown"}, precipitation chance ${dayWeather.precipprob.roundToInt()}%, temperature ${dayWeather.temp.roundToInt()}°C, humidity ${dayWeather.humidity.roundToInt()}%"
                        onNavigateToChat(irrigationAdvice)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Irrigation?", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val cropAdvice = "What crops should I plant or harvest given today's weather conditions: ${dayWeather.conditions}, temperature ${dayWeather.temp.roundToInt()}°C, humidity ${dayWeather.humidity.roundToInt()}%?"
                        onNavigateToChat(cropAdvice)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Grass,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Crop Care", fontSize = 12.sp)
                }
            }
        }
    }
}

data class FarmingRecommendation(
    val icon: ImageVector,
    val color: Color,
    val title: String,
    val description: String,
)

private fun buildWeatherSummary(dayWeather: DayWeather): String {
    return buildString {
        append("Temperature ${dayWeather.temp.roundToInt()}°C")
        append(", ${dayWeather.conditions}")
        append(", Humidity ${dayWeather.humidity.roundToInt()}%")
        append(", Wind ${dayWeather.windspeed.roundToInt()} km/h")
        if (dayWeather.precipprob > 10) {
            append(", ${dayWeather.precipprob.roundToInt()}% rain chance")
        }
        dayWeather.soilMoisture?.let {
            append(", Soil moisture ${(it * 100).roundToInt()}%")
        } ?: append(", Soil moisture data unavailable")
        if (dayWeather.uvindex > 6) {
            append(", High UV ${dayWeather.uvindex.roundToInt()}")
        }
    }
}

private fun getDynamicFarmingRecommendations(dayWeather: DayWeather): List<FarmingRecommendation> {
    val recommendations = mutableListOf<FarmingRecommendation>()

    val soilMoisture = dayWeather.soilMoisture ?: 0.5
    val soilMoistureText = dayWeather.soilMoisture?.let { "${(it * 100).roundToInt()}%" } ?: "estimated 50%"
    val precipChance = dayWeather.precipprob
    val shouldIrrigate = soilMoisture < 0.3 || (soilMoisture < 0.5 && precipChance < 20)

    if (shouldIrrigate) {
        val urgency = if (soilMoisture < 0.2) "urgently needed" else "recommended"
        recommendations.add(
            FarmingRecommendation(
                icon = Icons.Default.WaterDrop,
                color = if (soilMoisture < 0.2) Color(0xFFE53935) else Color(0xFF2196F3),
                title = "Irrigation ${urgency.split(" ").first().replaceFirstChar { it.uppercaseChar() }}",
                description = "Soil moisture at $soilMoistureText, ${precipChance.roundToInt()}% rain chance. Irrigation $urgency.",
            ),
        )
    } else if (precipChance > 70) {
        recommendations.add(
            FarmingRecommendation(
                icon = Icons.Default.Umbrella,
                color = Color(0xFF4CAF50),
                title = "Skip Irrigation",
                description = "${precipChance.roundToInt()}% chance of rain expected. Natural watering likely.",
            ),
        )
    }

    when {
        dayWeather.temp > 35 -> {
            recommendations.add(
                FarmingRecommendation(
                    icon = Icons.Default.Thermostat,
                    color = Color(0xFFE53935),
                    title = "Extreme Heat Alert",
                    description = "${dayWeather.temp.roundToInt()}°C - Provide shade for livestock, increase watering frequency.",
                ),
            )
        }
        dayWeather.temp > 30 -> {
            recommendations.add(
                FarmingRecommendation(
                    icon = Icons.Default.WbSunny,
                    color = Color(0xFFFF9800),
                    title = "Heat Management",
                    description = "${dayWeather.temp.roundToInt()}°C - Monitor crops for heat stress.",
                ),
            )
        }
        dayWeather.temp < 5 -> {
            recommendations.add(
                FarmingRecommendation(
                    icon = Icons.Default.AcUnit,
                    color = Color(0xFF2196F3),
                    title = "Frost Protection",
                    description = "${dayWeather.temp.roundToInt()}°C - Protect sensitive plants, cover crops if needed.",
                ),
            )
        }
        dayWeather.temp in 15.0..25.0 -> {
            recommendations.add(
                FarmingRecommendation(
                    icon = Icons.Default.Check,
                    color = Color(0xFF4CAF50),
                    title = "Ideal Temperature",
                    description = "${dayWeather.temp.roundToInt()}°C - Perfect for farming activities.",
                ),
            )
        }
    }

    if (dayWeather.windspeed > 20) {
        recommendations.add(
            FarmingRecommendation(
                icon = Icons.Default.Air,
                color = Color(0xFFFF9800),
                title = "Windy Conditions",
                description = "${dayWeather.windspeed.roundToInt()} km/h winds - Be cautious with spraying.",
            ),
        )
    }

    return recommendations.take(3)
}
