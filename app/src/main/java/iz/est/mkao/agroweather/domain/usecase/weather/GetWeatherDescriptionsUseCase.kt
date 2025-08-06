package iz.est.mkao.agroweather.domain.usecase.weather

import iz.est.mkao.agroweather.domain.model.WeatherDescription
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetWeatherDescriptionsUseCase @Inject constructor() {

    fun getHumidityDescription(humidity: Double): WeatherDescription {
        return when {
            humidity < 30 -> WeatherDescription("Low", "Dry conditions")
            humidity < 60 -> WeatherDescription("Moderate", "Comfortable")
            humidity < 80 -> WeatherDescription("High", "Humid conditions")
            else -> WeatherDescription("Very High", "Oppressive")
        }
    }

    fun getWindDirection(degrees: Double): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = ((degrees + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }

    fun getPressureDescription(pressure: Double): WeatherDescription {
        return when {
            pressure < 1000 -> WeatherDescription("Low", "Stormy weather")
            pressure < 1020 -> WeatherDescription("Normal", "Stable weather")
            else -> WeatherDescription("High", "Clear skies")
        }
    }

    fun getUVDescription(uvIndex: Double): WeatherDescription {
        return when {
            uvIndex <= 2 -> WeatherDescription("Low", "Safe")
            uvIndex <= 5 -> WeatherDescription("Moderate", "Protection advised")
            uvIndex <= 7 -> WeatherDescription("High", "Protection required")
            uvIndex <= 10 -> WeatherDescription("Very High", "Stay in shade")
            else -> WeatherDescription("Extreme", "Avoid sun exposure")
        }
    }

    fun getCloudDescription(cloudCover: Double): String {
        return when {
            cloudCover < 10 -> "Clear skies"
            cloudCover < 25 -> "Mostly clear"
            cloudCover < 50 -> "Partly cloudy"
            cloudCover < 75 -> "Mostly cloudy"
            else -> "Overcast"
        }
    }

    fun getSoilMoistureDescription(soilMoisture: Double): WeatherDescription {
        return when {
            soilMoisture < 0.2 -> WeatherDescription("Very dry", "Irrigation needed")
            soilMoisture < 0.4 -> WeatherDescription("Dry", "Consider watering")
            soilMoisture < 0.7 -> WeatherDescription("Optimal", "Good for crops")
            soilMoisture < 0.9 -> WeatherDescription("Wet", "Monitor drainage")
            else -> WeatherDescription("Saturated", "Risk of waterlogging")
        }
    }

    fun getSoilTempDescription(soilTemp: Double): String {
        return when {
            soilTemp < 5 -> "Too cold for planting"
            soilTemp < 10 -> "Cool - Limited growth"
            soilTemp < 20 -> "Optimal for cool crops"
            soilTemp < 30 -> "Optimal for warm crops"
            else -> "Hot - May stress plants"
        }
    }

    fun getVisibilityDescription(visibility: Double): String {
        return when {
            visibility < 1 -> "Very poor - Dense fog"
            visibility < 5 -> "Poor - Light fog"
            visibility < 10 -> "Moderate - Light haze"
            else -> "Excellent - Clear air"
        }
    }

    fun getDewPointDescription(dewPoint: Double, temperature: Double): String {
        val spread = temperature - dewPoint
        return when {
            spread < 2 -> "Fog/mist likely"
            spread < 5 -> "High moisture"
            spread < 10 -> "Moderate moisture"
            else -> "Dry air"
        }
    }

    fun getSolarDescription(solarRadiation: Double): String {
        return when {
            solarRadiation < 200 -> "Low solar energy"
            solarRadiation < 500 -> "Moderate solar energy"
            solarRadiation < 800 -> "High solar energy"
            else -> "Peak solar energy"
        }
    }

    fun getSolarEnergyDescription(solarEnergy: Double): String {
        return when {
            solarEnergy < 5 -> "Very low - Overcast conditions"
            solarEnergy < 15 -> "Low - Cloudy conditions"
            solarEnergy < 25 -> "Moderate - Partly cloudy"
            solarEnergy < 35 -> "High - Good solar conditions"
            else -> "Excellent - Peak solar energy"
        }
    }
}
