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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.data.model.HourWeather
import kotlin.math.roundToInt

@Composable
fun HourlyForecastCard(
    hours: List<HourWeather>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.hourly_forecast),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(hours.take(24)) { hour ->
                    Column(
                        modifier = Modifier
                            .width(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = hour.datetime.substring(0, 5),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        WeatherIconForHourly(
                            iconCode = hour.icon,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${hour.temp.roundToInt()}°",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (hour.precipprob > 0) {
                            Text(
                                text = "${hour.precipprob.roundToInt()}%",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherIconForHourly(iconCode: String, modifier: Modifier = Modifier) {
    val icon = when (iconCode) {
        "clear-day" -> Icons.Default.WbSunny
        "clear-night" -> Icons.Default.WbTwilight
        "rain", "showers-day", "showers-night" -> Icons.Default.Grain
        "snow", "snow-showers-day", "snow-showers-night" -> Icons.Default.Grain
        "wind" -> Icons.Default.Air
        "fog" -> Icons.Default.Visibility
        "cloudy" -> Icons.Default.Cloud
        "partly-cloudy-day" -> Icons.Default.Cloud
        "partly-cloudy-night" -> Icons.Default.Cloud
        "overcast" -> Icons.Default.Cloud
        "thunderstorms" -> Icons.Default.Cloud
        "hail" -> Icons.Default.Grain
        "sleet" -> Icons.Default.Grain
        "drizzle" -> Icons.Default.Grain
        "mist" -> Icons.Default.Visibility
        "haze" -> Icons.Default.Visibility
        "dust" -> Icons.Default.Cloud
        "smoke" -> Icons.Default.Cloud
        "tornado" -> Icons.Default.Air
        "hurricane" -> Icons.Default.Air
        else -> Icons.Default.WbSunny
    }

    val color = when (iconCode) {
        "clear-day" -> colorResource(R.color.icon_clear_day)
        "clear-night" -> colorResource(R.color.icon_clear_night)
        "rain", "showers-day", "showers-night" -> colorResource(R.color.icon_rain)
        "snow", "snow-showers-day", "snow-showers-night" -> colorResource(R.color.icon_snow)
        "wind" -> colorResource(R.color.icon_wind)
        "fog" -> colorResource(R.color.icon_fog)
        "cloudy" -> colorResource(R.color.icon_cloudy)
        "partly-cloudy-day" -> colorResource(R.color.icon_partly_cloudy_day)
        "partly-cloudy-night" -> colorResource(R.color.icon_partly_cloudy_night)
        "overcast" -> colorResource(R.color.icon_cloudy)
        "thunderstorms" -> colorResource(R.color.icon_thunderstorms)
        "hail" -> colorResource(R.color.icon_hail)
        "sleet" -> colorResource(R.color.icon_hail)
        "drizzle" -> colorResource(R.color.icon_drizzle)
        "mist" -> colorResource(R.color.icon_mist)
        "haze" -> colorResource(R.color.icon_haze)
        "dust" -> colorResource(R.color.icon_dust)
        "smoke" -> colorResource(R.color.icon_smoke)
        "tornado" -> colorResource(R.color.icon_tornado)
        "hurricane" -> colorResource(R.color.icon_hurricane)
        else -> colorResource(R.color.icon_clear_day)
    }

    Icon(
        imageVector = icon,
        contentDescription = iconCode,
        modifier = modifier,
        tint = color,
    )
}
