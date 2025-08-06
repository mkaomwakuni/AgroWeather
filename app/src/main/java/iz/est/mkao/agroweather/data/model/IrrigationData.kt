package iz.est.mkao.agroweather.data.model

import java.time.LocalDate

/**
 * Represents an irrigation recommendation for a specific day
 */
data class IrrigationRecommendation(
    val date: LocalDate,
    val score: Float,
    val soilMoisture: Double?,
    val soilTemperature: Double?,
    val precipitation: Double,
    val precipitationProbability: Double,
    val evapotranspiration: Double?,
    val temperature: Double,
    val humidity: Double,
    val windSpeed: Double,
    val conditions: String,
    val recommendationReason: String,
)

/**
 * Irrigation suitability parameters that can be customized by the user
 */
data class IrrigationParameters(
    val minSoilMoisture: Double = 0.2, 
    val maxPrecipProbability: Double = 20.0,
    val maxWindSpeed: Double = 15.0, 
    val optimalTemperatureRange: Pair<Double, Double> = Pair(10.0, 30.0), 
    val daysToConsider: Int = 7,
)
