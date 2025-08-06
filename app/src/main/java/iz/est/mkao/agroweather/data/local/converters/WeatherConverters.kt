package iz.est.mkao.agroweather.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import iz.est.mkao.agroweather.data.model.*

class WeatherConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherResponse(weatherResponse: WeatherResponse): String {
        return gson.toJson(weatherResponse)
    }

    @TypeConverter
    fun toWeatherResponse(weatherResponseString: String): WeatherResponse {
        return gson.fromJson(weatherResponseString, WeatherResponse::class.java)
    }

    @TypeConverter
    fun fromCurrentConditions(currentConditions: CurrentConditions): String {
        return gson.toJson(currentConditions)
    }

    @TypeConverter
    fun toCurrentConditions(currentConditionsString: String): CurrentConditions {
        return gson.fromJson(currentConditionsString, CurrentConditions::class.java)
    }

    @TypeConverter
    fun fromDayWeatherList(dayWeatherList: List<DayWeather>): String {
        return gson.toJson(dayWeatherList)
    }

    @TypeConverter
    fun toDayWeatherList(dayWeatherListString: String): List<DayWeather> {
        val listType = object : TypeToken<List<DayWeather>>() {}.type
        return gson.fromJson(dayWeatherListString, listType)
    }

    @TypeConverter
    fun fromHourWeatherList(hourWeatherList: List<HourWeather>): String {
        return gson.toJson(hourWeatherList)
    }

    @TypeConverter
    fun toHourWeatherList(hourWeatherListString: String): List<HourWeather> {
        val listType = object : TypeToken<List<HourWeather>>() {}.type
        return gson.fromJson(hourWeatherListString, listType)
    }

    @TypeConverter
    fun fromStringList(stringList: List<String>): String {
        return gson.toJson(stringList)
    }

    @TypeConverter
    fun toStringList(stringListString: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(stringListString, listType)
    }
}
