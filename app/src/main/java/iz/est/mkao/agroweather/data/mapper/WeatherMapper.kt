package iz.est.mkao.agroweather.data.mapper

import iz.est.mkao.agroweather.data.model.*
import iz.est.mkao.agroweather.data.model.CurrentConditions
import iz.est.mkao.agroweather.data.model.DayWeather
import iz.est.mkao.agroweather.data.model.HourWeather
import iz.est.mkao.agroweather.data.model.WeatherResponse
import iz.est.mkao.agroweather.data.remote.dto.*

/**
 * Mappers to convert between Open-Meteo DTOs and domain models
 * Provides clean separation between API layer and domain layer
 */

fun OpenMeteoResponseDto.toDomain(): WeatherResponse {
    return WeatherResponse(
        queryCost = 0,
        latitude = latitude,
        longitude = longitude,
        resolvedAddress = "$latitude,$longitude",
        address = "$latitude,$longitude",
        timezone = timezone,
        tzoffset = utcOffsetSeconds / 3600.0,
        description = "Weather forecast from Open-Meteo",
        days = mapDailyData(),
        currentConditions = current?.toDomain() ?: createDefaultCurrentConditions(),
    )
}

fun OpenMeteoCurrentDto.toDomain(): CurrentConditions {
    return CurrentConditions(
        datetime = time,
        temp = temperature2m ?: 0.0,
        feelslike = apparentTemperature ?: temperature2m ?: 0.0,
        humidity = relativeHumidity2m ?: 0.0,
        dew = 0.0,
        precip = precipitation ?: 0.0,
        precipprob = 0.0,
        snow = snowfall ?: 0.0,
        snowdepth = 0.0,
        windspeed = windSpeed10m ?: 0.0,
        windgust = windGusts10m ?: windSpeed10m ?: 0.0,
        winddir = windDirection10m ?: 0.0,
        pressure = pressureMsl ?: surfacePressure ?: 0.0,
        visibility = 10.0,
        cloudcover = cloudCover ?: 0.0,
        solarradiation = 0.0,
        solarenergy = 0.0,
        uvindex = 0.0,
        conditions = mapWeatherCodeToConditions(weatherCode ?: 0),
        icon = mapWeatherCodeToIcon(weatherCode ?: 0),
        soilmoisture = null,
        soiltemp = null,
        evapotranspiration = null,
        potentialevapotranspiration = null,
    )
}

private fun OpenMeteoResponseDto.mapDailyData(): List<DayWeather> {
    val dailyData = daily ?: return emptyList()
    val hourlyData = hourly

    return dailyData.time.mapIndexed { index, dateString ->

        val dayHours = if (hourlyData != null) {
            mapHourlyDataForDay(hourlyData, dateString)
        } else {
            emptyList()
        }


        val minTemp = dailyData.temperature2mMin?.getOrNull(index) ?: 0.0
        val maxTemp = dailyData.temperature2mMax?.getOrNull(index) ?: 0.0
        val avgTemp = (minTemp + maxTemp) / 2


        val dailySoilMoisture = if (dayHours.isNotEmpty()) {
            val validSoilMoisture = dayHours.mapNotNull { it.soilMoisture }
            if (validSoilMoisture.isNotEmpty()) validSoilMoisture.average() else null
        } else null
        
        val dailySoilTemperature = if (dayHours.isNotEmpty()) {
            val validSoilTemp = dayHours.mapNotNull { it.soilTemperature }
            if (validSoilTemp.isNotEmpty()) validSoilTemp.average() else null
        } else null
        
        val dailyEvapotranspiration = if (dayHours.isNotEmpty()) {
            val validET = dayHours.mapNotNull { it.evapotranspiration }
            if (validET.isNotEmpty()) validET.sum() else null
        } else null

        DayWeather(
            datetime = dateString,
            temp = avgTemp,
            feelslike = (
                (dailyData.apparentTemperatureMin?.getOrNull(index) ?: avgTemp) +
                    (dailyData.apparentTemperatureMax?.getOrNull(index) ?: avgTemp)
                ) / 2,
            humidity = dayHours.firstOrNull()?.humidity ?: 60.0,
            dew = dayHours.firstOrNull()?.dew ?: (avgTemp * 0.6),
            precip = dailyData.precipitationSum?.getOrNull(index) ?: 0.0,
            precipprob = dailyData.precipitationProbabilityMax?.getOrNull(index) ?: 0.0,
            snow = dailyData.snowfallSum?.getOrNull(index) ?: 0.0,
            snowdepth = 0.0,
            windspeed = dailyData.windSpeed10mMax?.getOrNull(index) ?: 0.0,
            windgust = dailyData.windGusts10mMax?.getOrNull(index) ?: dailyData.windSpeed10mMax?.getOrNull(index) ?: 0.0,
            winddir = dailyData.windDirection10mDominant?.getOrNull(index) ?: 0.0,
            pressure = dayHours.firstOrNull()?.pressure ?: 1013.25,
            visibility = dayHours.firstOrNull()?.visibility ?: 10000.0,
            cloudcover = dayHours.firstOrNull()?.cloudcover ?: 0.0,
            solarradiation = dailyData.shortwaveRadiationSum?.getOrNull(index)?.div(24) ?: 0.0,
            solarenergy = dailyData.shortwaveRadiationSum?.getOrNull(index) ?: 0.0,
            uvindex = dailyData.uvIndexMax?.getOrNull(index) ?: 0.0,
            sunrise = dailyData.sunrise?.getOrNull(index) ?: "",
            sunset = dailyData.sunset?.getOrNull(index) ?: "",
            conditions = mapWeatherCodeToConditions(dailyData.weatherCode?.getOrNull(index) ?: 0),
            description = mapWeatherCodeToDescription(dailyData.weatherCode?.getOrNull(index) ?: 0),
            icon = mapWeatherCodeToIcon(dailyData.weatherCode?.getOrNull(index) ?: 0),
            hours = dayHours,
            soilMoisture = dailySoilMoisture,
            soilTemperature = dailySoilTemperature,
            evapotranspiration = dailyEvapotranspiration,
            potentialEvapotranspiration = null,
            sunshineDuration = dailyData.sunshineDuration?.getOrNull(index),
        )
    }
}

private fun mapHourlyDataForDay(hourlyData: OpenMeteoHourlyDto, dayString: String): List<HourWeather> {
    return hourlyData.time.mapIndexedNotNull { index, timeString ->
        if (timeString.startsWith(dayString)) {
            HourWeather(
                datetime = timeString,
                temp = hourlyData.temperature2m?.getOrNull(index) ?: 0.0,
                feelslike = hourlyData.temperature2m?.getOrNull(index) ?: 0.0,
                humidity = hourlyData.relativeHumidity2m?.getOrNull(index) ?: 0.0,
                dew = hourlyData.dewPoint2m?.getOrNull(index) ?: 0.0,
                precip = (hourlyData.rain?.getOrNull(index) ?: 0.0) + (hourlyData.showers?.getOrNull(index) ?: 0.0),
                precipprob = hourlyData.precipitationProbability?.getOrNull(index) ?: 0.0,
                snow = hourlyData.snowfall?.getOrNull(index) ?: 0.0,
                snowdepth = hourlyData.snowDepth?.getOrNull(index) ?: 0.0,
                windspeed = hourlyData.windSpeed10m?.getOrNull(index) ?: 0.0,
                windgust = hourlyData.windGusts10m?.getOrNull(index) ?: hourlyData.windSpeed10m?.getOrNull(index) ?: 0.0,
                winddir = hourlyData.windDirection10m?.getOrNull(index) ?: 0.0,
                pressure = hourlyData.pressureMsl?.getOrNull(index) ?: hourlyData.surfacePressure?.getOrNull(index) ?: 0.0,
                visibility = hourlyData.visibility?.getOrNull(index) ?: 10000.0,
                cloudcover = hourlyData.cloudCover?.getOrNull(index) ?: 0.0,
                solarradiation = hourlyData.directRadiation?.getOrNull(index) ?: 0.0,
                solarenergy = hourlyData.directRadiation?.getOrNull(index) ?: 0.0,
                uvindex = hourlyData.uvIndex?.getOrNull(index) ?: 0.0,
                conditions = mapWeatherCodeToConditions(hourlyData.weatherCode?.getOrNull(index) ?: 0),
                icon = mapWeatherCodeToIcon(hourlyData.weatherCode?.getOrNull(index) ?: 0),
                soilMoisture = hourlyData.soilMoisture0To1cm?.getOrNull(index),
                soilTemperature = hourlyData.soilTemperature0cm?.getOrNull(index),
                evapotranspiration = hourlyData.evapotranspiration?.getOrNull(index),
                potentialEvapotranspiration = null,
            )
        } else {
            null
        }
    }
}

/**
 * Maps Open-Meteo weather codes to human-readable conditions
 * Based on WMO weather interpretation codes
 */
private fun mapWeatherCodeToConditions(code: Int): String {
    return when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers"
        85, 86 -> "Snow showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm with hail"
        else -> "Unknown"
    }
}

/**
 * Maps Open-Meteo weather codes to icon names
 */
private fun mapWeatherCodeToIcon(code: Int): String {
    return when (code) {
        0 -> "clear-day"
        1, 2, 3 -> "partly-cloudy-day"
        45, 48 -> "fog"
        51, 53, 55, 56, 57 -> "drizzle"
        61, 63, 65, 66, 67 -> "rain"
        71, 73, 75, 77 -> "snow"
        80, 81, 82 -> "showers-day"
        85, 86 -> "snow-showers-day"
        95, 96, 99 -> "thunderstorm"
        else -> "unknown"
    }
}

/**
 * Maps Open-Meteo weather codes to detailed descriptions
 */
private fun mapWeatherCodeToDescription(code: Int): String {
    return when (code) {
        0 -> "Clear sky with no clouds"
        1 -> "Mainly clear with few clouds"
        2 -> "Partly cloudy"
        3 -> "Overcast with many clouds"
        45 -> "Fog reducing visibility"
        48 -> "Depositing rime fog"
        51 -> "Light drizzle"
        53 -> "Moderate drizzle"
        55 -> "Dense drizzle"
        56 -> "Light freezing drizzle"
        57 -> "Dense freezing drizzle"
        61 -> "Slight rain"
        63 -> "Moderate rain"
        65 -> "Heavy rain"
        66 -> "Light freezing rain"
        67 -> "Heavy freezing rain"
        71 -> "Slight snow fall"
        73 -> "Moderate snow fall"
        75 -> "Heavy snow fall"
        77 -> "Snow grains"
        80 -> "Slight rain showers"
        81 -> "Moderate rain showers"
        82 -> "Violent rain showers"
        85 -> "Slight snow showers"
        86 -> "Heavy snow showers"
        95 -> "Thunderstorm"
        96 -> "Thunderstorm with slight hail"
        99 -> "Thunderstorm with heavy hail"
        else -> "Weather conditions unknown"
    }
}

/**
 * Creates a default CurrentConditions when API doesn't provide current conditions
 */
private fun createDefaultCurrentConditions(): CurrentConditions {
    return CurrentConditions(
        datetime = "",
        temp = 0.0,
        feelslike = 0.0,
        humidity = 0.0,
        dew = 0.0,
        precip = 0.0,
        precipprob = 0.0,
        snow = 0.0,
        snowdepth = 0.0,
        windspeed = 0.0,
        windgust = 0.0,
        winddir = 0.0,
        pressure = 0.0,
        visibility = 0.0,
        cloudcover = 0.0,
        solarradiation = 0.0,
        solarenergy = 0.0,
        uvindex = 0.0,
        conditions = "Unknown",
        icon = "unknown",
        soilmoisture = null,
        soiltemp = null,
        evapotranspiration = null,
        potentialevapotranspiration = null,
    )
}


