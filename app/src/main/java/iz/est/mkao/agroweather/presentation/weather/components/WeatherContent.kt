package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import iz.est.mkao.agroweather.domain.model.WeatherThemeColors
import iz.est.mkao.agroweather.presentation.navigation.DynamicColors.weatherColors
import iz.est.mkao.agroweather.presentation.weather.WeatherUiState
import iz.est.mkao.agroweather.presentation.weather.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(
    uiState: WeatherUiState,
    viewModel: WeatherViewModel,
    navController: NavController,
    weatherGradient: Brush,
    scrollBehavior: TopAppBarScrollBehavior,
    showCityPicker: Boolean,
    onShowCityPicker: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Use the provided weatherColors from WeatherApp
    // (weatherColors parameter is now passed from WeatherApp.kt)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        // Header section
        item {
            WeatherHeader(
                selectedCity = uiState.selectedCity,
                availableCities = uiState.availableCities,
                showCityPicker = showCityPicker,
                isLoadingCities = uiState.loadingState == iz.est.mkao.farmweather.presentation.weather.LoadingState.LoadingCities,
                currentConditions = uiState.weatherData?.currentConditions,
                location = uiState.weatherData?.resolvedAddress,
                temperatureUtils = viewModel.temperatureUtils,
                weatherGradient = weatherGradient,

                scrollBehavior = scrollBehavior,
                onShowCityPicker = onShowCityPicker,
                onCitySelected = { city ->
                    viewModel.selectCity(city)
                    onShowCityPicker(false)
                },
                onSearchCities = { query -> viewModel.searchCities(query) },
                onRefresh = { viewModel.refreshWeatherForSelectedCity() },
            )
        }

        // Weather details section
        item {
            uiState.weatherData?.currentConditions?.let { currentConditions ->
                WeatherDetailsSection(
                    currentConditions = currentConditions,
                    weatherDescriptionsUseCase = viewModel.getWeatherDescriptions(),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        // Enhanced daily conditions section
        item {
            uiState.weatherData?.let { weather ->
                EnhancedDailyConditionsSection(
                    dailyWeather = weather.days,
                    currentConditions = weather.currentConditions,
                    modifier = Modifier.padding(16.dp),
                    onInsightClick = { suggestion ->
                        // Navigate to chat with weather context
                        val cityName = weather.resolvedAddress.split(",").firstOrNull()?.trim() ?: "Current Location"
                        navController.navigate("chat/$suggestion/$cityName/0.0/0.0")
                    },
                )
            }
        }

        // Hourly forecast section  
        item {
            uiState.weatherData?.let { weather ->
                if (weather.days.isNotEmpty() && weather.days[0].hours.isNotEmpty()) {
                    HourlyForecastSection(
                        hours = weather.days[0].hours,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }

        // Soil hourly data section
        item {
            uiState.weatherData?.let { weather ->
                if (weather.days.isNotEmpty() && weather.days[0].hours.isNotEmpty()) {
                    SoilHourlyDataSection(
                        hours = weather.days[0].hours,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }

        // Detailed soil analysis cards
        item {
            uiState.weatherData?.let { weather ->
                if (weather.days.isNotEmpty() && weather.days[0].hours.isNotEmpty()) {
                    SoilDetailedCards(
                        hours = weather.days[0].hours,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        // Weather analysis charts
        item {
            uiState.weatherData?.let { weather ->
                if (weather.days.isNotEmpty() && weather.days[0].hours.isNotEmpty()) {
                    WeatherChartsSection(
                        dailyWeather = weather.days,
                        hourlyWeather = weather.days[0].hours,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        // Soil analysis chart
        item {
            uiState.weatherData?.let { weather ->
                if (weather.days.isNotEmpty() && weather.days[0].hours.isNotEmpty()) {
                    SoilAnalysisChart(
                        hourlyWeather = weather.days[0].hours,
                        modifier = Modifier
                            .height(200.dp)
                            .padding(horizontal = 16.dp),
                    )
                }
            }
        }

        // Extended 16-day forecast grid
        item {
            if (uiState.extendedForecast.isNotEmpty()) {
                ExtendedForecastGrid(
                    forecast = uiState.extendedForecast,
                    irrigationSuitabilities = uiState.irrigationSuitabilities,
                    modifier = Modifier.padding(16.dp),
                    onDayClick = { day ->
                        // Serialize day data and navigate with arguments
                        val dayDataJson = com.google.gson.Gson().toJson(day)
                        val encodedDayData = java.net.URLEncoder.encode(dayDataJson, "UTF-8")
                        navController.navigate("weather_day_details/$encodedDayData")
                    },
                )
            }
        }
    }
}
