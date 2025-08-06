package iz.est.mkao.agroweather.presentation.suggestion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iz.est.mkao.agroweather.data.model.WeatherResponse
import iz.est.mkao.agroweather.domain.repository.WeatherRepository
import iz.est.mkao.agroweather.presentation.common.LoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SuggestionUiState(
    val weatherData: WeatherResponse? = null,
    val weatherLoadingState: LoadingState = LoadingState.Idle,
    val error: String? = null,
    val lastFetchTime: Long = 0L,
)

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "SuggestionViewModel"
        private const val DEFAULT_LOCATION = "Nairobi"
        private const val CACHE_DURATION_MS = 10 * 60 * 1000L // 10 minutes cache
    }

    private val _uiState = MutableStateFlow(SuggestionUiState())
    val uiState: StateFlow<SuggestionUiState> = _uiState.asStateFlow()

    init {
        // Only fetch if we don't have cached data or cache is expired
        fetchCurrentWeatherIfNeeded()
    }

    /**
     * Check if we need to fetch weather data based on cache status
     */
    private fun fetchCurrentWeatherIfNeeded() {
        val currentState = _uiState.value
        val currentTime = System.currentTimeMillis()
        val isCacheExpired = currentTime - currentState.lastFetchTime > CACHE_DURATION_MS
        
        // Only fetch if we don't have data or cache is expired
        if (currentState.weatherData == null || isCacheExpired) {
            Log.d(TAG, "Fetching weather data - hasData: ${currentState.weatherData != null}, cacheExpired: $isCacheExpired")
            fetchCurrentWeather()
        } else {
            Log.d(TAG, "Using cached weather data, cache still valid for ${(CACHE_DURATION_MS - (currentTime - currentState.lastFetchTime)) / 1000} seconds")

            if (currentState.weatherLoadingState == LoadingState.Idle) {
                _uiState.update { it.copy(weatherLoadingState = LoadingState.Success) }
            }
        }
    }

    /**
     * Fetch current weather data
     */
    private fun fetchCurrentWeather() {
        Log.d(TAG, "Fetching current weather for location: $DEFAULT_LOCATION")

        _uiState.update { it.copy(weatherLoadingState = LoadingState.Loading) }

        viewModelScope.launch {
            try {
                weatherRepository.getWeatherData(DEFAULT_LOCATION)
                    .onSuccess { weatherResponse ->
                        _uiState.update {
                            it.copy(
                                weatherData = weatherResponse,
                                weatherLoadingState = LoadingState.Success,
                                error = null,
                                lastFetchTime = System.currentTimeMillis()
                            )
                        }
                        Log.d(TAG, "Weather data fetched successfully for ${weatherResponse.resolvedAddress}")
                    }
                    .onFailure { error ->
                        val errorMessage = "Failed to fetch weather data: ${error.localizedMessage}"
                        _uiState.update {
                            it.copy(
                                weatherLoadingState = LoadingState.Error(errorMessage, error),
                                error = errorMessage,
                            )
                        }
                        Log.e(TAG, errorMessage, error)
                    }
            } catch (e: Exception) {
                val errorMessage = "Error fetching weather data: ${e.localizedMessage}"
                _uiState.update {
                    it.copy(
                        weatherLoadingState = LoadingState.Error(errorMessage, e),
                        error = errorMessage,
                    )
                }
                Log.e(TAG, errorMessage, e)
            }
        }
    }

    /**
     * Retry fetching weather data (force refresh)
     */
    fun retryFetchWeather() {
        Log.d(TAG, "Force refreshing weather data")
        fetchCurrentWeather()
    }
    
    /**
     * Force refresh weather data (ignores cache)
     */
    fun forceRefresh() {
        Log.d(TAG, "Force refresh requested")
        fetchCurrentWeather()
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
