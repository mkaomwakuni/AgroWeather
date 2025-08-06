package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Eco

import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.data.model.DayWeather
import iz.est.mkao.agroweather.data.model.HourWeather
import iz.est.mkao.agroweather.domain.model.IrrigationSuitability
import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@Composable
private fun formatHourTime(datetime: String): String {
    val context = LocalContext.current
    return try {

        val timeStr = when {
            // If it contains 'T' (ISO format like "2025-01-15T07:00:00")
            datetime.contains("T") -> {
                val timePart = datetime.split("T")[1]
                timePart.substring(0, 5) // Get HH:MM part
            }
            // If it's already in HH:MM format
            datetime.contains(":") && datetime.length <= 8 -> {
                datetime.substring(0, 5)
            }
            // If it's just hour format like "07"
            datetime.length <= 2 -> {
                "${datetime.padStart(2, '0')}:00"
            }
            else -> {
                // Fallback: try to find time pattern
                val timePattern = Regex("""(\d{1,2}):(\d{2})""")
                val match = timePattern.find(datetime)
                match?.value ?: datetime.take(5)
            }
        }
        
        val parts = timeStr.split(":")
        if (parts.size >= 2) {
            val hour = parts[0].toInt()
            when {
                hour == 0 -> "12${context.getString(R.string.am_format)}"
                hour < 12 -> "${hour}${context.getString(R.string.am_format)}" 
                hour == 12 -> "12${context.getString(R.string.pm_format)}"
                else -> "${hour - 12}${context.getString(R.string.pm_format)}"
            }
        } else {
            timeStr
        }
    } catch (e: Exception) {
        // Fallback: try to extract just hour if possible
        try {
            val hour = datetime.filter { it.isDigit() }.take(2).toIntOrNull() ?: 0
            when {
                hour == 0 -> "12${context.getString(R.string.am_format)}"
                hour < 12 -> "${hour}${context.getString(R.string.am_format)}"
                hour == 12 -> "12${context.getString(R.string.pm_format)}"
                hour > 12 -> "${hour - 12}${context.getString(R.string.pm_format)}"
                else -> "${hour}${context.getString(R.string.am_format)}"
            }
        } catch (ex: Exception) {
            "N/A"
        }
    }
}

// Vivid color functions for better visibility
@Composable
private fun getTemperatureColor(temp: Double): Color {
    return when {
        temp >= 35 -> colorResource(R.color.temp_hot_red)
        temp >= 30 -> colorResource(R.color.temp_orange_red)
        temp >= 25 -> colorResource(R.color.temp_orange)
        temp >= 20 -> colorResource(R.color.temp_orange_golden)
        temp >= 15 -> colorResource(R.color.temp_green)
        temp >= 10 -> colorResource(R.color.temp_teal)
        temp >= 5 -> colorResource(R.color.temp_blue)
        else -> colorResource(R.color.temp_very_cold)
    }
}

@Composable
private fun getHumidityColor(humidity: Double): Color {
    return when {
        humidity >= 80 -> colorResource(R.color.humidity_background_deep)
        humidity >= 60 -> colorResource(R.color.humidity_background_purple)
        humidity >= 40 -> colorResource(R.color.humidity_background_light)
        else -> colorResource(R.color.humidity_background_gray)
    }
}

@Composable
private fun getWindSpeedColor(windSpeed: Double): Color {
    return when {
        windSpeed >= 25 -> colorResource(R.color.wind_high_red)
        windSpeed >= 15 -> colorResource(R.color.wind_orange)
        windSpeed >= 10 -> colorResource(R.color.wind_green)
        windSpeed >= 5 -> colorResource(R.color.wind_blue)
        else -> colorResource(R.color.wind_calm)
    }
}

private fun getWindDirectionArrow(degrees: Double): String {
    return when {
        degrees >= 337.5 || degrees < 22.5 -> "↑" // North
        degrees < 67.5 -> "↗" // Northeast
        degrees < 112.5 -> "→" // East
        degrees < 157.5 -> "↘" // Southeast
        degrees < 202.5 -> "↓" // South
        degrees < 247.5 -> "↙" // Southwest
        degrees < 292.5 -> "←" // West
        degrees < 337.5 -> "↖" // Northwest
        else -> "↑"
    }
}

private fun getWeatherEmoji(iconCode: String): String {
    return when (iconCode) {
        "clear-day" -> "☀️"
        "clear-night" -> "🌙"
        "rain", "showers-day", "showers-night" -> "🌧️"
        "snow", "snow-showers-day", "snow-showers-night" -> "❄️"
        "wind" -> "💨"
        "fog" -> "🌫️"
        "cloudy" -> "☁️"
        "partly-cloudy-day" -> "⛅"
        "partly-cloudy-night" -> "☁️"
        "overcast" -> "☁️"
        "thunderstorms" -> "⛈️"
        "hail" -> "🧊"
        "sleet" -> "🌨️"
        "drizzle" -> "🌦️"
        else -> "☀️"
    }
}

@Composable
private fun getCurrentDayName(): String {
    val context = LocalContext.current
    val days = arrayOf(
        context.getString(R.string.sunday), context.getString(R.string.monday), context.getString(R.string.tuesday), 
        context.getString(R.string.wednesday), context.getString(R.string.thursday), context.getString(R.string.friday), 
        context.getString(R.string.saturday)
    )
    val calendar = java.util.Calendar.getInstance()
    return days[calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1]
}

@Composable
private fun getCurrentDate(): String {
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    val months = arrayOf(
        context.getString(R.string.january), context.getString(R.string.february), context.getString(R.string.march),
        context.getString(R.string.april), context.getString(R.string.may), context.getString(R.string.june),
        context.getString(R.string.july), context.getString(R.string.august), context.getString(R.string.september),
        context.getString(R.string.october), context.getString(R.string.november), context.getString(R.string.december)
    )
    val month = months[calendar.get(java.util.Calendar.MONTH)]
    return "$day $month"
}

@Composable
fun IrrigationDaysCard(
    hours: List<HourWeather>,
    modifier: Modifier = Modifier,
) {

    val morningHours = hours.filter { 
        val hour = extractHourFromDatetime(it.datetime)
        hour in 6..10
    }
    val eveningHours = hours.filter { 
        val hour = extractHourFromDatetime(it.datetime)
        hour in 17..19
    }
    
    val bestIrrigationTime = findBestIrrigationTime(hours)
    val irrigationScore = calculateIrrigationScore(hours)
    val recommendations = generateIrrigationRecommendations(hours)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                irrigationScore >= 8 -> colorResource(R.color.irrigation_excellent_bg)
                irrigationScore >= 6 -> colorResource(R.color.irrigation_good_bg)
                else -> colorResource(R.color.irrigation_poor_bg)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "💧",
                    fontSize = 24.sp,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.irrigation_analysis),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${stringResource(R.string.today)} • ${getCurrentDate()}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(
                            color = when {
                                irrigationScore >= 8 -> Color(0xFF4CAF50)
                                irrigationScore >= 6 -> Color(0xFFFF9800)
                                else -> Color(0xFFE53E3E)
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${irrigationScore}/10",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Best time recommendation
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🕐",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.best_time),
                            fontSize = 14.sp,
                            color = colorResource(R.color.blue_background_alpha),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = bestIrrigationTime,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Key metrics grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IrrigationMetricCard(
                    title = stringResource(R.string.soil_moisture),
                    value = "${((hours.firstOrNull()?.soilMoisture ?: 0.3) * 100).roundToInt()}%",
                    status = when {
                        (hours.firstOrNull()?.soilMoisture ?: 0.3) < 0.3 -> stringResource(R.string.low)
                        (hours.firstOrNull()?.soilMoisture ?: 0.3) < 0.6 -> stringResource(R.string.good)
                        else -> stringResource(R.string.high)
                    },
                    modifier = Modifier.weight(1f)
                )
                
                IrrigationMetricCard(
                    title = stringResource(R.string.wind_speed),
                    value = "${hours.firstOrNull()?.windspeed?.roundToInt() ?: 0} km/h",
                    status = when {
                        (hours.firstOrNull()?.windspeed ?: 0.0) < 15 -> stringResource(R.string.good)
                        (hours.firstOrNull()?.windspeed ?: 0.0) < 25 -> stringResource(R.string.fair)
                        else -> stringResource(R.string.high)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IrrigationMetricCard(
                    title = "Rain Chance",
                    value = "${hours.firstOrNull()?.precipprob?.roundToInt() ?: 0}%",
                    status = when {
                        (hours.firstOrNull()?.precipprob ?: 0.0) < 20 -> "Good"
                        (hours.firstOrNull()?.precipprob ?: 0.0) < 50 -> "Fair"
                        else -> "Poor"
                    },
                    modifier = Modifier.weight(1f)
                )
                
                IrrigationMetricCard(
                    title = "Temperature",
                    value = "${hours.firstOrNull()?.temp?.roundToInt() ?: 0}°C",
                    status = when {
                        (hours.firstOrNull()?.temp ?: 0.0) in 15.0..30.0 -> "Good"
                        (hours.firstOrNull()?.temp ?: 0.0) in 10.0..35.0 -> "Fair"
                        else -> "Poor"
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendations
            Text(
                text = "Recommendations",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            recommendations.forEach { recommendation ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = recommendation,
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun IrrigationMetricCard(
    title: String,
    value: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                "Good" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                "Fair" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                else -> Color(0xFFE53E3E).copy(alpha = 0.1f)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = when (status) {
                    "Good" -> Color(0xFF4CAF50)
                    "Fair" -> Color(0xFFFF9800)
                    else -> Color(0xFFE53E3E)
                }
            )
        }
    }
}

// Helper functions for irrigation analysis
private fun extractHourFromDatetime(datetime: String): Int {
    return try {
        when {
            datetime.contains("T") -> {
                val timePart = datetime.split("T")[1]
                timePart.substring(0, 2).toInt()
            }
            datetime.contains(":") -> {
                datetime.substring(0, 2).toInt()
            }
            else -> datetime.take(2).toIntOrNull() ?: 0
        }
    } catch (e: Exception) {
        0
    }
}

@Composable
private fun findBestIrrigationTime(hours: List<HourWeather>): String {
    val context = LocalContext.current
    val morningScore = hours.filter { 
        extractHourFromDatetime(it.datetime) in 6..10 
    }.map { calculateHourIrrigationScore(it) }.maxOrNull() ?: 0

    val eveningScore = hours.filter { 
        extractHourFromDatetime(it.datetime) in 17..19 
    }.map { calculateHourIrrigationScore(it) }.maxOrNull() ?: 0

    return when {
        morningScore > eveningScore -> context.getString(R.string.early_morning_6_10_am)
        eveningScore > morningScore -> context.getString(R.string.evening_5_7_pm)
        morningScore > 6 -> context.getString(R.string.early_morning_6_10_am)
        eveningScore > 6 -> context.getString(R.string.evening_5_7_pm)
        else -> context.getString(R.string.avoid_today)
    }
}

private fun calculateIrrigationScore(hours: List<HourWeather>): Int {
    if (hours.isEmpty()) return 0
    
    val avgHour = hours.take(8).map { calculateHourIrrigationScore(it) }.average()
    return avgHour.roundToInt().coerceIn(0, 10)
}

private fun calculateHourIrrigationScore(hour: HourWeather): Int {
    var score = 10
    
    // Soil moisture penalty
    val soilMoisture = hour.soilMoisture ?: 0.3
    if (soilMoisture > 0.7) score -= 4
    else if (soilMoisture > 0.5) score -= 2
    else if (soilMoisture < 0.2) score += 1
    
    // Wind penalty
    if (hour.windspeed > 25) score -= 3
    else if (hour.windspeed > 15) score -= 1
    
    // Rain penalty
    if (hour.precipprob > 50) score -= 4
    else if (hour.precipprob > 30) score -= 2
    else if (hour.precipprob > 15) score -= 1
    
    // Temperature penalty
    if (hour.temp > 35 || hour.temp < 5) score -= 3
    else if (hour.temp > 30 || hour.temp < 10) score -= 1
    
    return score.coerceIn(0, 10)
}

private fun generateIrrigationRecommendations(hours: List<HourWeather>): List<String> {
    val recommendations = mutableListOf<String>()
    val firstHour = hours.firstOrNull() ?: return listOf("No data available")
    
    val soilMoisture = firstHour.soilMoisture ?: 0.3
    val precipProb = firstHour.precipprob
    val windSpeed = firstHour.windspeed
    val temp = firstHour.temp
    
    when {
        soilMoisture > 0.7 -> recommendations.add("Soil is saturated. Delay irrigation to prevent waterlogging.")
        soilMoisture < 0.2 -> recommendations.add("Soil is very dry. Consider immediate irrigation.")
        soilMoisture < 0.4 -> recommendations.add("Soil moisture is low. Good time for irrigation.")
        else -> recommendations.add("Soil moisture is adequate. Monitor before irrigating.")
    }
    
    when {
        precipProb > 50 -> recommendations.add("High rain probability. Postpone irrigation.")
        precipProb > 30 -> recommendations.add("Moderate rain chance. Monitor weather closely.")
        precipProb < 10 -> recommendations.add("Low rain probability. Safe to irrigate.")
    }
    
    when {
        windSpeed > 25 -> recommendations.add("High winds. Avoid irrigation to prevent drift.")
        windSpeed > 15 -> recommendations.add("Moderate winds. Use lower pressure if irrigating.")
        else -> recommendations.add("Wind conditions are favorable for irrigation.")
    }
    
    when {
        temp > 35 -> recommendations.add("Very hot. Irrigate early morning or evening only.")
        temp > 30 -> recommendations.add("Hot weather. Avoid midday irrigation.")
        temp < 5 -> recommendations.add("Too cold. Delay irrigation until warmer.")
        else -> recommendations.add("Temperature is suitable for irrigation.")
    }
    
    // Add timing recommendations
    val currentHour = extractHourFromDatetime(hours.firstOrNull()?.datetime ?: "06:00")
    when {
        currentHour in 6..10 -> recommendations.add("Current time is ideal for morning irrigation.")
        currentHour in 11..16 -> recommendations.add("Consider waiting for evening (5-7 PM) to irrigate.")
        currentHour in 17..19 -> recommendations.add("Good evening time for irrigation.")
        else -> recommendations.add("Plan irrigation for tomorrow morning (6-10 AM).")
    }
    
    return recommendations.take(4) // Limit to 4 recommendations
}

@Composable
fun HourlyForecastSection(
    hours: List<HourWeather>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.hourly_forecast),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // New detailed hourly card
        DetailedHourlyWeatherCard(
            hours = hours.take(8),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Irrigation recommendations for today
        IrrigationDaysCard(
            hours = hours,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun DetailedHourlyWeatherCard(
    hours: List<HourWeather>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with current day/date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = getWeatherEmoji(hours.firstOrNull()?.icon ?: "clear-day"),
                    fontSize = 20.sp,
                    modifier = Modifier.size(24.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = getCurrentDayName(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = " · ${getCurrentDate()}",
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }
            
            // Column Headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hour", fontSize = 14.sp, color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Rain.", fontSize = 14.sp, color = Color.Gray,
                    modifier = Modifier.weight(0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "Temp.", fontSize = 14.sp, color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "Hum.", fontSize = 14.sp, color = Color.Gray,
                    modifier = Modifier.weight(0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "Wind/Gust", fontSize = 14.sp, color = Color.Gray,
                    modifier = Modifier.weight(1.2f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
            
            // Weather data rows
            hours.forEach { hour ->
                DetailedWeatherRow(hour)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun DetailedWeatherRow(hour: HourWeather) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Time and icon
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatHourTime(hour.datetime),
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(55.dp)
            )
            Text(
                text = getWeatherEmoji(hour.icon),
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Rain probability with vivid blue background
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .weight(0.8f)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (hour.precipprob > 5) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF2196F3), // Vivid blue
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${hour.precipprob.roundToInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = "0%",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        // Temperature with gradient background
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .background(
                        color = getTemperatureColor(hour.temp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${hour.temp.roundToInt()}°",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        // Humidity with vivid purple background
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .weight(0.8f)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .background(
                        color = getHumidityColor(hour.humidity),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${hour.humidity.roundToInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        // Wind with direction, speed, and gust
        Row(
            modifier = Modifier.weight(1.2f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getWindDirectionArrow(hour.winddir),
                fontSize = 16.sp,
                color = getWindSpeedColor(hour.windspeed),
                modifier = Modifier.padding(end = 4.dp)
            )
            Column {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .background(
                            color = getWindSpeedColor(hour.windspeed).copy(alpha = 0.2f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${hour.windspeed.roundToInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = getWindSpeedColor(hour.windspeed)
                    )
                }
                // Wind gust
                if (hour.windgust > hour.windspeed + 5) {  // Only show if gust is significantly higher
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .background(
                                color = getWindSpeedColor(hour.windgust).copy(alpha = 0.1f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "G${hour.windgust.roundToInt()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = getWindSpeedColor(hour.windgust)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactHourlyForecastCard(
    hour: HourWeather,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(85.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = formatHourTime(hour.datetime),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = getWeatherEmoji(hour.icon),
                fontSize = 24.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .background(
                            color = getTemperatureColor(hour.temp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${hour.temp.roundToInt()}°",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                if (hour.precipprob > 10) {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${hour.precipprob.roundToInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2196F3),
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun ExtendedForecastGrid(
    forecast: List<DayWeather>,
    irrigationSuitabilities: List<IrrigationSuitability>,
    modifier: Modifier = Modifier,
    onDayClick: (DayWeather) -> Unit = {},
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.agricultural_forecast) + " (16-Day)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            forecast.take(16).zip(irrigationSuitabilities.take(16)).forEach { (day, irrigation) ->
                AgriculturalDayCard(
                    day = day,
                    irrigationSuitability = irrigation,
                    onClick = { onDayClick(day) },
                )
            }
        }
    }
}

// Beautiful green color functions for agricultural data
private fun getSoilMoistureColor(moisture: Double): Color {
    return when {
        moisture >= 0.8 -> Color(0xFF1565C0) // Deep blue for very wet
        moisture >= 0.6 -> Color(0xFF2E7D32) // Forest green for good
        moisture >= 0.4 -> Color(0xFF66BB6A) // Light green for moderate
        moisture >= 0.2 -> Color(0xFFFFB74D) // Orange for low
        else -> Color(0xFFD32F2F) // Red for very dry
    }
}

private fun getSolarRadiationColor(radiation: Double): Color {
    return when {
        radiation >= 800 -> Color(0xFFFF6F00) // Deep orange for very high
        radiation >= 600 -> Color(0xFFFFB300) // Golden for high
        radiation >= 400 -> Color(0xFFFDD835) // Yellow for moderate
        radiation >= 200 -> Color(0xFFAED581) // Light green for low
        else -> Color(0xFF78909C) // Gray for very low
    }
}

// Helper data class for spray conditions
data class SprayCondition(
    val status: String,
    val reason: String
)

// Calculate spray conditions for forecast
private fun calculateSprayCondition(day: DayWeather): SprayCondition {
    val windSpeed = day.windspeed
    val rainChance = day.precipprob
    val temperature = day.temp
    val humidity = day.humidity
    
    return when {
        // Poor conditions
        windSpeed > 25 -> SprayCondition("Poor", "High winds")
        rainChance > 70 -> SprayCondition("Poor", "Rain expected")
        temperature > 35 -> SprayCondition("Poor", "Too hot")
        temperature < 5 -> SprayCondition("Poor", "Too cold")
        
        // Fair conditions
        windSpeed > 15 -> SprayCondition("Fair", "Moderate winds")
        rainChance > 40 -> SprayCondition("Fair", "Rain possible")
        temperature > 30 -> SprayCondition("Fair", "Hot weather")
        temperature < 10 -> SprayCondition("Fair", "Cool weather")
        humidity < 30 -> SprayCondition("Fair", "Low humidity")
        
        // Good conditions
        windSpeed <= 10 && rainChance <= 20 && temperature in 15.0..28.0 -> 
            SprayCondition("Excellent", "Ideal conditions")
        windSpeed <= 15 && rainChance <= 30 -> 
            SprayCondition("Good", "Favorable conditions")
            
        // Default good
        else -> SprayCondition("Good", "Suitable")
    }
}

private fun getSprayConditionColor(status: String): Color {
    return when (status) {
        "Excellent" -> Color(0xFF4CAF50) // Bright green for excellent
        "Good" -> Color(0xFF66BB6A) // Green for good
        "Fair" -> Color(0xFFFF9800) // Orange for fair
        "Poor" -> Color(0xFFD32F2F) // Red for poor
        else -> Color(0xFF9E9E9E) // Gray for unknown
    }
}

private fun getSoilTemperatureColor(temp: Double): Color {
    return when {
        temp >= 35 -> Color(0xFFD32F2F) // Red for too hot
        temp >= 25 -> Color(0xFF66BB6A) // Green for optimal
        temp >= 15 -> Color(0xFF81C784) // Light green for good
        temp >= 5 -> Color(0xFF42A5F5) // Blue for cool
        else -> Color(0xFF1976D2) // Deep blue for cold
    }
}

@Composable
private fun getAgriculturalIcon(condition: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        condition.contains("clear", ignoreCase = true) -> Icons.Default.WbSunny
        condition.contains("rain", ignoreCase = true) -> Icons.Default.WaterDrop
        condition.contains("cloudy", ignoreCase = true) -> Icons.Default.Cloud
        condition.contains("storm", ignoreCase = true) -> Icons.Default.Thunderstorm
        condition.contains("snow", ignoreCase = true) -> Icons.Default.AcUnit
        condition.contains("wind", ignoreCase = true) -> Icons.Default.Air
        condition.contains("fog", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.Eco
    }
}

@Composable
fun AgriculturalDayCard(
    day: DayWeather,
    irrigationSuitability: IrrigationSuitability,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val cardBackgroundColor = when {
        irrigationSuitability.isSuitable -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with date and irrigation status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = try {
                            LocalDate.parse(day.datetime).format(
                                DateTimeFormatter.ofPattern("EEEE, MMM d"),
                            )
                        } catch (e: Exception) {
                            day.datetime
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = day.conditions,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Irrigation priority indicator
                Box(
                    modifier = Modifier
                        .background(
                            color = if (irrigationSuitability.isSuitable) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (irrigationSuitability.isSuitable) Icons.Default.WaterDrop else Icons.Default.Close,
                            contentDescription = "Irrigation Status",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (irrigationSuitability.isSuitable) 
                            stringResource(R.string.irrigate) 
                        else 
                            stringResource(R.string.skip_irrigation),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weather summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getAgriculturalIcon(day.conditions),
                        contentDescription = day.conditions,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = getTemperatureColor(day.temp),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${day.temp.roundToInt()}°C",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                        if (day.precipprob > 10) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${day.precipprob.roundToInt()}% chance",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }

                // UV Index and Solar Info
                Column(horizontalAlignment = Alignment.End) {
                    if (day.uvindex > 3) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFFB300),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "☀️ UV ${day.uvindex.roundToInt()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    }
                    if (day.solarradiation > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🌞 ${day.solarradiation.roundToInt()} W/m²",
                            fontSize = 10.sp,
                            color = getSolarRadiationColor(day.solarradiation),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Agricultural metrics grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Soil Moisture
                AgriculturalMetricCard(
                    title = "Soil Moisture",
                    value = "${((day.soilMoisture ?: 0.45) * 100).roundToInt()}%",
                    iconVector = Icons.Default.WaterDrop,
                    color = getSoilMoistureColor(day.soilMoisture ?: 0.45),
                    modifier = Modifier.weight(1f)
                )

                // Soil Temperature
                AgriculturalMetricCard(
                    title = "Soil Temp",
                    value = "${(day.soilTemperature ?: (day.temp - 2)).roundToInt()}°C",
                    iconVector = Icons.Default.Thermostat,
                    color = getSoilTemperatureColor(day.soilTemperature ?: (day.temp - 2)),
                    modifier = Modifier.weight(1f)
                )

                // Wind Conditions
                AgriculturalMetricCard(
                    title = "Wind",
                    value = "${day.windspeed.roundToInt()} km/h",
                    iconVector = Icons.Default.Air,
                    color = getWindSpeedColor(day.windspeed),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Spray Conditions - based on weather
                val sprayCondition = calculateSprayCondition(day)
                AgriculturalMetricCard(
                    title = "Spray Condition",
                    value = sprayCondition.status,
                    iconVector = Icons.Default.Eco,
                    color = getSprayConditionColor(sprayCondition.status),
                    modifier = Modifier.weight(1f)
                )

                // Humidity
                AgriculturalMetricCard(
                    title = "Humidity",
                    value = "${day.humidity.roundToInt()}%",
                    iconVector = Icons.Default.Opacity,
                    color = getHumidityColor(day.humidity),
                    modifier = Modifier.weight(1f)
                )


                // Use real API sunshine duration data, fallback to calculation
                val sunshineHours = if (day.sunshineDuration != null && day.sunshineDuration > 0) {
                    String.format("%.1f", day.sunshineDuration / 3600) // Convert seconds to hours
                } else {
                    calculateSunshineHours(day.sunrise, day.sunset)
                }
                AgriculturalMetricCard(
                    title = "Sunshine",
                    value = "${sunshineHours}h",
                    iconVector = Icons.Default.WbSunny,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )

                // Dew point for disease risk
                AgriculturalMetricCard(
                    title = "Dew Point",
                    value = "${day.dew.roundToInt()}°C",
                    iconVector = Icons.Default.WaterDrop,
                    color = if (day.dew > 15) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Irrigation recommendations snippet
            if (irrigationSuitability.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.agricultural_insights),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = irrigationSuitability.recommendations.take(2).joinToString(" • "),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AgriculturalMetricCard(
    title: String,
    value: String,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = title,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Text(
                text = title,
                fontSize = 9.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}




private fun calculateSunshineHours(sunrise: String, sunset: String): String {
    return try {
        // Handle different time formats
        val cleanSunrise = when {
            sunrise.contains("T") -> sunrise.split("T")[1].take(5)
            sunrise.contains(":") -> sunrise.take(5)
            else -> "06:30" // Realistic sunrise fallback
        }
        
        val cleanSunset = when {
            sunset.contains("T") -> sunset.split("T")[1].take(5)  
            sunset.contains(":") -> sunset.take(5)
            else -> "18:30" // Realistic sunset fallback
        }
        
        val sunriseTime = java.time.LocalTime.parse(cleanSunrise)
        val sunsetTime = java.time.LocalTime.parse(cleanSunset)
        val duration = java.time.Duration.between(sunriseTime, sunsetTime)
        "${duration.toHours()}.${(duration.toMinutes() % 60) / 6}"
    } catch (e: Exception) {
        // Realistic varying fallback based on season (approximate)
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        val seasonalHours = when (currentMonth) {
            12, 1, 2 -> "10.5" // Winter - shorter days
            3, 4, 5 -> "12.0"  // Spring - moderate days  
            6, 7, 8 -> "13.5"  // Summer - longer days
            9, 10, 11 -> "11.5" // Fall - moderate days
            else -> "12.0"
        }
        seasonalHours
    }
}
