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
import androidx.compose.ui.platform.LocalContext
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
fun WeatherMetricsGrid(
    dayWeather: DayWeather,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.height(450.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            DetailCard(
                title = stringResource(R.string.humidity),
                value = "${dayWeather.humidity.roundToInt()}%",
                icon = Icons.Default.WaterDrop,
                subtitle = getHumidityStatus(dayWeather.humidity),
            )
        }
        item {
            DetailCard(
                title = stringResource(R.string.wind_speed),
                value = "${dayWeather.windspeed.roundToInt()} km/h",
                icon = Icons.Default.Air,
                subtitle = getWindDirection(dayWeather.winddir),
            )
        }
        item {
            DetailCard(
                title = stringResource(R.string.pressure),
                value = "${dayWeather.pressure.roundToInt()} hPa",
                icon = Icons.Default.MonitorHeart,
                subtitle = getPressureStatus(dayWeather.pressure),
            )
        }
        item {
            DetailCard(
                title = stringResource(R.string.uv_index),
                value = "${dayWeather.uvindex.roundToInt()}",
                icon = Icons.Default.Shield,
                subtitle = getUVStatus(dayWeather.uvindex),
            )
        }

        item {
            DetailCard(
                title = stringResource(R.string.precipitation),
                value = "${String.format("%.1f", dayWeather.precip)} mm",
                icon = Icons.Default.Grain,
                subtitle = "${dayWeather.precipprob.roundToInt()}% chance",
            )
        }
        item {
            DetailCard(
                title = stringResource(R.string.wind_direction),
                value = "${dayWeather.winddir.roundToInt()}°",
                icon = Icons.Default.Navigation,
                subtitle = getWindDirection(dayWeather.winddir),
            )
        }
        item {
            DetailCard(
                title = stringResource(R.string.solar_radiation),
                value = "${dayWeather.solarradiation.roundToInt()} W/m²",
                icon = Icons.Default.WbSunny,
                subtitle = getSolarStatus(dayWeather.solarradiation),
            )
        }
        item {
            DetailCard(
                title = stringResource(R.string.solar_energy),
                value = "${String.format("%.1f", dayWeather.solarenergy)} MJ/m²",
                icon = Icons.Default.LightMode,
                subtitle = stringResource(R.string.daily_total),
            )
        }
        if (dayWeather.snow > 0) {
            item {
                DetailCard(
                    title = stringResource(R.string.snow),
                    value = "${String.format("%.1f", dayWeather.snow)} mm",
                    icon = Icons.Default.AcUnit,
                    subtitle = stringResource(R.string.depth_cm, dayWeather.snowdepth),
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
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
                tint = getDetailIconColor(icon),
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
private fun getDetailIconColor(icon: ImageVector): Color {
    return when (icon) {
        Icons.Default.Thermostat -> colorResource(R.color.detail_icon_thermostat)
        Icons.Default.WaterDrop -> colorResource(R.color.detail_icon_water_drop)
        Icons.Default.Air -> colorResource(R.color.detail_icon_air)
        Icons.Default.MonitorHeart -> colorResource(R.color.detail_icon_monitor_heart)
        Icons.Default.Shield -> colorResource(R.color.detail_icon_shield)
        Icons.Default.Grain -> colorResource(R.color.detail_icon_grain)

        Icons.Default.Navigation -> colorResource(R.color.detail_icon_navigation)
        Icons.Default.WbSunny -> colorResource(R.color.detail_icon_wb_sunny)
        Icons.Default.LightMode -> colorResource(R.color.detail_icon_light_mode)
        Icons.Default.AcUnit -> colorResource(R.color.detail_icon_ac_unit)
        else -> colorResource(R.color.detail_icon_default)
    }
}

@Composable
private fun getHumidityStatus(humidity: Double): String {
    return when {
        humidity < 30 -> stringResource(R.string.humidity_low_dry)
        humidity < 60 -> stringResource(R.string.humidity_comfortable)
        humidity < 80 -> stringResource(R.string.humidity_high_humid)
        else -> stringResource(R.string.humidity_very_high)
    }
}

@Composable
private fun getPressureStatus(pressure: Double): String {
    return when {
        pressure < 1000 -> stringResource(R.string.pressure_low_stormy)
        pressure < 1020 -> stringResource(R.string.pressure_normal)
        else -> stringResource(R.string.pressure_high_clear)
    }
}

@Composable
private fun getUVStatus(uvIndex: Double): String {
    return when {
        uvIndex <= 2 -> stringResource(R.string.uv_low)
        uvIndex <= 5 -> stringResource(R.string.uv_moderate)
        uvIndex <= 7 -> stringResource(R.string.uv_high)
        uvIndex <= 10 -> stringResource(R.string.uv_very_high)
        else -> stringResource(R.string.uv_extreme)
    }
}



@Composable
private fun getSolarStatus(solarRadiation: Double): String {
    return when {
        solarRadiation < 200 -> stringResource(R.string.solar_low)
        solarRadiation < 500 -> stringResource(R.string.solar_moderate)
        solarRadiation < 800 -> stringResource(R.string.solar_high)
        else -> stringResource(R.string.solar_peak)
    }
}

@Composable
private fun getWindDirection(degrees: Double): String {
    val context = LocalContext.current
    val directions = arrayOf(
        context.getString(R.string.wind_n), context.getString(R.string.wind_nne), context.getString(R.string.wind_ne), context.getString(R.string.wind_ene),
        context.getString(R.string.wind_e), context.getString(R.string.wind_ese), context.getString(R.string.wind_se), context.getString(R.string.wind_sse),
        context.getString(R.string.wind_s), context.getString(R.string.wind_ssw), context.getString(R.string.wind_sw), context.getString(R.string.wind_wsw),
        context.getString(R.string.wind_w), context.getString(R.string.wind_wnw), context.getString(R.string.wind_nw), context.getString(R.string.wind_nnw)
    )
    val index = ((degrees + 11.25) / 22.5).toInt() % 16
    return directions[index]
}
