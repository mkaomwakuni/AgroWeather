package iz.est.mkao.agroweather.data.repository

import iz.est.mkao.agroweather.data.api.WeatherApiService
import iz.est.mkao.agroweather.data.mapper.toDomain
import iz.est.mkao.agroweather.data.model.WeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Repository class to handle weather data operations using Open-Meteo API
 * No API key required - free and open source weather API
 */
class WeatherRepository {

    private val apiService: WeatherApiService

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/"

        @Volatile
        private var INSTANCE: WeatherRepository? = null

        fun getInstance(): WeatherRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepository().also { INSTANCE = it }
            }
        }
    }

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(WeatherApiService::class.java)
    }

    /**
     * Get current weather and forecast for a location
     * @param location Location string in format "latitude,longitude" or city name
     */
    suspend fun getWeatherData(location: String): Result<WeatherResponse> {
        return try {
            val (latitude, longitude) = parseLocation(location)

            val response = apiService.getWeatherData(
                latitude = latitude,
                longitude = longitude,
            )

            if (response.isSuccessful) {
                response.body()?.let { openMeteoResponse ->
                    Result.success(openMeteoResponse.toDomain())
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API call failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get weather data for a specific date range
     * @param location Location string in format "latitude,longitude" or city name
     * @param startDate Start date for the forecast
     * @param endDate End date for the forecast
     */
    suspend fun getWeatherDataForDateRange(
        location: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Result<WeatherResponse> {
        return try {
            val (latitude, longitude) = parseLocation(location)
            val forecastDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1

            val response = apiService.getExtendedForecast(
                latitude = latitude,
                longitude = longitude,
                forecastDays = forecastDays,
            )

            if (response.isSuccessful) {
                response.body()?.let { openMeteoResponse ->
                    Result.success(openMeteoResponse.toDomain())
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API call failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get current weather conditions only
     * @param location Location string in format "latitude,longitude" or city name
     */
    suspend fun getCurrentWeather(location: String): Result<WeatherResponse> {
        return try {
            val (latitude, longitude) = parseLocation(location)

            val response = apiService.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
            )

            if (response.isSuccessful) {
                response.body()?.let { openMeteoResponse ->
                    Result.success(openMeteoResponse.toDomain())
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API call failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
                Pair(52.52, 13.41)
            }
        } catch (e: Exception) {
            // Fallback to Berlin coordinates if parsing fails
            Pair(52.52, 13.41)
        }
    }
}
