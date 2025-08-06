package iz.est.mkao.agroweather.domain.usecase.weather

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import iz.est.mkao.agroweather.domain.model.WeatherGradientType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GetWeatherGradientUseCase @Inject constructor() {

    private val colorPools = mapOf(
        WeatherGradientType.SUNNY_HOT to listOf(
            // Hot sun variations
            listOf(Color(0xFFFFD700), Color(0xFFFF8C00), Color(0xFFFF6B35), Color(0x33FFFFFF)),
            listOf(Color(0xFFFFA500), Color(0xFFFF4500), Color(0xFFDC143C), Color(0x33FFFFFF)),
            listOf(Color(0xFFFFD700), Color(0xFFFFB347), Color(0xFFFF7F50), Color(0x33FFFFFF)),
            listOf(Color(0xFFFDB462), Color(0xFFE78B3E), Color(0xFFBD5B34), Color(0x33FFFFFF)),
        ),
        WeatherGradientType.CLEAR_COOL to listOf(
            // Clear sky variations
            listOf(Color(0xFF87CEEB), Color(0xFF4682B4), Color(0xFF1E88E5), Color(0x33FFFFFF)),
            listOf(Color(0xFF87CEFA), Color(0xFF6495ED), Color(0xFF4169E1), Color(0x33FFFFFF)),
            listOf(Color(0xFFADD8E6), Color(0xFF5F9EA0), Color(0xFF008B8B), Color(0x33FFFFFF)),
            listOf(Color(0xFF98D8E8), Color(0xFF5B9BD5), Color(0xFF2E86AB), Color(0x33FFFFFF)),
        ),
        WeatherGradientType.RAINY to listOf(
            // Rainy day variations
            listOf(Color(0xFF708090), Color(0xFF4682B4), Color(0xFF2F4F4F), Color(0x4DFFFFFF)),
            listOf(Color(0xFF696969), Color(0xFF4A5568), Color(0xFF2D3748), Color(0x4DFFFFFF)),
            listOf(Color(0xFF778899), Color(0xFF36454F), Color(0xFF263238), Color(0x4DFFFFFF)),
            listOf(Color(0xFF7393B3), Color(0xFF536878), Color(0xFF36454F), Color(0x4DFFFFFF)),
        ),
        WeatherGradientType.CLOUDY to listOf(
            // Cloudy variations
            listOf(Color(0xFFB0C4DE), Color(0xFF778899), Color(0xFF696969), Color(0x4DFFFFFF)),
            listOf(Color(0xFFC0C0C0), Color(0xFF9099A2), Color(0xFF5D6D7E), Color(0x4DFFFFFF)),
            listOf(Color(0xFFD3D3D3), Color(0xFFA9A9A9), Color(0xFF808080), Color(0x4DFFFFFF)),
            listOf(Color(0xFFE5E4E2), Color(0xFFBDBDBD), Color(0xFF757575), Color(0x4DFFFFFF)),
        ),
        WeatherGradientType.PARTLY_CLOUDY to listOf(
            // Partly cloudy variations
            listOf(Color(0xFF87CEFA), Color(0xFF4A90E2), Color(0xFF1976D2), Color(0x33FFFFFF)),
            listOf(Color(0xFF98D8E8), Color(0xFF6BB6FF), Color(0xFF1E90FF), Color(0x33FFFFFF)),
            listOf(Color(0xFFB0E0E6), Color(0xFF7FB3D3), Color(0xFF4682B4), Color(0x33FFFFFF)),
            listOf(Color(0xFFAFEEEE), Color(0xFF87CEEB), Color(0xFF4682B4), Color(0x33FFFFFF)),
        ),
        WeatherGradientType.NIGHT to listOf(
            // Night variations
            listOf(Color(0xFF191970), Color(0xFF483D8B), Color(0xFF2F2F4F), Color(0x66FFFFFF)),
            listOf(Color(0xFF000080), Color(0xFF4B0082), Color(0xFF301934), Color(0x66FFFFFF)),
            listOf(Color(0xFF1C1C2E), Color(0xFF2A2A4A), Color(0xFF3A3A5C), Color(0x66FFFFFF)),
            listOf(Color(0xFF0B1426), Color(0xFF1A2332), Color(0xFF2D3A52), Color(0x66FFFFFF)),
        ),
        WeatherGradientType.STORMY to listOf(
            // Stormy variations
            listOf(Color(0xFF2F2F2F), Color(0xFF4B0082), Color(0xFF8B008B), Color(0x66FFFFFF)),
            listOf(Color(0xFF36013F), Color(0xFF800080), Color(0xFF663399), Color(0x66FFFFFF)),
            listOf(Color(0xFF1C1C1C), Color(0xFF483248), Color(0xFF654321), Color(0x66FFFFFF)),
            listOf(Color(0xFF2C3E50), Color(0xFF8E44AD), Color(0xFF9B59B6), Color(0x66FFFFFF)),
        ),
        WeatherGradientType.SNOWY to listOf(
            // Snowy variations
            listOf(Color(0xFFF0F8FF), Color(0xFFE6E6FA), Color(0xFFD3D3D3), Color(0x33FFFFFF)),
            listOf(Color(0xFFF8F8FF), Color(0xFFDCDCDC), Color(0xFFC0C0C0), Color(0x33FFFFFF)),
            listOf(Color(0xFFE0F6FF), Color(0xFFB8D4E3), Color(0xFF87A7CA), Color(0x33FFFFFF)),
            listOf(Color(0xFFF5F5F5), Color(0xFFE8E8E8), Color(0xFFBDBDBD), Color(0x33FFFFFF)),
        ),
        WeatherGradientType.DEFAULT to listOf(
            // Default variations
            listOf(Color(0xFF4A90E2), Color(0xFF7BB3F7), Color(0x33FFFFFF)),
            listOf(Color(0xFF6BA6CD), Color(0xFF9BCDFF), Color(0x33FFFFFF)),
            listOf(Color(0xFF5DADE2), Color(0xFF85C1E9), Color(0x33FFFFFF)),
        )
    )

    operator fun invoke(iconCode: String, temperature: Double): Brush {
        val gradientType = determineGradientType(iconCode, temperature)
        return createDynamicGradient(gradientType, temperature)
    }

    private fun createDynamicGradient(type: WeatherGradientType, temperature: Double): Brush {
        val colorOptions = colorPools[type] ?: colorPools[WeatherGradientType.DEFAULT]!!
        
        // Add temperature-based variation
        val selectedColors = when {
            temperature > 30 -> {
                // Very hot - prefer warmer variations
                val hotVariations = colorOptions.filter { colors ->
                    colors.any { it.red > 0.7f || (it.red > 0.5f && it.green > 0.5f && it.blue < 0.5f) }
                }
                hotVariations.takeIf { it.isNotEmpty() } ?: colorOptions
            }
            temperature < 5 -> {
                // Very cold - prefer cooler variations
                val coldVariations = colorOptions.filter { colors ->
                    colors.any { it.blue > 0.6f || (it.red < 0.4f && it.green < 0.4f) }
                }
                coldVariations.takeIf { it.isNotEmpty() } ?: colorOptions
            }
            else -> colorOptions
        }
        
        // Select a random variation from appropriate options
        val chosenColors = selectedColors.random()
        
        return Brush.verticalGradient(colors = chosenColors)
    }

    private fun determineGradientType(iconCode: String, temperature: Double): WeatherGradientType {
        return when {
            iconCode.contains("clear") && temperature > 25 -> WeatherGradientType.SUNNY_HOT
            iconCode.contains("clear") -> WeatherGradientType.CLEAR_COOL
            iconCode.contains("rain") || iconCode.contains("showers") -> WeatherGradientType.RAINY
            iconCode.contains("cloudy") || iconCode.contains("overcast") -> WeatherGradientType.CLOUDY
            iconCode.contains("partly") -> WeatherGradientType.PARTLY_CLOUDY
            iconCode.contains("night") -> WeatherGradientType.NIGHT
            iconCode.contains("thunder") || iconCode.contains("storm") -> WeatherGradientType.STORMY
            iconCode.contains("snow") -> WeatherGradientType.SNOWY
            else -> WeatherGradientType.DEFAULT
        }
    }


}
