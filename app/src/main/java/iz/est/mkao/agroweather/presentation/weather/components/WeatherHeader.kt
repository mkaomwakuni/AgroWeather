package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.data.model.City
import iz.est.mkao.agroweather.data.model.CurrentConditions
import iz.est.mkao.agroweather.domain.model.WeatherThemeColors
import iz.est.mkao.agroweather.util.TemperatureUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHeader(
    selectedCity: City?,
    availableCities: List<City>,
    showCityPicker: Boolean,
    isLoadingCities: Boolean,
    currentConditions: CurrentConditions?,
    location: String?,
    temperatureUtils: TemperatureUtils,
    weatherGradient: Brush,
    weatherColors: WeatherThemeColors = WeatherThemeColors(
        primary = Color.White,
        secondary = Color.White,
        accent = Color.White
    ),
    scrollBehavior: TopAppBarScrollBehavior,
    onShowCityPicker: (Boolean) -> Unit,
    onCitySelected: (City) -> Unit,
    onSearchCities: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentDate = remember {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
        today.format(formatter)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(weatherGradient) // Dynamic weather-based gradient
            .statusBarsPadding(), // Extend gradient to status bar
    ) {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = currentDate,
                        color = Color.White,
                        fontSize = 18.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Date picker action */ }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            tint = Color.White,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                        )
                    }
                    Text(
                        text = temperatureUtils.getTemperatureUnit(),
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 12.dp, end = 16.dp),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets(0),
            )

            // City selector
            CitySelector(
                selectedCity = selectedCity,
                availableCities = availableCities,
                showCityPicker = showCityPicker,
                isLoadingCities = isLoadingCities,
                onShowCityPicker = onShowCityPicker,
                onCitySelected = onCitySelected,
                onSearchCities = onSearchCities,
            )

            // Current weather display
            currentConditions?.let { conditions ->
                location?.let { loc ->
                    CurrentWeatherDisplay(
                        currentConditions = conditions,
                        location = loc,
                        temperatureUtils = temperatureUtils,
                        weatherColors = weatherColors,
                    )
                }
            }
        }
    }
}
