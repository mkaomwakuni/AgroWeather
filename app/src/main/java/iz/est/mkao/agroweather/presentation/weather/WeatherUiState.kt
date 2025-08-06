package iz.est.mkao.agroweather.presentation.weather

import iz.est.mkao.agroweather.data.model.City
import iz.est.mkao.agroweather.data.model.DayWeather
import iz.est.mkao.agroweather.data.model.WeatherResponse
import iz.est.mkao.agroweather.domain.model.IrrigationSuitability

data class WeatherUiState(
    val weatherData: WeatherResponse? = null,
    val extendedForecast: List<DayWeather> = emptyList(),
    val irrigationSuitabilities: List<IrrigationSuitability> = emptyList(),
    val selectedCity: City? = null,
    val availableCities: List<City> = emptyList(),
    val loadingState: LoadingState = LoadingState.Idle,
    val error: WeatherError? = null,
)

enum class LoadingState {
    Idle,
    LoadingWeather,
    LoadingExtendedForecast,
    LoadingCities,
    Refreshing,
}

sealed class WeatherError(
    val message: String,
    val cause: Throwable? = null,
) {
    class NetworkError(message: String = "Network connection failed", cause: Throwable? = null) : WeatherError(message, cause)
    class ApiError(message: String = "Weather service unavailable", cause: Throwable? = null) : WeatherError(message, cause)
    class LocationError(message: String = "Location not found", cause: Throwable? = null) : WeatherError(message, cause)
    class UnknownError(message: String = "An unexpected error occurred", cause: Throwable? = null) : WeatherError(message, cause)

    companion object {
        fun fromThrowable(throwable: Throwable): WeatherError {
            return when {
                throwable.message?.contains("network", ignoreCase = true) == true -> NetworkError(cause = throwable)
                throwable.message?.contains("not found", ignoreCase = true) == true -> LocationError(cause = throwable)
                throwable.message?.contains("api", ignoreCase = true) == true -> ApiError(cause = throwable)
                else -> UnknownError(throwable.message ?: "Unknown error", throwable)
            }
        }
    }
}
