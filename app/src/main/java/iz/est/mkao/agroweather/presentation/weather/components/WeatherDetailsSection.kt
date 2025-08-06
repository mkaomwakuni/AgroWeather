package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import iz.est.mkao.agroweather.data.model.CurrentConditions
import iz.est.mkao.agroweather.domain.usecase.weather.GetWeatherDescriptionsUseCase
import kotlin.math.roundToInt

@Composable
fun WeatherDetailsSection(
    currentConditions: CurrentConditions,
    weatherDescriptionsUseCase: GetWeatherDescriptionsUseCase,
    modifier: Modifier = Modifier,
) {
    val detailCards = remember(currentConditions) {
        buildWeatherDetailCards(currentConditions, weatherDescriptionsUseCase)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.height(400.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(detailCards) { cardData ->
            WeatherDetailCard(
                title = cardData.title,
                value = cardData.value,
                icon = cardData.icon,
                subtitle = cardData.subtitle,
            )
        }
    }
}

@Composable
fun WeatherDetailCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val iconColor = remember(icon) {
        getIconColor(icon)
    }

    Card(
        modifier = modifier.height(if (subtitle != null) 120.dp else 100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                        )
                    }
                }
            }

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier
                    .size(70.dp)
                    .offset(x = 20.dp),
            )
        }
    }
}

private fun getIconColor(icon: ImageVector): Color {
    return when (icon) {
        Icons.Default.Thermostat -> Color(0xFFFF6B35)
        Icons.Default.WaterDrop -> Color(0xFF1E88E5)
        Icons.Default.Air -> Color(0xFF87CEEB)
        Icons.Default.MonitorHeart -> Color(0xFF9C27B0)
        Icons.Default.Shield -> Color(0xFFFFD700)
        Icons.Default.Cloud -> Color(0xFF696969)
        Icons.Default.WbSunny -> Color(0xFFFFA726)
        Icons.Default.LightMode -> Color(0xFFFFEB3B)
        Icons.Default.Opacity -> Color(0xFF26C6DA)
        Icons.Default.Visibility -> Color(0xFF2196F3)
        Icons.Default.CompareArrows -> Color(0xFF42A5F5)
        else -> Color(0xFF6200EE)
    }
}

private data class WeatherDetailCardData(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val subtitle: String?,
)

private fun buildWeatherDetailCards(
    currentConditions: CurrentConditions,
    weatherDescriptionsUseCase: GetWeatherDescriptionsUseCase,
): List<WeatherDetailCardData> {
    return buildList {
        // Temperature & Feel
        add(
            WeatherDetailCardData(
                title = "Feels Like",
                value = "${currentConditions.feelslike.roundToInt()}°C",
                icon = Icons.Default.Thermostat,
                subtitle = "vs ${currentConditions.temp.roundToInt()}°C actual",
            ),
        )

        // Humidity
        val humidityDesc = weatherDescriptionsUseCase.getHumidityDescription(currentConditions.humidity)
        add(
            WeatherDetailCardData(
                title = "Humidity",
                value = "${currentConditions.humidity.roundToInt()}%",
                icon = Icons.Default.WaterDrop,
                subtitle = "${humidityDesc.level} - ${humidityDesc.description}",
            ),
        )

        // Wind
        val windDirection = weatherDescriptionsUseCase.getWindDirection(currentConditions.winddir)
        add(
            WeatherDetailCardData(
                title = "Wind",
                value = "${currentConditions.windspeed.roundToInt()} km/h",
                icon = Icons.Default.Air,
                subtitle = "$windDirection (${currentConditions.winddir.roundToInt()}°)",
            ),
        )

        // Pressure
        val pressureDesc = weatherDescriptionsUseCase.getPressureDescription(currentConditions.pressure)
        add(
            WeatherDetailCardData(
                title = "Pressure",
                value = "${currentConditions.pressure.roundToInt()} hPa",
                icon = Icons.Default.MonitorHeart,
                subtitle = "${pressureDesc.level} - ${pressureDesc.description}",
            ),
        )

        // UV Index
        val uvDesc = weatherDescriptionsUseCase.getUVDescription(currentConditions.uvindex)
        add(
            WeatherDetailCardData(
                title = "UV Index",
                value = "${currentConditions.uvindex.roundToInt()}",
                icon = Icons.Default.Shield,
                subtitle = "${uvDesc.level} - ${uvDesc.description}",
            ),
        )

        // Cloud Cover
        add(
            WeatherDetailCardData(
                title = "Cloud Cover",
                value = "${currentConditions.cloudcover.roundToInt()}%",
                icon = Icons.Default.Cloud,
                subtitle = weatherDescriptionsUseCase.getCloudDescription(currentConditions.cloudcover),
            ),
        )

        // Visibility
        add(
            WeatherDetailCardData(
                title = "Visibility",
                value = "${currentConditions.visibility.roundToInt()} km",
                icon = Icons.Default.Visibility,
                subtitle = weatherDescriptionsUseCase.getVisibilityDescription(currentConditions.visibility),
            ),
        )

        // Dew Point
        add(
            WeatherDetailCardData(
                title = "Dew Point",
                value = "${currentConditions.dew.roundToInt()}°C",
                icon = Icons.Default.Opacity,
                subtitle = weatherDescriptionsUseCase.getDewPointDescription(currentConditions.dew, currentConditions.temp),
            ),
        )

        // Soil Moisture (if available)
        currentConditions.soilmoisture?.let { soilMoisture ->
            val soilDesc = weatherDescriptionsUseCase.getSoilMoistureDescription(soilMoisture)
            add(
                WeatherDetailCardData(
                    title = "Soil Moisture",
                    value = "${(soilMoisture * 100).roundToInt()}%",
                    icon = Icons.Default.WaterDrop,
                    subtitle = "${soilDesc.level} - ${soilDesc.description}",
                ),
            )
        }

        // Evapotranspiration (if available)
        currentConditions.evapotranspiration?.let { et ->
            add(
                WeatherDetailCardData(
                    title = "Evapotranspiration",
                    value = "${String.format("%.1f", et)} mm",
                    icon = Icons.Default.CompareArrows,
                    subtitle = "Water loss rate",
                ),
            )
        }
    }
}
