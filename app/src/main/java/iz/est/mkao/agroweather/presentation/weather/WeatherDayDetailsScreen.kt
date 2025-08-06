package iz.est.mkao.agroweather.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.data.model.DayWeather
import iz.est.mkao.agroweather.presentation.weather.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDayDetailsScreen(
    navController: NavController,
    dayWeather: DayWeather,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = try {
                                LocalDate.parse(dayWeather.datetime).format(
                                    DateTimeFormatter.ofPattern("EEEE, MMMM d"),
                                )
                            } catch (e: Exception) {
                                dayWeather.datetime
                            },
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = dayWeather.conditions,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                WeatherOverviewCard(dayWeather = dayWeather)
            }

            item {
                Text(
                    text = stringResource(R.string.weather_details),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                WeatherMetricsGrid(dayWeather = dayWeather)
            }

            item {
                AgriculturalInfoSection(dayWeather = dayWeather)
            }

            if (dayWeather.hours.isNotEmpty()) {
                item {
                    HourlyForecastCard(hours = dayWeather.hours)
                }
            }

            item {
                SunInfoCard(dayWeather = dayWeather)
            }

            item {
                SmartFarmingCard(
                    dayWeather = dayWeather,
                    onNavigateToChat = { suggestion ->
                        navController.navigate(
                            "chat?suggestion=${java.net.URLEncoder.encode(suggestion, "UTF-8")}&cityName=&latitude=0.0&longitude=0.0"
                        )
                    }
                )
            }
                }
    }
}

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

