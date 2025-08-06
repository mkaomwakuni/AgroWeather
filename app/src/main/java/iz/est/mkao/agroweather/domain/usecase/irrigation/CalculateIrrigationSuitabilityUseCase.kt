package iz.est.mkao.agroweather.domain.usecase.irrigation

import iz.est.mkao.agroweather.data.model.DayWeather
import iz.est.mkao.agroweather.domain.model.IrrigationSuitability
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class CalculateIrrigationSuitabilityUseCase @Inject constructor() {

    companion object {
        // Soil Temperature thresholds (°C)
        private const val WARM_SEASON_CROP_MIN_SOIL_TEMP = 15.0 // 60°F
        private const val COOL_SEASON_CROP_MIN_SOIL_TEMP = 4.0  // 40°F
        private const val FROZEN_SOIL_THRESHOLD = 0.0
        
        // Soil Moisture thresholds (as percentage of field capacity)
        private const val SANDY_SOIL_IRRIGATE_THRESHOLD = 0.5   
        private const val LOAMY_SOIL_IRRIGATE_THRESHOLD = 0.35  
        private const val CLAY_SOIL_IRRIGATE_THRESHOLD = 0.45   
        private const val WATERLOGGED_THRESHOLD = 0.8
        private const val DROUGHT_STRESS_THRESHOLD = 0.2
        
        // Environmental conditions
        private const val HIGH_PRECIPITATION_THRESHOLD = 0.5    
        private const val MAX_WIND_SPEED_FOR_IRRIGATION = 25.0  
        private const val HIGH_HUMIDITY_THRESHOLD = 80.0        
        private const val HEAT_STRESS_TEMPERATURE = 35.0        
        private const val COLD_STRESS_TEMPERATURE = 5.0
        
        // Default values when data not available - more realistic for varying conditions
        private const val DEFAULT_SOIL_MOISTURE = 0.35  // 35% - closer to irrigation threshold
        private const val DEFAULT_SOIL_TEMPERATURE = 18.0
        private const val DEFAULT_EVAPOTRANSPIRATION = 3.5 // Realistic daily ET
    }

    data class CropType(val isWarmSeason: Boolean = true)

    operator fun invoke(
        day: DayWeather,
        cropType: CropType = CropType(true),
        soilType: SoilType = SoilType.LOAMY,
        upcomingRainMm: Double = 0.0
    ): IrrigationSuitability {
        
        val soilMoisture = day.soilMoisture ?: DEFAULT_SOIL_MOISTURE
        val soilTemp = day.soilTemperature ?: (day.temp - 2.0)
        val precipProb = day.precipprob
        val windSpeed = day.windspeed
        val airTemp = day.temp
        val humidity = day.humidity ?: 60.0
        val precipitation = day.precip
        
        // Calculate irrigation suitability
        val analysis = analyzeIrrigationConditions(
            soilMoisture = soilMoisture,
            soilTemp = soilTemp,
            airTemp = airTemp,
            precipitation = precipitation,
            precipProb = precipProb,
            windSpeed = windSpeed,
            humidity = humidity,
            upcomingRainMm = upcomingRainMm,
            cropType = cropType,
            soilType = soilType,
            evapotranspiration = day.evapotranspiration ?: DEFAULT_EVAPOTRANSPIRATION // Use actual ET from API or realistic default
        )

        return IrrigationSuitability(
            isSuitable = analysis.isSuitable,
            soilMoisture = soilMoisture,
            precipitationProbability = precipProb,
            windSpeed = windSpeed,
            temperature = airTemp,
            recommendations = analysis.recommendations,
        )
    }
    
    private fun analyzeIrrigationConditions(
        soilMoisture: Double,
        soilTemp: Double,
        airTemp: Double,
        precipitation: Double,
        precipProb: Double,
        windSpeed: Double,
        humidity: Double,
        upcomingRainMm: Double,
        cropType: CropType,
        soilType: SoilType,
        evapotranspiration: Double = DEFAULT_EVAPOTRANSPIRATION
    ): IrrigationAnalysis {
        
        val recommendations = mutableListOf<String>()
        var irrigationScore = 10.0
        var priority = "Monitor"
        
        // Evapotranspiration is now passed as a parameter (defaulted to 4.5mm/day)
        
        // UPDATED: More practical irrigation recommendation algorithm
        // Primary conditions: Soil Moisture < 40% AND No significant rain in forecast (next 24h) AND ET > 3mm/day
        // Secondary conditions: Consider soil moisture alone if very low, or ET alone if very high
        val soilMoisturePercent = soilMoisture * 100
        val isLowSoilMoisture = soilMoisturePercent < 40.0
        val isVeryLowSoilMoisture = soilMoisturePercent < 25.0
        val isNoRainForecast = precipProb < 30.0 && upcomingRainMm < 2.0
        val isHighEvapotranspiration = evapotranspiration > 3.0
        val isVeryHighEvapotranspiration = evapotranspiration > 6.0
        
        // More flexible algorithm: Primary condition OR critical secondary conditions
        val shouldIrrigateByAlgorithm = (isLowSoilMoisture && isNoRainForecast && isHighEvapotranspiration) ||
                isVeryLowSoilMoisture || // Critical soil moisture regardless of other factors
                (isVeryHighEvapotranspiration && isNoRainForecast) // Very high ET with no rain
        
        if (shouldIrrigateByAlgorithm) {
            when {
                isVeryLowSoilMoisture -> {
                    recommendations.add("🚨 URGENT IRRIGATION NEEDED - Critical soil moisture:")
                    recommendations.add("💧 Very low soil moisture (${soilMoisturePercent.roundToInt()}% < 25%)")
                    priority = "Urgent"
                }
                isVeryHighEvapotranspiration && isNoRainForecast -> {
                    recommendations.add("🌱 IRRIGATION RECOMMENDED - High water demand:")
                    recommendations.add("🌱 Very high evapotranspiration (${evapotranspiration.roundToInt()} mm/day > 6mm)")
                    recommendations.add("☀️ No significant rain expected (${precipProb.roundToInt()}% chance)")
                    priority = "High"
                }
                else -> {
                    recommendations.add("💧 IRRIGATION RECOMMENDED based on conditions:")
                    recommendations.add("💧 Soil moisture: ${soilMoisturePercent.roundToInt()}% (< 40%)")
                    recommendations.add("☀️ Rain probability: ${precipProb.roundToInt()}% (< 30%)")
                    recommendations.add("🌱 Evapotranspiration: ${evapotranspiration.roundToInt()} mm/day (> 3mm)")
                    priority = "Recommended"
                }
            }
            irrigationScore = 10.0  // Maximum score
        } else {
            // Explain why irrigation is not needed
            recommendations.add("✅ SKIP IRRIGATION - Conditions don't require watering:")
            
            if (soilMoisturePercent >= 40.0) {
                recommendations.add("💧 Good soil moisture (${soilMoisturePercent.roundToInt()}% >= 40%)")
                irrigationScore -= 2.0
            }
            
            if (precipProb >= 30.0 || upcomingRainMm >= 2.0) {
                recommendations.add("🌧️ Rain expected (${precipProb.roundToInt()}% chance, ${upcomingRainMm.roundToInt()}mm)")
                irrigationScore -= 2.0
            }
            
            if (evapotranspiration <= 3.0) {
                recommendations.add("🌱 Low water demand (ET: ${evapotranspiration.roundToInt()} mm/day <= 3mm)")
                irrigationScore -= 2.0
            }
            
            priority = "Skip"
        }
        
        // Check soil temperature suitability (additional information)
        val minSoilTemp = if (cropType.isWarmSeason) WARM_SEASON_CROP_MIN_SOIL_TEMP else COOL_SEASON_CROP_MIN_SOIL_TEMP
        
        when {
            soilTemp <= FROZEN_SOIL_THRESHOLD -> {
                recommendations.add("❄️ Soil frozen (${soilTemp.roundToInt()}°C) - Avoid irrigation to prevent waterlogging")
                if (!shouldIrrigateByAlgorithm) irrigationScore -= 5.0
            }
            soilTemp < minSoilTemp -> {
                val cropSeason = if (cropType.isWarmSeason) "warm-season" else "cool-season"
                recommendations.add("🌡️ Soil cold (${soilTemp.roundToInt()}°C) for $cropSeason crops - Limited root activity")
                if (!shouldIrrigateByAlgorithm) irrigationScore -= 2.0
            }
            else -> {
                recommendations.add("✅ Soil temperature suitable (${soilTemp.roundToInt()}°C)")
            }
        }
        
        // Check wind conditions (additional information)
        when {
            windSpeed > 35.0 -> { 
                recommendations.add("💨 Very windy (${windSpeed.roundToInt()} km/h) - Use drip irrigation if irrigating")
                if (!shouldIrrigateByAlgorithm) irrigationScore -= 2.0
            }
            windSpeed > MAX_WIND_SPEED_FOR_IRRIGATION -> {
                recommendations.add("💨 Windy (${windSpeed.roundToInt()} km/h) - Reduce sprinkler pressure if irrigating")
                if (!shouldIrrigateByAlgorithm) irrigationScore -= 1.0
            }
        }
        
        // Add practical timing recommendations if irrigation is needed
        if (shouldIrrigateByAlgorithm || irrigationScore >= 5.0) {
            recommendations.add("⏰ Best time: Early morning (6-8 AM) or evening (6-8 PM)")
            
            val soilTypeAdvice = when (soilType) {
                SoilType.SANDY -> "Light, frequent watering (every 2-3 days)"
                SoilType.LOAMY -> "Moderate watering (every 3-4 days), 1-1.5 inches deep"
                SoilType.CLAY -> "Deep, infrequent watering (every 5-6 days)"
            }
            recommendations.add("💧 Method: $soilTypeAdvice")
        }
        
        // Final decision is determined by the algorithm, not the score
        return IrrigationAnalysis(shouldIrrigateByAlgorithm, priority, recommendations)
    }
    
    private fun getTensionAdvice(tensionKpa: Int): String {
        return when {
            tensionKpa < 10 -> "(too wet)"
            tensionKpa in 10..30 -> "(optimal range)"
            tensionKpa > 30 -> "(needs irrigation)"
            else -> ""
        }
    }
    
    enum class SoilType {
        SANDY, LOAMY, CLAY
    }
    
    private data class IrrigationAnalysis(
        val isSuitable: Boolean,
        val priority: String,
        val recommendations: List<String>
    )
}
