package iz.est.mkao.agroweather.presentation.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import iz.est.mkao.agroweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromptViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptUiState())
    val uiState: StateFlow<PromptUiState> = _uiState.asStateFlow()

    fun onEvent(event: PromptEvent) {
        when (event) {
            is PromptEvent.OnSuggestionSelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedSuggestion = event.suggestion,
                )
            }
            is PromptEvent.OnLocationChanged -> {
                _uiState.value = _uiState.value.copy(
                    cityName = event.cityName,
                    latitude = event.latitude,
                    longitude = event.longitude,
                )
            }
            PromptEvent.OnLoadSuggestions -> {
                loadSuggestions()
            }
        }
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val locationSettings = userPreferences.getLocationSettings()
                val location = if (locationSettings.useCurrentLocation) {
                    "${locationSettings.defaultLatitude},${locationSettings.defaultLongitude}"
                } else {
                    locationSettings.defaultLocation
                }
                
                weatherRepository.getCurrentWeather(location)
                    .onSuccess { weatherData ->
                        val suggestions = generateWeatherBasedSuggestions(weatherData)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherSuggestions = suggestions,
                        )
                    }
                    .onFailure {
                        // Fallback to default suggestions if weather data fails
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherSuggestions = getDefaultWeatherSuggestions(),
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    weatherSuggestions = getDefaultWeatherSuggestions(),
                    error = e.message,
                )
            }
        }
    }
    
    private fun generateWeatherBasedSuggestions(weatherData: iz.est.mkao.farmweather.data.model.WeatherResponse): List<String> {
        val suggestions = mutableListOf<String>()
        val currentConditions = weatherData.currentConditions
        val today = weatherData.days.firstOrNull()
        
        // Temperature-based suggestions
        when {
            currentConditions.temp > 30 -> {
                suggestions.add("How to protect crops from heat stress at ${currentConditions.temp.toInt()}°C?")
                suggestions.add("Best irrigation practices for hot weather conditions?")
            }
            currentConditions.temp < 5 -> {
                suggestions.add("Cold weather precautions for crops at ${currentConditions.temp.toInt()}°C?")
                suggestions.add("Which crops can survive frost conditions?")
            }
            else -> {
                suggestions.add("Optimal farming activities for ${currentConditions.temp.toInt()}°C weather?")
            }
        }
        
        // Precipitation-based suggestions
        today?.let { dayWeather ->
            when {
                dayWeather.precipprob > 70 -> {
                    suggestions.add("Rainy day farming tasks with ${dayWeather.precipprob.toInt()}% rain chance?")
                    suggestions.add("How to prevent crop diseases in wet conditions?")
                }
                dayWeather.precipprob < 20 && currentConditions.humidity < 40 -> {
                    suggestions.add("Drought management strategies for dry conditions?")
                    suggestions.add("Water conservation techniques for farming?")
                }
                else -> {
                    suggestions.add("Optimal watering schedule for current conditions?")
                }
            }
        }
        
        // Humidity and wind based suggestions
        when {
            currentConditions.humidity > 80 -> {
                suggestions.add("High humidity farming considerations at ${currentConditions.humidity.toInt()}%?")
            }
            currentConditions.windspeed > 25 -> {
                suggestions.add("Wind protection strategies for crops at ${currentConditions.windspeed.toInt()} km/h?")
            }
            else -> {
                // Normal conditions - add general suggestion
                suggestions.add("General farming tips for current weather conditions?")
            }
        }
        
        // Seasonal suggestions based on current conditions
        suggestions.add("What crops to plant in current weather conditions?")
        suggestions.add("Pest control recommendations for today's weather?")
        
        return suggestions.take(5) // Limit to 5 suggestions
    }

    private fun getDefaultWeatherSuggestions(): List<String> {
        return listOf(
            "How will today's weather affect my crops?",
            "What crops are best for current soil conditions?",
            "Should I irrigate today based on weather forecast?",
            "Best planting time with current weather trends?",
            "Weather-based pest control recommendations",
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
