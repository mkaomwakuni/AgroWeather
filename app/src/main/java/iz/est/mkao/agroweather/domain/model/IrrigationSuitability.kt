package iz.est.mkao.agroweather.domain.model

data class IrrigationSuitability(
    val isSuitable: Boolean,
    val soilMoisture: Double,
    val precipitationProbability: Double,
    val windSpeed: Double,
    val temperature: Double,
    val recommendations: List<String>,
)
