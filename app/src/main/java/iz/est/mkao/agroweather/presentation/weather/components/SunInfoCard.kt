package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.data.model.DayWeather

@Composable
fun SunInfoCard(
    dayWeather: DayWeather,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = stringResource(R.string.sun_weather_info),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = stringResource(R.string.sunrise),
                        tint = colorResource(R.color.sun_sunrise),
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        text = stringResource(R.string.sunrise),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = dayWeather.sunrise.substring(0, 5),
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.WbTwilight,
                        contentDescription = stringResource(R.string.sunset),
                        tint = colorResource(R.color.sun_sunset),
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        text = stringResource(R.string.sunset),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = dayWeather.sunset.substring(0, 5),
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = stringResource(R.string.solar_energy),
                        tint = colorResource(R.color.sun_solar_energy),
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        text = stringResource(R.string.solar_energy),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${String.format("%.1f", dayWeather.solarenergy)} MJ",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
