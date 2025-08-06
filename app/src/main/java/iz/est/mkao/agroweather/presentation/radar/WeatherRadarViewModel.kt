package iz.est.mkao.agroweather.presentation.radar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iz.est.mkao.agroweather.AgroWeatherApp
import iz.est.mkao.agroweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// GeoPoint simulation since OSMDroid integration is causing import issues
data class GeoPoint(val latitude: Double, val longitude: Double)


@HiltViewModel
class WeatherRadarViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val userPreferences: iz.est.mkao.farmweather.data.preferences.UserPreferences,
) : ViewModel() {

    companion object {
        private const val TAG = "WeatherRadarViewModel"

        // East Africa weather stations
        private val DEFAULT_WEATHER_STATIONS = listOf(
            WeatherStation(
                id = "nairobi",
                name = "Nairobi",
                latitude = -1.2921,
                longitude = 36.8219,
                temperature = 22.0,
                conditions = "Partly Cloudy",
            ),
        )
    }

    private val _uiState = MutableStateFlow(WeatherRadarUiState())
    val uiState: StateFlow<WeatherRadarUiState> = _uiState.asStateFlow()

    init {
        initializeRadar()
    }

    private fun initializeRadar() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Get location from user preferences
                val locationSettings = userPreferences.getLocationSettings()
                val defaultLocation = GeoPoint(
                    locationSettings.defaultLatitude, 
                    locationSettings.defaultLongitude
                )
                
                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.d(TAG, "Using location from preferences: ${locationSettings.defaultLocation} " +
                            "(${locationSettings.defaultLatitude}, ${locationSettings.defaultLongitude})")
                }
                
                _uiState.update {
                    it.copy(
                        currentLocation = defaultLocation,
                        weatherStations = DEFAULT_WEATHER_STATIONS,
                    )
                }

                // Load current weather for the location
                loadCurrentWeather()

                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.d(TAG, "Radar initialized successfully")
                }
            } catch (e: Exception) {
                handleError("Failed to initialize radar", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refreshRadarData() {
        if (AgroWeatherApp.DEBUG_MODE) {
            Log.d(TAG, "Refreshing radar data...")
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Refresh current weather
                loadCurrentWeather()

                // Update weather stations with fresh data
                updateWeatherStations()

                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.d(TAG, "Radar data refreshed successfully")
                }
            } catch (e: Exception) {
                handleError("Failed to refresh radar data", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun changeRadarLayer(layer: RadarLayer) {
        if (AgroWeatherApp.DEBUG_MODE) {
            Log.d(TAG, "Changing radar layer to: ${layer.displayName}")
        }

        _uiState.update {
            it.copy(selectedLayer = layer)
        }

        // Here you would typically load different radar data based on the layer
        // For now, we'll just update the UI state
    }

    fun goToCurrentLocation() {
        if (AgroWeatherApp.DEBUG_MODE) {
            Log.d(TAG, "Going to current location")
        }

        viewModelScope.launch {
            try {
                // Get location from user preferences
                val locationSettings = userPreferences.getLocationSettings()
                
                // If user has selected to use current location, we'd use device GPS here
                // For now, we'll use their default location from preferences
                val currentLocation = GeoPoint(
                    locationSettings.defaultLatitude,
                    locationSettings.defaultLongitude
                )
                
                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.d(TAG, "Going to user location: ${locationSettings.defaultLocation} " +
                            "(${locationSettings.defaultLatitude}, ${locationSettings.defaultLongitude})")
                }

                _uiState.update {
                    it.copy(currentLocation = currentLocation)
                }

                // Load weather for current location
                loadCurrentWeather()
            } catch (e: Exception) {
                handleError("Failed to get current location", e)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadCurrentWeather() {
        viewModelScope.launch {
            try {
                val location = _uiState.value.currentLocation
                if (location != null) {
                    // For demonstration, we'll use "Nairobi" as the city name (Central East Africa)
                    // In a real app, you'd reverse geocode the coordinates
                    weatherRepository.getCurrentWeather("Nairobi")
                        .onSuccess { weatherResponse ->
                            val currentWeather = CurrentWeatherInfo(
                                temp = weatherResponse.currentConditions.temp.toInt(),
                                conditions = weatherResponse.currentConditions.conditions,
                                humidity = weatherResponse.currentConditions.humidity.toInt(),
                                windSpeed = weatherResponse.currentConditions.windspeed,
                                pressure = weatherResponse.currentConditions.pressure,
                            )

                            _uiState.update {
                                it.copy(currentWeather = currentWeather)
                            }

                            if (AgroWeatherApp.DEBUG_MODE) {
                                Log.d(TAG, "Current weather loaded: ${currentWeather.conditions}")
                            }
                        }
                        .onFailure { error ->
                            Log.w(TAG, "Failed to load current weather: ${error.message}")
                            // Don't show error for background weather loading
                        }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error loading current weather: ${e.message}")
            }
        }
    }

    private fun updateWeatherStations() {
        viewModelScope.launch {
            try {
                // In a real implementation, you'd fetch current conditions for each station
                // For now, we'll simulate updated data
                val updatedStations = DEFAULT_WEATHER_STATIONS.map { station ->
                    station.copy(
                        temperature = station.temperature + ((-2..2).random()), // Simulate temperature variation
                        conditions = listOf("Sunny", "Cloudy", "Partly Cloudy", "Light Rain", "Overcast").random(),
                    )
                }

                _uiState.update {
                    it.copy(weatherStations = updatedStations)
                }

                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.d(TAG, "Weather stations updated")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to update weather stations: ${e.message}")
            }
        }
    }

    private fun handleError(message: String, exception: Exception) {
        val fullMessage = "$message: ${exception.localizedMessage}"
        _uiState.update {
            it.copy(
                error = fullMessage,
                isLoading = false,
            )
        }
        Log.e(TAG, fullMessage, exception)
    }
}

// UI State
data class WeatherRadarUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLocation: GeoPoint? = null,
    val currentWeather: CurrentWeatherInfo? = null,
    val selectedLayer: RadarLayer = RadarLayer.PRECIPITATION,
    val weatherStations: List<WeatherStation> = emptyList(),
    val isAnimating: Boolean = false,
)

// Current weather info for radar display
data class CurrentWeatherInfo(
    val temp: Int,
    val conditions: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
)
