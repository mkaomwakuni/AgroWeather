package iz.est.mkao.agroweather.data.service

import iz.est.mkao.agroweather.data.model.DayWeather
import iz.est.mkao.agroweather.data.model.IrrigationParameters
import iz.est.mkao.agroweather.data.model.IrrigationRecommendation
import iz.est.mkao.agroweather.data.model.WeatherResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Service for analyzing weather data and providing irrigation recommendations
 */
class IrrigationService {

    /**
     * Analyze weather data and determine the best days for irrigation
     * @param weatherData The weather data to analyze
     * @param parameters Custom irrigation parameters (optional)
     * @return List of irrigation recommendations sorted by score (best days first)
     */
    fun analyzeBestDaysToIrrigate(
        weatherData: WeatherResponse,
        parameters: IrrigationParameters = IrrigationParameters(),
    ): List<IrrigationRecommendation> {
        val recommendations = mutableListOf<IrrigationRecommendation>()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Limit to the number of days to consider from parameters
        val daysToAnalyze = weatherData.days.take(parameters.daysToConsider)

        for (day in daysToAnalyze) {
            // Skip days that already have significant precipitation
            if (day.precip > 5.0) {
                continue
            }

            val date = LocalDate.parse(day.datetime, dateFormatter)



            // 1. Soil Moisture Component (0.0-1.0)
            // Lower soil moisture means higher irrigation need
            val soilMoistureScore = day.soilMoisture?.let {
                calculateSoilMoistureScore(it, parameters.minSoilMoisture)
            } ?: 0.5f // Default if no soil moisture data

            // 2. Precipitation Probability Component (0.0-1.0)
            // Lower probability means better for irrigation
            val precipProbScore = calculatePrecipProbabilityScore(
                day.precipprob,
                parameters.maxPrecipProbability,
            )

            // 3. Wind Component (0.0-1.0)
            // Lower wind speed is better for irrigation
            val windScore = calculateWindScore(day.windspeed, parameters.maxWindSpeed)

            // 4. Temperature Component (0.0-1.0)
            // Temperature within optimal range is better
            val tempScore = calculateTemperatureScore(
                day.temp,
                parameters.optimalTemperatureRange.first,
                parameters.optimalTemperatureRange.second,
            )


            val overallScore = calculateOverallScore(
                soilMoistureScore = soilMoistureScore,
                precipProbScore = precipProbScore,
                windScore = windScore,
                tempScore = tempScore,
            )

            // Generate recommendation reason
            val reason = generateRecommendationReason(
                day,
                overallScore,
                soilMoistureScore,
                precipProbScore,
                windScore,
                tempScore,
            )

            // Create recommendation object
            val recommendation = IrrigationRecommendation(
                date = date,
                score = overallScore,
                soilMoisture = day.soilMoisture,
                soilTemperature = day.soilTemperature,
                precipitation = day.precip,
                precipitationProbability = day.precipprob,
                evapotranspiration = day.evapotranspiration,
                temperature = day.temp,
                humidity = day.humidity,
                windSpeed = day.windspeed,
                conditions = day.conditions,
                recommendationReason = reason,
            )

            recommendations.add(recommendation)
        }

        // Sort by score in descending order (best days first)
        return recommendations.sortedByDescending { it.score }
    }

    /**
     * Calculate soil moisture score component
     */
    private fun calculateSoilMoistureScore(
        soilMoisture: Double,
        minThreshold: Double,
    ): Float {
        // Normalize to 0-1 range where 0 = minThreshold and 1 = 0.0
        // Lower soil moisture means higher irrigation need
        return if (soilMoisture <= minThreshold) {
            1.0f // Maximum score (highest need)
        } else {
            // Linear interpolation between minThreshold and 1.0
            ((1.0 - ((soilMoisture - minThreshold) / (1.0 - minThreshold))).toFloat()).coerceIn(0.0f, 1.0f)
        }
    }

    /**
     * Calculate precipitation probability score component
     */
    private fun calculatePrecipProbabilityScore(
        precipProbability: Double,
        maxThreshold: Double,
    ): Float {
        // Normalize to 0-1 range where 0 = maxThreshold and 1 = 0%
        // Lower precipitation probability means better for irrigation
        return if (precipProbability >= maxThreshold) {
            0.0f // Minimum score (not good for irrigation)
        } else {
            // Linear interpolation between 0 and maxThreshold
            ((1.0 - (precipProbability / maxThreshold)).toFloat()).coerceIn(0.0f, 1.0f)
        }
    }

    /**
     * Calculate wind score component
     */
    private fun calculateWindScore(
        windSpeed: Double,
        maxThreshold: Double,
    ): Float {
        // Normalize to 0-1 range where 0 = maxThreshold and 1 = 0
        // Lower wind speed is better for irrigation
        return if (windSpeed >= maxThreshold) {
            0.0f // Minimum score (not good for irrigation)
        } else {
            // Linear interpolation between 0 and maxThreshold
            (1.0 - (windSpeed / maxThreshold)).toFloat().coerceIn(0.0f, 1.0f)
        }
    }

    /**
     * Calculate temperature score component
     */
    private fun calculateTemperatureScore(
        temperature: Double,
        minOptimal: Double,
        maxOptimal: Double,
    ): Float {
        // Normalize to 0-1 range where 1 = within optimal range
        return when {
            temperature in minOptimal..maxOptimal -> 1.0f
            temperature < minOptimal -> {
                // Linear decrease below minimum optimal
                (0.5 + (temperature / (2 * minOptimal))).toFloat().coerceIn(0.0f, 1.0f)
            }
            else -> { // temperature > maxOptimal
                // Linear decrease above maximum optimal
                (1.0 - ((temperature - maxOptimal) / maxOptimal) * 0.5).toFloat().coerceIn(0.0f, 1.0f)
            }
        }
    }

    /**
     * Calculate overall irrigation score using weighted components
     */
    private fun calculateOverallScore(
        soilMoistureScore: Float,
        precipProbScore: Float,
        windScore: Float,
        tempScore: Float,
    ): Float {
        // Weighted average of components
        // Soil moisture and precipitation probability are most important
        val totalScore = (soilMoistureScore * 0.4f) +
            (precipProbScore * 0.3f) +
            (windScore * 0.2f) +
            (tempScore * 0.1f)

        return totalScore.coerceIn(0.0f, 1.0f)
    }

    /**
     * Generate a human-readable recommendation reason
     */
    private fun generateRecommendationReason(
        day: DayWeather,
        overallScore: Float,
        soilMoistureScore: Float,
        precipProbScore: Float,
        windScore: Float,
        tempScore: Float,
    ): String {
        val sb = StringBuilder()

        when {
            overallScore >= 0.8 -> sb.append("Highly recommended for irrigation. ")
            overallScore >= 0.6 -> sb.append("Good conditions for irrigation. ")
            overallScore >= 0.4 -> sb.append("Moderate conditions for irrigation. ")
            else -> sb.append("Not recommended for irrigation. ")
        }

        // Add specific details
        if (soilMoistureScore > 0.7) {
            sb.append("Soil moisture is low. ")
        }

        if (precipProbScore < 0.3) {
            sb.append("High chance of rain. ")
        } else {
            sb.append("Low chance of rain. ")
        }

        if (windScore < 0.5) {
            sb.append("Wind conditions may affect irrigation effectiveness. ")
        }

        if (tempScore < 0.5) {
            sb.append("Temperature conditions are not optimal. ")
        }

        return sb.toString().trim()
    }
}
