package iz.est.mkao.agroweather.data.api

import iz.est.mkao.agroweather.data.remote.dto.OpenMeteoResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Weather API service interface for Open-Meteo Weather API
 * Documentation: https://open-meteo.com/en/docs
 * No API key required - free and open source weather API
 */
interface WeatherApiService {

    /**
     * Get comprehensive weather data from Open-Meteo API
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @param current Current weather parameters
     * @param hourly Hourly weather parameters
     * @param daily Daily weather parameters
     * @param timezone Timezone for the forecast
     * @param forecastDays Number of forecast days (default 16)
     * @param forecastHours Number of forecast hours (default 24)
     */
    @GET("v1/forecast")
    suspend fun getWeatherData(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,showers,snowfall,rain,precipitation,cloud_cover,pressure_msl,surface_pressure,wind_gusts_10m,wind_direction_10m,wind_speed_10m,apparent_temperature,is_day,relative_humidity_2m,weather_code",
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,dew_point_2m,precipitation_probability,showers,rain,snow_depth,snowfall,pressure_msl,surface_pressure,cloud_cover,evapotranspiration,vapour_pressure_deficit,wind_speed_10m,wind_gusts_10m,wind_direction_10m,soil_temperature_0cm,soil_temperature_6cm,soil_moisture_0_to_1cm,soil_moisture_1_to_3cm,soil_moisture_3_to_9cm,uv_index,uv_index_clear_sky,is_day,sunshine_duration,wet_bulb_temperature_2m,direct_radiation,weather_code",
        @Query("daily") daily: String = "sunrise,sunset,precipitation_hours,temperature_2m_max,temperature_2m_min,weather_code,uv_index_max,sunshine_duration,daylight_duration,snowfall_sum,showers_sum,rain_sum,wind_speed_10m_max,wind_gusts_10m_max,wind_direction_10m_dominant,shortwave_radiation_sum,precipitation_probability_max,precipitation_sum,uv_index_clear_sky_max,apparent_temperature_min,apparent_temperature_max",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 16,
        @Query("forecast_hours") forecastHours: Int = 24,
    ): Response<OpenMeteoResponseDto>

    /**
     * Get current weather conditions only
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @param current Current weather parameters
     * @param timezone Timezone for the forecast
     */
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,showers,snowfall,rain,precipitation,cloud_cover,pressure_msl,surface_pressure,wind_gusts_10m,wind_direction_10m,wind_speed_10m,apparent_temperature,is_day,relative_humidity_2m,weather_code",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 1,
    ): Response<OpenMeteoResponseDto>

    /**
     * Get weather data for extended forecast with agricultural data
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @param daily Daily weather parameters
     * @param hourly Hourly weather parameters (for agricultural data)
     * @param timezone Timezone for the forecast
     * @param forecastDays Number of forecast days (max 16 for free tier)
     */
    @GET("v1/forecast")
    suspend fun getExtendedForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "sunrise,sunset,precipitation_hours,temperature_2m_max,temperature_2m_min,weather_code,uv_index_max,sunshine_duration,daylight_duration,snowfall_sum,showers_sum,rain_sum,wind_speed_10m_max,wind_gusts_10m_max,wind_direction_10m_dominant,shortwave_radiation_sum,precipitation_probability_max,precipitation_sum,uv_index_clear_sky_max,apparent_temperature_min,apparent_temperature_max",
        @Query("hourly") hourly: String = "relative_humidity_2m,dew_point_2m,soil_moisture_0_to_1cm,soil_temperature_0cm,evapotranspiration",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 16,
    ): Response<OpenMeteoResponseDto>
}
