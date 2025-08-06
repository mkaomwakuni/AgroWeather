package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.data.model.DayWeather
import iz.est.mkao.agroweather.data.model.HourWeather
import kotlin.math.*

@Composable
fun WeatherChartsSection(
    dailyWeather: List<DayWeather>,
    hourlyWeather: List<HourWeather>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Weather Analysis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Temperature trend chart
        TemperatureTrendChart(
            hourlyWeather = hourlyWeather.take(24),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
        )

        // Weather condition pie chart
        WeatherConditionsPieChart(
            dailyWeather = dailyWeather.take(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 16.dp),
        )

        // Multi-parameter comparison chart
        WeatherParametersChart(
            hourlyWeather = hourlyWeather.take(12),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        )
    }
}

@Composable
fun TemperatureTrendChart(
    hourlyWeather: List<HourWeather>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "24-Hour Temperature Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35),
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (hourlyWeather.isNotEmpty()) {
                Canvas(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    drawTemperatureChart(
                        scope = this,
                        hourlyData = hourlyWeather,
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No temperature data available")
                }
            }
        }
    }
}

@Composable
fun WeatherConditionsPieChart(
    dailyWeather: List<DayWeather>,
    modifier: Modifier = Modifier,
) {
    val conditionGroups = dailyWeather
        .groupBy { it.conditions }
        .mapValues { it.value.size }

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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Pie chart
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                if (conditionGroups.isNotEmpty()) {
                    Canvas(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        drawWeatherPieChart(
                            scope = this,
                            data = conditionGroups,
                        )
                    }
                }
            }

            // Legend
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "7-Day Weather Types",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )

                val colors = getWeatherColors()
                conditionGroups.entries.take(5).forEachIndexed { index, entry ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(colors[index % colors.size]),
                        )
                        Text(
                            text = "${entry.key} (${entry.value})",
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
fun WeatherParametersChart(
    hourlyWeather: List<HourWeather>,
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
        ) {
            Text(
                text = "Multi-Parameter Analysis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            if (hourlyWeather.isNotEmpty()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                ) {
                    drawMultiParameterChart(
                        scope = this,
                        hourlyData = hourlyWeather,
                    )
                }
                
                // Bottom labels for time axis
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    hourlyWeather.take(4).forEach { hour ->
                        Text(
                            text = formatChartHourLabel(hour.datetime),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No weather data available")
                }
            }

            // Legend with current values and indices
            LazyRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    listOf(
                        Triple("Humidity", Color(0xFF2196F3), "${hourlyWeather.firstOrNull()?.humidity?.toInt() ?: 0}%"), // Vivid blue
                        Triple("Wind", Color(0xFF4CAF50), "${hourlyWeather.firstOrNull()?.windspeed?.toInt() ?: 0} km/h"), // Vivid green
                        Triple("UV Index", Color(0xFFFF9800), "${hourlyWeather.firstOrNull()?.uvindex?.toInt() ?: 0}"), // Vivid orange
                        Triple("Clouds", Color(0xFF9C27B0), "${hourlyWeather.firstOrNull()?.cloudcover?.toInt() ?: 0}%"), // Vivid purple
                    ),
                ) { (label, color, value) ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = color.copy(alpha = 0.1f),
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color),
                            )
                            Column {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SoilAnalysisChart(
    hourlyWeather: List<HourWeather>,
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
        ) {
            Text(
                text = "Soil Conditions Analysis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(bottom = 12.dp),
            )

            if (hourlyWeather.isNotEmpty()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                ) {
                    drawSoilAnalysisChart(
                        scope = this,
                        hourlyData = hourlyWeather,
                    )
                }
                
                // Bottom labels for soil chart time axis
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    hourlyWeather.take(4).forEach { hour ->
                        Text(
                            text = formatChartHourLabel(hour.datetime),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Soil chart legend with current values
            LazyRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    listOf(
                        Triple("Soil Temp", Color(0xFFE53E3E), "${hourlyWeather.firstOrNull()?.soilTemperature?.toInt() ?: "N/A"}°C"), // Vivid red
                        Triple("Moisture", Color(0xFF2196F3), "${((hourlyWeather.firstOrNull()?.soilMoisture ?: 0.0) * 100).toInt()}%"), // Vivid blue
                        Triple("Evapotrans", Color(0xFF4CAF50), "${String.format("%.1f", hourlyWeather.firstOrNull()?.evapotranspiration ?: 0.0)}mm"), // Vivid green
                    ),
                ) { (label, color, value) ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = color.copy(alpha = 0.1f),
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color),
                            )
                            Column {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Chart Drawing Functions
private fun drawTemperatureChart(
    scope: DrawScope,
    hourlyData: List<HourWeather>,
) {
    val temperatures = hourlyData.map { it.temp }
    if (temperatures.isEmpty()) return

    val maxTemp = temperatures.maxOrNull() ?: 0.0
    val minTemp = temperatures.minOrNull() ?: 0.0
    val tempRange = maxTemp - minTemp

    val width = scope.size.width
    val height = scope.size.height - 40f // Leave space for labels
    val stepX = width / (temperatures.size - 1).coerceAtLeast(1)

    // Create gradient path
    val path = Path()
    val gradientPath = Path()

    temperatures.forEachIndexed { index, temp ->
        val x = index * stepX
        val y = if (tempRange > 0) {
            height - ((temp - minTemp) / tempRange * height).toFloat()
        } else {
            height / 2
        }

        if (index == 0) {
            path.moveTo(x, y)
            gradientPath.moveTo(x, height)
            gradientPath.lineTo(x, y)
        } else {
            path.lineTo(x, y)
            gradientPath.lineTo(x, y)
        }
    }

    // Close gradient path
    gradientPath.lineTo(width, height)
    gradientPath.close()

    // Draw gradient fill
    scope.drawPath(
        path = gradientPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFF6B35).copy(alpha = 0.3f),
                Color.Transparent,
            ),
        ),
    )

    // Draw line
    scope.drawPath(
        path = path,
        color = Color(0xFFFF6B35),
        style = Stroke(width = 6f),
    )

    // Draw points
    temperatures.forEachIndexed { index, temp ->
        val x = index * stepX
        val y = if (tempRange > 0) {
            height - ((temp - minTemp) / tempRange * height).toFloat()
        } else {
            height / 2
        }

        scope.drawCircle(
            color = Color(0xFFFF6B35),
            radius = 8f,
            center = Offset(x, y),
        )
        scope.drawCircle(
            color = Color.White,
            radius = 4f,
            center = Offset(x, y),
        )
    }
}

private fun drawWeatherPieChart(
    scope: DrawScope,
    data: Map<String, Int>,
) {
    val total = data.values.sum().toFloat()
    val colors = getWeatherColors()
    var currentAngle = 0f
    val center = Offset(scope.size.width / 2, scope.size.height / 2)
    val radius = minOf(scope.size.width, scope.size.height) / 2 - 40f

    data.entries.forEachIndexed { index, entry ->
        val sweepAngle = (entry.value / total) * 360f

        scope.drawArc(
            color = colors[index % colors.size],
            startAngle = currentAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(
                center.x - radius,
                center.y - radius,
            ),
            size = Size(radius * 2, radius * 2),
        )

        currentAngle += sweepAngle
    }
}

private fun drawMultiParameterChart(
    scope: DrawScope,
    hourlyData: List<HourWeather>,
) {
    val width = scope.size.width
    val height = scope.size.height
    val stepX = width / (hourlyData.size - 1).coerceAtLeast(1)

    // Normalize data to 0-1 range for display
    val humidity = hourlyData.map { it.humidity / 100.0 }
    val windSpeed = hourlyData.map { (it.windspeed / 50.0).coerceAtMost(1.0) } // Max 50 km/h
    val uvIndex = hourlyData.map { (it.uvindex / 12.0).coerceAtMost(1.0) } // Max UV 12
    val cloudCover = hourlyData.map { it.cloudcover / 100.0 }

    val datasets = listOf(
        humidity to Color(0xFF2196F3), // Vivid blue
        windSpeed to Color(0xFF4CAF50), // Vivid green  
        uvIndex to Color(0xFFFF9800), // Vivid orange
        cloudCover to Color(0xFF9C27B0), // Vivid purple
    )

    datasets.forEach { (data, color) ->
        val path = Path()
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - (value * height).toFloat()

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        scope.drawPath(
            path = path,
            color = color,
            style = Stroke(width = 6f), // Thicker lines for better visibility
        )
    }
}

private fun drawSoilAnalysisChart(
    scope: DrawScope,
    hourlyData: List<HourWeather>,
) {
    val width = scope.size.width
    val height = scope.size.height
    val stepX = width / (hourlyData.size - 1).coerceAtLeast(1)

    // Soil temperature (normalized to 0-40°C range)
    val soilTemps = hourlyData.mapNotNull { it.soilTemperature?.let { temp -> (temp / 40.0).coerceIn(0.0, 1.0) } }
    // Soil moisture (already 0-1 range)
    val soilMoisture = hourlyData.mapNotNull { it.soilMoisture }
    // Evapotranspiration (normalized to 0-5mm range)
    val evapotranspiration = hourlyData.mapNotNull { it.evapotranspiration?.let { et -> (et / 5.0).coerceIn(0.0, 1.0) } }

    val datasets = listOf(
        soilTemps to Color(0xFFE53E3E), // Vivid red
        soilMoisture to Color(0xFF2196F3), // Vivid blue
        evapotranspiration to Color(0xFF4CAF50), // Vivid green
    )

    datasets.forEach { (data, color) ->
        if (data.isNotEmpty()) {
            val path = Path()
            data.forEachIndexed { index, value ->
                val x = index * stepX * (hourlyData.size.toFloat() / data.size.toFloat())
                val y = height - (value * height).toFloat()

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            scope.drawPath(
                path = path,
                color = color,
                style = Stroke(width = 4f),
            )
        }
    }
}

private fun getWeatherColors(): List<Color> {
    return listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFFFF9800), // Orange
        Color(0xFF4CAF50), // Green
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFCDDC39), // Lime
    )
}

// Helper function to format hour labels for charts
private fun formatChartHourLabel(datetime: String): String {
    return try {
        val timeStr = datetime.substring(0, 5) // Extract HH:MM
        val parts = timeStr.split(":")
        if (parts.size >= 2) {
            val hour = parts[0].toInt()
            when {
                hour == 0 -> "12AM"
                hour < 12 -> "${hour}AM" 
                hour == 12 -> "12PM"
                else -> "${hour - 12}PM"
            }
        } else {
            timeStr
        }
    } catch (e: Exception) {
        datetime.substring(0, 2) + "h"
    }
}
