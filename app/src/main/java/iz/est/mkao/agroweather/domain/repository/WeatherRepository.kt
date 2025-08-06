package iz.est.mkao.agroweather.domain.repository

import iz.est.mkao.agroweather.data.model.WeatherResponse
import java.time.LocalDate

/**
 * Domain layer repository interface for weather operations
 * This interface defines the contract for weather data access
 */
interface WeatherRepository {

    /**
     * Get current weather and forecast for a location
     * @param location The location (can be city name, coordinates, or address)
     * @return Result containing weather data or error
     */
    suspend fun getWeatherData(location: String): Result<WeatherResponse>

    /**
     * Get weather data for a specific date range
     * @param location The location
     * @param startDate Start date for the range
     * @param endDate End date for the range
     * @return Result containing weather data or error
     */
    suspend fun getWeatherDataForDateRange(
        location: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Result<WeatherResponse>

    /**
     * Get current weather conditions only
     * @param location The location
     * @return Result containing current weather data or error
     */
    suspend fun getCurrentWeather(location: String): Result<WeatherResponse>
}
