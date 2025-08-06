package iz.est.mkao.agroweather.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Objects for Open-Meteo Weather API responses
 * These are separate from domain models to maintain clean architecture
 * Documentation: https://open-meteo.com/en/docs
 */

data class OpenMeteoResponseDto(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("elevation") val elevation: Double?,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("timezone_abbreviation") val timezoneAbbreviation: String,
    @SerializedName("utc_offset_seconds") val utcOffsetSeconds: Int,
    @SerializedName("current") val current: OpenMeteoCurrentDto?,
    @SerializedName("hourly") val hourly: OpenMeteoHourlyDto?,
    @SerializedName("daily") val daily: OpenMeteoDailyDto?,
)

data class OpenMeteoCurrentDto(
    @SerializedName("time") val time: String,
    @SerializedName("temperature_2m") val temperature2m: Double?,
    @SerializedName("showers") val showers: Double?,
    @SerializedName("snowfall") val snowfall: Double?,
    @SerializedName("rain") val rain: Double?,
    @SerializedName("precipitation") val precipitation: Double?,
    @SerializedName("cloud_cover") val cloudCover: Double?,
    @SerializedName("pressure_msl") val pressureMsl: Double?,
    @SerializedName("surface_pressure") val surfacePressure: Double?,
    @SerializedName("wind_gusts_10m") val windGusts10m: Double?,
    @SerializedName("wind_direction_10m") val windDirection10m: Double?,
    @SerializedName("wind_speed_10m") val windSpeed10m: Double?,
    @SerializedName("apparent_temperature") val apparentTemperature: Double?,
    @SerializedName("is_day") val isDay: Int?,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: Double?,
    @SerializedName("weather_code") val weatherCode: Int?,
)

data class OpenMeteoHourlyDto(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature2m: List<Double?>,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: List<Double?>,
    @SerializedName("dew_point_2m") val dewPoint2m: List<Double?>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Double?>,
    @SerializedName("showers") val showers: List<Double?>,
    @SerializedName("rain") val rain: List<Double?>,
    @SerializedName("snow_depth") val snowDepth: List<Double?>,
    @SerializedName("snowfall") val snowfall: List<Double?>,
    @SerializedName("pressure_msl") val pressureMsl: List<Double?>,
    @SerializedName("surface_pressure") val surfacePressure: List<Double?>,
    @SerializedName("cloud_cover") val cloudCover: List<Double?>,
    @SerializedName("visibility") val visibility: List<Double?>,
    @SerializedName("evapotranspiration") val evapotranspiration: List<Double?>,
    @SerializedName("vapour_pressure_deficit") val vapourPressureDeficit: List<Double?>,
    @SerializedName("wind_speed_10m") val windSpeed10m: List<Double?>,
    @SerializedName("wind_gusts_10m") val windGusts10m: List<Double?>,
    @SerializedName("wind_direction_10m") val windDirection10m: List<Double?>,
    @SerializedName("soil_temperature_0cm") val soilTemperature0cm: List<Double?>,
    @SerializedName("soil_temperature_6cm") val soilTemperature6cm: List<Double?>,
    @SerializedName("soil_moisture_0_to_1cm") val soilMoisture0To1cm: List<Double?>,
    @SerializedName("soil_moisture_1_to_3cm") val soilMoisture1To3cm: List<Double?>,
    @SerializedName("soil_moisture_3_to_9cm") val soilMoisture3To9cm: List<Double?>,
    @SerializedName("uv_index") val uvIndex: List<Double?>,
    @SerializedName("uv_index_clear_sky") val uvIndexClearSky: List<Double?>,
    @SerializedName("is_day") val isDay: List<Int?>,
    @SerializedName("sunshine_duration") val sunshineDuration: List<Double?>,
    @SerializedName("wet_bulb_temperature_2m") val wetBulbTemperature2m: List<Double?>,
    @SerializedName("direct_radiation") val directRadiation: List<Double?>,
    @SerializedName("weather_code") val weatherCode: List<Int?>,
)

data class OpenMeteoDailyDto(
    @SerializedName("time") val time: List<String>,
    @SerializedName("sunrise") val sunrise: List<String?>,
    @SerializedName("sunset") val sunset: List<String?>,
    @SerializedName("precipitation_hours") val precipitationHours: List<Double?>,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double?>,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double?>,
    @SerializedName("weather_code") val weatherCode: List<Int?>,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double?>,
    @SerializedName("sunshine_duration") val sunshineDuration: List<Double?>,
    @SerializedName("daylight_duration") val daylightDuration: List<Double?>,
    @SerializedName("snowfall_sum") val snowfallSum: List<Double?>,
    @SerializedName("showers_sum") val showersSum: List<Double?>,
    @SerializedName("rain_sum") val rainSum: List<Double?>,
    @SerializedName("wind_speed_10m_max") val windSpeed10mMax: List<Double?>,
    @SerializedName("wind_gusts_10m_max") val windGusts10mMax: List<Double?>,
    @SerializedName("wind_direction_10m_dominant") val windDirection10mDominant: List<Double?>,
    @SerializedName("shortwave_radiation_sum") val shortwaveRadiationSum: List<Double?>,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Double?>,
    @SerializedName("precipitation_sum") val precipitationSum: List<Double?>,
    @SerializedName("uv_index_clear_sky_max") val uvIndexClearSkyMax: List<Double?>,
    @SerializedName("apparent_temperature_min") val apparentTemperatureMin: List<Double?>,
    @SerializedName("apparent_temperature_max") val apparentTemperatureMax: List<Double?>,
)
