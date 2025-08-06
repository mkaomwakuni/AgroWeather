package iz.est.mkao.agroweather.presentation.weather

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import iz.est.mkao.agroweather.presentation.weather.components.CityPickerDialog
import iz.est.mkao.agroweather.presentation.weather.components.ErrorDialog
import iz.est.mkao.agroweather.presentation.weather.components.LoadingScreen
import iz.est.mkao.agroweather.presentation.weather.components.WeatherContent
import iz.est.mkao.agroweather.presentation.common.NetworkSnackbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp(
    navController: NavController = rememberNavController(),
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    var showCityPicker by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Dynamic gradient colors from weather-based color pools
    val weatherGradient = remember(
        uiState.weatherData?.currentConditions?.icon,
        uiState.weatherData?.currentConditions?.temp
    ) {
        uiState.weatherData?.currentConditions?.let { current ->
            // Use the new dynamic color pool system with random variations
            viewModel.getWeatherGradient(current.icon, current.temp)
        } ?: androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(
                androidx.compose.ui.graphics.Color(0xFF6BA6CD),
                androidx.compose.ui.graphics.Color(0xFF8BB8E8),
                androidx.compose.ui.graphics.Color(0x20FFFFFF),
                androidx.compose.ui.graphics.Color.Transparent,
            ),
        )
    }

    // Extract theme colors for comprehensive UI theming
    val weatherColors = remember(
        uiState.weatherData?.currentConditions?.icon,
        uiState.weatherData?.currentConditions?.temp
    ) {
        uiState.weatherData?.currentConditions?.let { current ->
            viewModel.getWeatherColors(current.icon, current.temp)
        } ?: iz.est.mkao.farmweather.domain.model.WeatherThemeColors(
            primary = androidx.compose.ui.graphics.Color(0xFF6BA6CD),
            secondary = androidx.compose.ui.graphics.Color(0xFF8BB8E8),
            accent = androidx.compose.ui.graphics.Color(0xFF4A90E2)
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        when (uiState.loadingState) {
            LoadingState.LoadingWeather, LoadingState.LoadingCities -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            else -> {
                WeatherContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    navController = navController,
                    weatherGradient = weatherGradient,
                    scrollBehavior = scrollBehavior,
                    showCityPicker = showCityPicker,
                    onShowCityPicker = { show: Boolean -> showCityPicker = show },
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }

        // Error display
        uiState.error?.let { error: WeatherError ->
            ErrorDialog(
                error = error,
                onDismiss = { viewModel.clearError() },
                onRetry = { viewModel.refreshWeatherForSelectedCity() },
            )
        }

        // City picker dialog
        if (showCityPicker) {
            CityPickerDialog(
                availableCities = uiState.availableCities,
                isLoadingCities = uiState.loadingState == LoadingState.LoadingCities,
                onCitySelected = { city ->
                    viewModel.selectCity(city)
                    showCityPicker = false
                },
                onDismiss = { showCityPicker = false },
                onSearchCities = { query -> viewModel.searchCities(query) },
            )
        }

        // Network connectivity snackbar
        NetworkSnackbar(
            modifier = Modifier.padding(paddingValues),
            isVisible = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherAppPreview() {
    androidx.compose.material3.MaterialTheme {
        WeatherApp()
    }
}
