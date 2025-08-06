package iz.est.mkao.agroweather.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iz.est.mkao.agroweather.data.model.City
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import iz.est.mkao.agroweather.data.repository.CityRepository
import iz.est.mkao.agroweather.domain.repository.WeatherRepository
import iz.est.mkao.agroweather.domain.usecase.irrigation.CalculateIrrigationSuitabilityUseCase
import iz.est.mkao.agroweather.domain.usecase.weather.GetWeatherDescriptionsUseCase
import iz.est.mkao.agroweather.domain.usecase.weather.GetWeatherGradientUseCase
import iz.est.mkao.agroweather.domain.model.WeatherThemeColors
import iz.est.mkao.agroweather.domain.model.WeatherGradientType
import iz.est.mkao.agroweather.util.TemperatureUtils
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
    private val userPreferences: UserPreferences,
    private val calculateIrrigationSuitabilityUseCase: CalculateIrrigationSuitabilityUseCase,
    private val getWeatherGradientUseCase: GetWeatherGradientUseCase,
    private val getWeatherDescriptionsUseCase: GetWeatherDescriptionsUseCase,
    val temperatureUtils: TemperatureUtils,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        loadCities()
        loadDefaultCity()
    }

    fun loadWeatherData(location: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loadingState = LoadingState.LoadingWeather,
                error = null,
            )

            weatherRepository.getWeatherData(location).fold(
                onSuccess = { weatherResponse ->
                    _uiState.value = _uiState.value.copy(
                        weatherData = weatherResponse,
                        loadingState = LoadingState.Idle,
                        error = null,
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        loadingState = LoadingState.Idle,
                        error = WeatherError.fromThrowable(exception),
                    )
                },
            )
        }
    }

    fun loadExtendedForecast(location: String, days: Int = 16) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loadingState = LoadingState.LoadingExtendedForecast,
            )

            val endDate = LocalDate.now().plusDays(days.toLong())

            weatherRepository.getWeatherDataForDateRange(
                location = location,
                startDate = LocalDate.now(),
                endDate = endDate,
            ).fold(
                onSuccess = { weatherResponse ->
                    val irrigationSuitabilities = weatherResponse.days.map { day ->
                        calculateIrrigationSuitabilityUseCase(day)
                    }

                    _uiState.value = _uiState.value.copy(
                        extendedForecast = weatherResponse.days,
                        irrigationSuitabilities = irrigationSuitabilities,
                        loadingState = LoadingState.Idle,
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        loadingState = LoadingState.Idle,
                        error = WeatherError.fromThrowable(exception),
                    )
                },
            )
        }
    }

    fun refreshWeather(location: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loadingState = LoadingState.Refreshing)
            loadWeatherData(location)
            loadExtendedForecast(location)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getWeatherGradient(iconCode: String, temperature: Double) =
        getWeatherGradientUseCase(iconCode, temperature)

    fun getWeatherColors(iconCode: String, temperature: Double): WeatherThemeColors {
        return when {
            iconCode.contains("clear") && temperature > 25 -> WeatherThemeColors(
                primary = Color(0xFFFFD700), // Gold
                secondary = Color(0xFFFF8C00), // Dark orange
                accent = Color(0xFFFF6B35), // Red orange
            )
            iconCode.contains("clear") -> WeatherThemeColors(
                primary = Color(0xFF87CEEB), // Sky blue
                secondary = Color(0xFF4682B4), // Steel blue
                accent = Color(0xFF1E88E5), // Blue
            )
            iconCode.contains("rain") || iconCode.contains("showers") -> WeatherThemeColors(
                primary = Color(0xFF708090), // Slate gray
                secondary = Color(0xFF4682B4), // Steel blue
                accent = Color(0xFF2F4F4F), // Dark slate gray
            )
            iconCode.contains("cloudy") || iconCode.contains("overcast") -> WeatherThemeColors(
                primary = Color(0xFFB0C4DE), // Light steel blue
                secondary = Color(0xFF778899), // Light slate gray
                accent = Color(0xFF696969), // Dim gray
            )
            iconCode.contains("partly") -> WeatherThemeColors(
                primary = Color(0xFF87CEFA), // Light sky blue
                secondary = Color(0xFF4A90E2), // Blue
                accent = Color(0xFF1976D2), // Primary blue
            )
            iconCode.contains("night") -> WeatherThemeColors(
                primary = Color(0xFF191970), // Midnight blue
                secondary = Color(0xFF483D8B), // Dark slate blue
                accent = Color(0xFF2F2F4F), // Dark slate gray
            )
            iconCode.contains("thunder") || iconCode.contains("storm") -> WeatherThemeColors(
                primary = Color(0xFF2F2F2F), // Dark gray
                secondary = Color(0xFF4B0082), // Indigo
                accent = Color(0xFF8B008B), // Dark magenta
            )
            iconCode.contains("snow") -> WeatherThemeColors(
                primary = Color(0xFFF0F8FF), // Alice blue
                secondary = Color(0xFFE6E6FA), // Lavender
                accent = Color(0xFFD3D3D3), // Light gray
            )
            else -> WeatherThemeColors(
                primary = Color(0xFF4A90E2),
                secondary = Color(0xFF7BB3F7),
                accent = Color(0xFF6BA6CD),
            )
        }
    }

    fun getWeatherDescriptions() = getWeatherDescriptionsUseCase

    private fun loadCities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loadingState = LoadingState.LoadingCities)

            try {
                cityRepository.getCities().collect { cities ->
                    _uiState.value = _uiState.value.copy(
                        availableCities = cities,
                        loadingState = LoadingState.Idle,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loadingState = LoadingState.Idle,
                    error = WeatherError.fromThrowable(e),
                )
            }
        }
    }

    private fun loadDefaultCity() {
        viewModelScope.launch {
            try {
                val selectedCity = if (userPreferences.useCurrentLocation) {
                    // Use current location - for now use default city
                    cityRepository.getDefaultCity()
                } else {
                    // Use selected city from preferences
                    val savedLocation = userPreferences.defaultLocation
                    cityRepository.getCityByName(savedLocation) ?: cityRepository.getDefaultCity()
                }

                _uiState.value = _uiState.value.copy(selectedCity = selectedCity)
                loadWeatherData(selectedCity.getLocationString())
                loadExtendedForecast(selectedCity.getLocationString())
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = WeatherError.fromThrowable(e),
                )
            }
        }
    }

    fun selectCity(city: City) {
        _uiState.value = _uiState.value.copy(selectedCity = city)

        // Save selected city to preferences for synchronization
        userPreferences.updateLocationSettings(
            useCurrentLocation = false,
            location = city.name,
            latitude = city.latitude,
            longitude = city.longitude,
        )

        loadWeatherData(city.getLocationString())
        loadExtendedForecast(city.getLocationString())
    }

    fun searchCities(query: String) {
        viewModelScope.launch {
            cityRepository.searchCities(query).collect { cities ->
                _uiState.value = _uiState.value.copy(
                    availableCities = cities,
                )
            }
        }
    }

    fun refreshWeatherForSelectedCity() {
        _uiState.value.selectedCity?.let { city ->
            refreshWeather(city.getLocationString())
        }
    }

    /**
     * Refresh location from user preferences (called when returning from settings)
     */
    fun refreshLocationFromPreferences() {
        loadDefaultCity()
    }
}
