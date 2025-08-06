package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.data.model.HourWeather
import kotlin.math.roundToInt

@Composable
fun SoilHourlyDataSection(
    hours: List<HourWeather>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Soil Conditions (24 Hours)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            items(hours.take(24)) { hour ->
                SoilHourlyCard(hour = hour)
            }
        }
    }
}

@Composable
fun SoilHourlyCard(hour: HourWeather) {
    Card(
        modifier = Modifier.width(160.dp),
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
            // Time header
            Text(
                text = hour.datetime.substring(11, 16), // Extract HH:MM
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB),
            )

            // Soil Temperature
            hour.soilTemperature?.let { soilTemp ->
                SoilDataRow(
                    icon = Icons.Default.Thermostat,
                    label = "Soil Temp",
                    value = "${soilTemp.roundToInt()}°C",
                    color = Color(0xFFFF6B35),
                )
            }

            // Soil Moisture
            hour.soilMoisture?.let { soilMoisture ->
                SoilDataRow(
                    icon = Icons.Default.WaterDrop,
                    label = "Moisture",
                    value = "${(soilMoisture * 100).roundToInt()}%",
                    color = Color(0xFF1E88E5),
                )
            }

            // Evapotranspiration
            hour.evapotranspiration?.let { et ->
                SoilDataRow(
                    icon = Icons.Default.CompareArrows,
                    label = "ET",
                    value = "${String.format("%.1f", et)}mm",
                    color = Color(0xFF42A5F5),
                )
            }

            // UV Index
            SoilDataRow(
                icon = Icons.Default.WbSunny,
                label = "UV",
                value = "${hour.uvindex.roundToInt()}",
                color = Color(0xFFFFD700),
            )
        }
    }
}

@Composable
fun SoilDataRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(16.dp),
        )

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun SoilDetailedCards(
    hours: List<HourWeather>,
    modifier: Modifier = Modifier,
) {
    val currentHour = hours.firstOrNull() ?: return

    Column(modifier = modifier) {
        Text(
            text = "Detailed Soil Analysis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Soil Temperature Card with gradient
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(20.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFF9A56),
                                    Color(0xFFFF6B35),
                                ),
                            ),
                        )
                        .padding(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column {
                                Text(
                                    text = "Soil Temperature",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                currentHour.soilTemperature?.let { temp ->
                                    Text(
                                        text = "${temp.roundToInt()}°C",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                } ?: Text(
                                    text = "N/A",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = "Temperature",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(32.dp),
                            )
                        }

                        Text(
                            text = getSoilTempAdvice(currentHour.soilTemperature),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            // Soil Moisture Card with gradient
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(20.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF42A5F5),
                                    Color(0xFF1E88E5),
                                ),
                            ),
                        )
                        .padding(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column {
                                Text(
                                    text = "Soil Moisture",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                currentHour.soilMoisture?.let { moisture ->
                                    Text(
                                        text = "${(moisture * 100).roundToInt()}%",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                } ?: Text(
                                    text = "N/A",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Moisture",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(32.dp),
                            )
                        }

                        Text(
                            text = getSoilMoistureAdvice(currentHour.soilMoisture),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Evapotranspiration Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF66BB6A),
                                Color(0xFF4CAF50),
                            ),
                        ),
                    )
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "Evapotranspiration",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        currentHour.evapotranspiration?.let { et ->
                            Text(
                                text = "${String.format("%.1f", et)}mm/hr",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        } ?: Text(
                            text = "N/A",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Water loss rate from soil & plants",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = "Evapotranspiration",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        }
    }
}

private fun getSoilTempAdvice(soilTemp: Double?): String {
    return when {
        soilTemp == null -> "Monitor soil temperature"
        soilTemp < 5 -> "Too cold for most crops"
        soilTemp < 10 -> "Cool - limited growth"
        soilTemp < 20 -> "Good for cool season crops"
        soilTemp < 30 -> "Optimal for warm season crops"
        else -> "Hot - may stress plants"
    }
}

private fun getSoilMoistureAdvice(soilMoisture: Double?): String {
    return when {
        soilMoisture == null -> "Monitor soil moisture"
        soilMoisture < 0.2 -> "Very dry - irrigation needed"
        soilMoisture < 0.4 -> "Dry - consider watering"
        soilMoisture < 0.7 -> "Optimal for most crops"
        soilMoisture < 0.9 -> "Wet - monitor drainage"
        else -> "Saturated - risk of waterlogging"
    }
}
