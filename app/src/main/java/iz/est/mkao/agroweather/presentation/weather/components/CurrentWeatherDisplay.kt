package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.data.model.CurrentConditions
import iz.est.mkao.agroweather.domain.model.WeatherThemeColors
import iz.est.mkao.agroweather.util.TemperatureUtils
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherDisplay(
    currentConditions: CurrentConditions,
    location: String,
    temperatureUtils: TemperatureUtils,
    weatherColors: WeatherThemeColors = WeatherThemeColors(
        primary = Color.White,
        secondary = Color.White,
        accent = Color.White
    ),
    modifier: Modifier = Modifier,
) {
    val lastUpdated = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        Column {
            // Main temperature
            Row(
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = temperatureUtils.convertTemperature(currentConditions.temp).roundToInt().toString(),
                    color = weatherColors.primary,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 90.sp,
                )
                Text(
                    text = "°",
                    color = weatherColors.primary,
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Weather condition
            Text(
                text = currentConditions.conditions,
                color = weatherColors.secondary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp),
            )

            Text(
                text = "Feels like ${temperatureUtils.formatTemperature(currentConditions.feelslike)}",
                color = weatherColors.accent.copy(alpha = 0.9f),
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Additional weather information
            WeatherAdditionalInfo(
                currentConditions = currentConditions,
                lastUpdated = lastUpdated,
                weatherColors = weatherColors,
            )
        }

        // Large weather icon positioned independently
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopEnd)
                .offset(x = 220.dp, y = (-70).dp),
        ) {
            WeatherIcon(
                iconCode = currentConditions.icon,
                modifier = Modifier.size(350.dp),
            )
        }
    }
}

@Composable
private fun WeatherAdditionalInfo(
    currentConditions: CurrentConditions,
    lastUpdated: String,
    weatherColors: WeatherThemeColors,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Last updated: $lastUpdated",
            color = weatherColors.accent.copy(alpha = 0.8f),
            fontSize = 14.sp,
        )

        // Agricultural data row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            currentConditions.soilmoisture?.let { soilMoisture ->
                Text(
                    text = "Soil ${(soilMoisture * 100).roundToInt()}%",
                    color = weatherColors.secondary.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                )
            } ?: Text(
                text = "Soil N/A",
                color = weatherColors.secondary.copy(alpha = 0.8f),
                fontSize = 14.sp,
            )

            currentConditions.evapotranspiration?.let { et ->
                Text(
                    text = "ET ${String.format("%.1f", et)}mm",
                    color = weatherColors.secondary.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                )
            } ?: Text(
                text = "ET N/A",
                color = weatherColors.secondary.copy(alpha = 0.8f),
                fontSize = 14.sp,
            )
        }

        // Weather essentials row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Pressure ${currentConditions.pressure.roundToInt()} hPa",
                color = weatherColors.accent.copy(alpha = 0.8f),
                fontSize = 14.sp,
            )
            Text(
                text = "Dew Point ${currentConditions.dew.roundToInt()}°C",
                color = weatherColors.accent.copy(alpha = 0.8f),
                fontSize = 14.sp,
            )
        }
    }
}
