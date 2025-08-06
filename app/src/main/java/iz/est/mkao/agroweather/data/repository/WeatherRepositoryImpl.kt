package iz.est.mkao.agroweather.data.repository

import android.util.Log
import iz.est.mkao.agroweather.util.SecureLogger
import iz.est.mkao.agroweather.AgroWeatherApp
import iz.est.mkao.agroweather.data.api.WeatherApiService
import iz.est.mkao.agroweather.data.mapper.toDomain
import iz.est.mkao.agroweather.data.model.WeatherResponse
import iz.est.mkao.agroweather.domain.repository.WeatherRepository
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-ready WeatherRepository implementation with proper error handling,
 * retry logic, and logging - Updated for Open-Meteo API
 */
@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
) : WeatherRepository {

    companion object {
        private const val TAG = "WeatherRepository"
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val RETRY_DELAY_MS = 1000L
    }

    /**
     * Get current weather and forecast for a location with retry logic
     */
    override suspend fun getWeatherData(location: String): Result<WeatherResponse> {
        return executeWithRetry {
            if (AgroWeatherApp.DEBUG_MODE) {
                SecureLogger.d(TAG, "Fetching weather data for location: $location")
            }

            val (latitude, longitude) = parseLocation(location)
            val response = apiService.getWeatherData(
                latitude = latitude,
                longitude = longitude,
            )

            handleApiResponse(response) { it.toDomain() }
        }
    }

    /**
     * Get weather data for a specific date range with retry logic
     */
    override suspend fun getWeatherDataForDateRange(
        location: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Result<WeatherResponse> {
        return executeWithRetry {
            val forecastDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
            // Clamp to Open-Meteo's free tier limit of 16 days
            val clampedForecastDays = minOf(forecastDays, 16)

            if (AgroWeatherApp.DEBUG_MODE) {
                Log.d(TAG, "Fetching weather data for location: $location, forecast days: $clampedForecastDays")
                if (forecastDays > 16) {
                    Log.w(TAG, "Requested $forecastDays days, clamped to 16 days (Open-Meteo free tier limit)")
                }
            }

            val (latitude, longitude) = parseLocation(location)
            val response = apiService.getExtendedForecast(
                latitude = latitude,
                longitude = longitude,
                forecastDays = clampedForecastDays,
            )

            handleApiResponse(response) { it.toDomain() }
        }
    }

    /**
     * Get current weather conditions only with retry logic
     */
    override suspend fun getCurrentWeather(location: String): Result<WeatherResponse> {
        return executeWithRetry {
            if (AgroWeatherApp.DEBUG_MODE) {
                Log.d(TAG, "Fetching current weather for location: $location")
            }

            val (latitude, longitude) = parseLocation(location)
            val response = apiService.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
            )

            handleApiResponse(response) { it.toDomain() }
        }
    }

    /**
     * Parse location string to extract latitude and longitude coordinates
     * Supports formats: "lat,lng", "lat,lng" or defaults to a sample location
     * In a production app, you would integrate with a geocoding service for city names
     */
    private fun parseLocation(location: String): Pair<Double, Double> {
        return try {
            val parts = location.split(",")
            if (parts.size >= 2) {
                val lat = parts[0].trim().toDouble()
                val lng = parts[1].trim().toDouble()
                Pair(lat, lng)
            } else {
                // Default to a sample location (Berlin) if parsing fails
                // In production, you would use a geocoding service here
                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.w(TAG, "Unable to parse location '$location', using default coordinates")
                }
                Pair(52.52, 13.41)
            }
        } catch (e: Exception) {
            // Fallback to Berlin coordinates if parsing fails
            if (AgroWeatherApp.DEBUG_MODE) {
                Log.w(TAG, "Error parsing location '$location': ${e.message}, using default coordinates")
            }
            Pair(52.52, 13.41)
        }
    }

    /**
     * Execute API call with retry logic for transient errors
     */
    private suspend fun <T> executeWithRetry(
        maxRetries: Int = MAX_RETRY_ATTEMPTS,
        apiCall: suspend () -> Result<T>,
    ): Result<T> {
        var lastException: Throwable? = null

        repeat(maxRetries) { attempt ->
            try {
                val result = apiCall()
                if (result.isSuccess) {
                    if (attempt > 0 && AgroWeatherApp.DEBUG_MODE) {
                        Log.d(TAG, "API call succeeded after ${attempt + 1} attempts")
                    }
                    return result
                }

                // If it's a failure due to non-retryable error, return immediately
                result.exceptionOrNull()?.let { exception ->
                    if (!isRetryableException(exception)) {
                        logError("Non-retryable error on attempt ${attempt + 1}", exception)
                        return result
                    }
                    lastException = exception
                }
            } catch (e: Exception) {
                lastException = e
                if (!isRetryableException(e)) {
                    logError("Non-retryable exception on attempt ${attempt + 1}", e)
                    return Result.failure(e)
                }

                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.w(TAG, "Retryable error on attempt ${attempt + 1}/$maxRetries: ${e.message}")
                }
            }

            // Don't delay after the last attempt
            if (attempt < maxRetries - 1) {
                delay(RETRY_DELAY_MS * (attempt + 1)) // Exponential backoff
            }
        }

        val finalException = lastException ?: Exception("Unknown error after $maxRetries attempts")
        logError("All retry attempts failed", finalException)
        return Result.failure(finalException)
    }

    /**
     * Handle API response and convert to domain model
     */
    private fun <T, R> handleApiResponse(
        response: retrofit2.Response<T>,
        mapper: (T) -> R,
    ): Result<R> {
        return try {
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val mapped = mapper(body)
                    if (AgroWeatherApp.DEBUG_MODE) {
                        Log.d(TAG, "API call successful")
                    }
                    Result.success(mapped)
                } ?: run {
                    val error = "Empty response body"
                    logError(error, null)
                    Result.failure<R>(Exception(error))
                }
            } else {
                val errorMsg = "API call failed: ${response.code()} - ${response.message()}"
                val exception = HttpException(response)
                logError(errorMsg, exception)
                Result.failure(exception)
            }
        } catch (e: Exception) {
            logError("Error processing API response", e)
            Result.failure(e)
        }
    }

    /**
     * Determine if an exception is retryable
     */
    private fun isRetryableException(exception: Throwable): Boolean {
        return when (exception) {
            is IOException,
            is SocketTimeoutException,
            -> true
            is HttpException -> {
                // Retry on server errors (5xx) and some client errors
                val code = exception.code()
                code >= 500 || code == 408 || code == 429
            }
            else -> false
        }
    }

    /**
     * Log errors based on build configuration
     */
    private fun logError(message: String, exception: Throwable?) {
        when (AgroWeatherApp.LOG_LEVEL) {
            "DEBUG", "INFO" -> {
                Log.e(TAG, message, exception)
            }
            "ERROR" -> {
                if (exception != null) {
                    Log.e(TAG, message, exception)
                }
            }
            // In production, we might want to use a crash reporting service
        }
    }
}
