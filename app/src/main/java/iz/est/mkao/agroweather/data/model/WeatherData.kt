package iz.est.mkao.agroweather.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val queryCost: Int,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val address: String,
    val timezone: String,
    val tzoffset: Double,
    val description: String?,
    val days: List<DayWeather>,
    val currentConditions: CurrentConditions,
)

data class CurrentConditions(
    val datetime: String,
    val temp: Double,
    val feelslike: Double,
    val humidity: Double,
    val dew: Double,
    val precip: Double,
    val precipprob: Double,
    val snow: Double,
    val snowdepth: Double,
    val windspeed: Double,
    val windgust: Double,
    val winddir: Double,
    val pressure: Double,
    val visibility: Double,
    val cloudcover: Double,
    val solarradiation: Double,
    val solarenergy: Double,
    val uvindex: Double,
    val conditions: String,
    val icon: String,

    // Agriculture specific elements
    val soilmoisture: Double?,
    val soiltemp: Double?,
    val evapotranspiration: Double?,
    val potentialevapotranspiration: Double?,
)

data class DayWeather(
    val datetime: String,
    val temp: Double,
    val feelslike: Double,
    val humidity: Double,
    val dew: Double,
    val precip: Double,
    val precipprob: Double,
    val snow: Double,
    val snowdepth: Double,
    val windspeed: Double,
    val windgust: Double,
    val winddir: Double,
    val pressure: Double,
    val visibility: Double,
    val cloudcover: Double,
    val solarradiation: Double,
    val solarenergy: Double,
    val uvindex: Double,
    val sunrise: String,
    val sunset: String,
    val conditions: String,
    val description: String,
    val icon: String,
    val hours: List<HourWeather>,

    // Agriculture specific elements
    @SerializedName("soilmoisture") val soilMoisture: Double?,
    @SerializedName("soiltemp") val soilTemperature: Double?,
    @SerializedName("evapotranspiration") val evapotranspiration: Double?,
    @SerializedName("potentialevapotranspiration") val potentialEvapotranspiration: Double?,
    val sunshineDuration: Double?, // Real sunshine duration from API in seconds
)

data class HourWeather(
    val datetime: String,
    val temp: Double,
    val feelslike: Double,
    val humidity: Double,
    val dew: Double,
    val precip: Double,
    val precipprob: Double,
    val snow: Double,
    val snowdepth: Double,
    val windspeed: Double,
    val windgust: Double,
    val winddir: Double,
    val pressure: Double,
    val visibility: Double,
    val cloudcover: Double,
    val solarradiation: Double,
    val solarenergy: Double,
    val uvindex: Double,
    val conditions: String,
    val icon: String,

    // Agriculture specific elements
    @SerializedName("soilmoisture") val soilMoisture: Double?,
    @SerializedName("soiltemp") val soilTemperature: Double?,
    @SerializedName("evapotranspiration") val evapotranspiration: Double?,
    @SerializedName("potentialevapotranspiration") val potentialEvapotranspiration: Double?,
)
